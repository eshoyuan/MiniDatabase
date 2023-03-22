package ed.inf.adbs.minibase.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SumOperator is the operator that implements the sum aggregate.
 * We first use ProjectOperator or JoinOperator to get all the tuples.
 * Then we use a HashMap to sum the tuples with the same group.
 * The outputAfterSum is the output after sum.
 * The outputIndex is the index of the output, used for getNextTuple().
 * To optimize the performance, we do not use List to store all the tuples and do the sum in the end.
 * Instead, we do the sum when we get the tuples.
 */
public class SumOperator extends Operator {

    private final Operator child;
    private final List<Tuple> outputAfterSum = new ArrayList<>(); // output after sum
    private final HashMap<Tuple, Integer> outputGroup = new HashMap<>(); // To sum the group of tuples
    private Integer outputIndex = 0; // Index of the output, used for getNextTuple()

    /**
     * Constructs a SumOperator.
     *
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

        List<Variable> headVariables = query.getHead().getVariables();
        List<Term> headAggregateVariables = query.getHead().getSumAggregate().getProductTerms();
        List<Term> nonAggregateVariables = new ArrayList<>();
        for (Variable variable : headVariables) {
            if (!headAggregateVariables.contains(variable)) {
                nonAggregateVariables.add(variable);
            }
        }
        // If there is only one relation atom, use ProjectOperator
        // Do nonDistinctGetNextTuple() to get all the tuples
        if (numOfRelationAtoms == 1) {
            this.child = new ProjectOperator(dbPath, query);
            Tuple tuple = ((ProjectOperator) this.child).nonDistinctGetNextTuple();
            while (tuple != null) {
                Object[] tupleValues = new Object[nonAggregateVariables.size()];
                for (int i = 0; i < nonAggregateVariables.size(); i++) {
                    tupleValues[i] = tuple.get(headVariables.indexOf(nonAggregateVariables.get(i)));
                }
                Tuple t = new Tuple(tupleValues);
                Integer product = 1;
                for (int i = 0; i < headAggregateVariables.size(); i++) {
                    if (headAggregateVariables.get(i) instanceof Constant)
                        product *= ((IntegerConstant) headAggregateVariables.get(i)).getValue();
                    else
                        product *= (int) tuple.get(headVariables.indexOf(headAggregateVariables.get(i)));
                }
                if (outputGroup.containsKey(t)) {
                    outputGroup.put(t, outputGroup.get(t) + product);
                } else {
                    outputGroup.put(t, product);
                }
                tuple = ((ProjectOperator) this.child).nonDistinctGetNextTuple();
            }
        } else {
            this.child = new JoinOperator(dbPath, query);
            Tuple tuple = ((JoinOperator) this.child).nonDistinctGetNextTuple();
            while (tuple != null) {
                Object[] tupleValues = new Object[nonAggregateVariables.size()];
                for (int i = 0; i < nonAggregateVariables.size(); i++) {
                    tupleValues[i] = tuple.get(headVariables.indexOf(nonAggregateVariables.get(i)));
                }
                Tuple t = new Tuple(tupleValues);
                Integer product = 1;
                for (int i = 0; i < headAggregateVariables.size(); i++) {
                    if (headAggregateVariables.get(i) instanceof Constant)
                        product *= ((IntegerConstant) headAggregateVariables.get(i)).getValue();
                    else
                        product *= (int) tuple.get(headVariables.indexOf(headAggregateVariables.get(i)));
                }
                if (outputGroup.containsKey(t)) {
                    outputGroup.put(t, outputGroup.get(t) + product);
                } else {
                    outputGroup.put(t, product);
                }

                tuple = ((JoinOperator) this.child).nonDistinctGetNextTuple();
            }
        }
        for (Tuple tuple : outputGroup.keySet()) {
            Object[] tupleValues = new Object[nonAggregateVariables.size()+1];
            for (int i = 0; i < nonAggregateVariables.size(); i++) {
                tupleValues[i] = tuple.get(headVariables.indexOf(nonAggregateVariables.get(i)));
            }
            tupleValues[nonAggregateVariables.size()] = outputGroup.get(tuple);
            Tuple t = new Tuple(tupleValues);
            outputAfterSum.add(t);
        }
    }

    /**
     * Returns the next tuple in the output after sum.
     *
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