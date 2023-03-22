package ed.inf.adbs.minibase.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * SumOperator is the operator that implements the sum aggregate.
 * We first use ProjectOperator or JoinOperator to get all the tuples.
 * Then we use a HashSet to store the group of tuples.
 * We use a List to store the output after sum.
 */
public class SumOperator extends Operator {

    private final Operator child;
    private final List<Tuple> output = new ArrayList<>();
    private final List<Tuple> outputAfterSum = new ArrayList<>(); // output after sum
    private final HashSet<Tuple> outputGroup = new HashSet<>(); // To store the group of tuples
    private Integer outputIndex = 0; // Index of the output, used for getNextTuple()

    /**
     * Constructs a SumOperator.
     * @param dbPath the path to the database
     * @param query  the query
     */
    public SumOperator(String dbPath, Query query) throws IOException {
        Integer numOfRelationAtoms = 0;
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                numOfRelationAtoms++;
            }
        }
        // If there is only one relation atom, use ProjectOperator
        // Do nonDistinctGetNextTuple() to get all the tuples
        if (numOfRelationAtoms == 1) {
            this.child = new ProjectOperator(dbPath, query);
            Tuple tuple = ((ProjectOperator) this.child).nonDistinctGetNextTuple();
            while (tuple != null) {
                output.add(new Tuple(tuple));
                tuple = ((ProjectOperator) this.child).nonDistinctGetNextTuple();
            }
        } else {
            this.child = new JoinOperator(dbPath, query);
            Tuple tuple = ((JoinOperator) this.child).nonDistinctGetNextTuple();
            while (tuple != null) {
                output.add(new Tuple(tuple));
                tuple = ((JoinOperator) this.child).nonDistinctGetNextTuple();
            }
        }

        List<Variable> headVariables = query.getHead().getVariables();
        List<Term> headAggregateVariables = query.getHead().getSumAggregate().getProductTerms();
        List<Term> nonAggregateVariables = new ArrayList<>();
        for (Variable variable : headVariables) {
            if (!headAggregateVariables.contains(variable)) {
                nonAggregateVariables.add(variable);
            }
        }
        if (output.size() != 0) {
            if (nonAggregateVariables.size() != 0) {
                for (Tuple tuple : output) {
                    Object[] tupleValues = new Object[nonAggregateVariables.size()];
                    for (int i = 0; i < nonAggregateVariables.size(); i++) {
                        tupleValues[i] = tuple.get(headVariables.indexOf(nonAggregateVariables.get(i)));
                    }
                    Tuple t = new Tuple(tupleValues);
                    if (outputGroup.contains(t)) {
                        continue;
                    }
                    outputGroup.add(t);
                    int sum = 0;
                    for (Tuple tuple1 : output) {
                        Object[] tupleValues1 = new Object[nonAggregateVariables.size()];
                        for (int i = 0; i < nonAggregateVariables.size(); i++) {
                            tupleValues1[i] = tuple1.get(headVariables.indexOf(nonAggregateVariables.get(i)));
                        }
                        Tuple t1 = new Tuple(tupleValues1);
                        // Get the product of the aggregate variables
                        int product1 = 0;
                        if (t.equals(t1)) {
                            for (int i = 0; i < headAggregateVariables.size(); i++) {
                                if (i == 0) {
                                    if (headAggregateVariables.get(i) instanceof Constant)
                                        product1 = ((IntegerConstant) headAggregateVariables.get(i)).getValue();
                                    else
                                        product1 = (int) tuple.get(headVariables.indexOf(headAggregateVariables.get(i)));
                                } else {
                                    if (headAggregateVariables.get(i) instanceof Constant)
                                        product1 = ((IntegerConstant) headAggregateVariables.get(i)).getValue() * product1;
                                    else
                                        product1 = (int) tuple.get(headVariables.indexOf(headAggregateVariables.get(i))) * product1;
                                }
                            }
                        }
                        sum = sum + product1;
                    }
                    Object[] tupleValues2 = new Object[nonAggregateVariables.size() + 1];
                    for (int i = 0; i < nonAggregateVariables.size(); i++) {
                        tupleValues2[i] = tuple.get(headVariables.indexOf(nonAggregateVariables.get(i)));
                    }
                    tupleValues2[nonAggregateVariables.size()] = sum;
                    Tuple t2 = new Tuple(tupleValues2);
                    outputAfterSum.add(t2);
                }
            } else {
                int sum = 0;
                for (Tuple tuple : output) {
                    int product1 = 0;
                    for (int i = 0; i < headAggregateVariables.size(); i++) {
                        if (i == 0) {
                            if (headAggregateVariables.get(i) instanceof Constant) {
                                product1 = ((IntegerConstant) headAggregateVariables.get(i)).getValue();
                            } else {
                                product1 = (int) tuple.get(headVariables.indexOf(headAggregateVariables.get(i)));
                            }
                        } else {
                            if (headAggregateVariables.get(i) instanceof Constant) {
                                product1 = ((IntegerConstant) headAggregateVariables.get(i)).getValue() * product1;
                            } else {
                                product1 = (int) tuple.get(headVariables.indexOf(headAggregateVariables.get(i))) * product1;
                            }
                        }
                    }
                    sum = sum + product1;
                }
                Object[] tupleValues2 = new Object[1];
                tupleValues2[0] = sum;
                Tuple t2 = new Tuple(tupleValues2);
                outputAfterSum.add(t2);
            }
        }

    }

    /**
     * Returns the next tuple in the output after sum.
     * @return the next tuple in the output after sum
     */
    @Override
    public Tuple getNextTuple() throws IOException {
        if (outputIndex < outputAfterSum.size()) {
            return outputAfterSum.get(outputIndex++);
        }
        return null;

    }

    /**
     * Resets the output index.
     */
    @Override
    public void reset() {
        outputIndex = 0;
    }
}