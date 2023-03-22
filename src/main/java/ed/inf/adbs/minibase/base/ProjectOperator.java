package ed.inf.adbs.minibase.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * ProjectOperator is the operator that projects the tuples from the child operator.
 * We use ScanOperator or SelectOperator as the child operator.
 * The ProjectOperator will project the tuples according to the variables in the head by using the variables in the body.
 * We record the tuples that have been projected in a HashSet to avoid duplicate tuples.
 */
public class ProjectOperator extends Operator {
    private final Object[] headVariables;
    private final List<Term> bodyVariables;
    // To avoid duplicate tuples
    private final HashSet<Tuple> output = new HashSet<>();
    private Operator child = null;

    /**
     * Constructs a ProjectOperator.
     * @param dbPath the path to the database
     * @param query  the query
     */
    public ProjectOperator(String dbPath, Query query) {
        for (Atom atom : query.getBody()) {
            if (atom instanceof ComparisonAtom) {
                this.child = new SelectOperator(dbPath, query);
            }
        }
        if (this.child == null) {
            this.child = new ScanOperator(dbPath, query);
        }
        this.headVariables = query.getHead().getVariables().toArray();
        this.bodyVariables = ((RelationalAtom) query.getBody().get(0)).getTerms();
    }

    /**
     * Constructs a ProjectOperator.
     * @param child the child operator
     * @param headVariables the variables in the head
     * @param bodyVariables the variables in the body
     */
    public ProjectOperator(Operator child, Object[] headVariables, List<Term> bodyVariables) {
        this.child = child;
        this.headVariables = new Object[headVariables.length];
        System.arraycopy(headVariables, 0, this.headVariables, 0, headVariables.length);
        this.bodyVariables = new ArrayList<>(bodyVariables);
    }

    /**
     * @return the list of variables that are returned by the operator
     */
    @Override
    public Tuple getNextTuple() throws IOException {
        Tuple tuple = child.getNextTuple();
        if (tuple == null) {
            return null;
        }
        Object[] projectValues = new Object[headVariables.length];
        for (int i = 0; i < headVariables.length; i++) {
            projectValues[i] = tuple.get(bodyVariables.indexOf(headVariables[i]));
        }
        if (output.contains(new Tuple(projectValues))) {
            return getNextTuple();
        }
        output.add(new Tuple(projectValues));
        return new Tuple(projectValues);
    }

    /**
     * @return the list of variables that are returned by the operator
     */
    public Tuple nonDistinctGetNextTuple() throws IOException {
        Tuple tuple = child.getNextTuple();
        if (tuple == null) {
            return null;
        }
        Object[] projectValues = new Object[headVariables.length];
        for (int i = 0; i < headVariables.length; i++) {
            projectValues[i] = tuple.get(bodyVariables.indexOf(headVariables[i]));
        }
        return new Tuple(projectValues);
    }

    /**
     * @return the list of variables that are returned by the operator
     */
    @Override
    public void reset() {
        child.reset();
        output.clear();
    }
}
