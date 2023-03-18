package ed.inf.adbs.minibase.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectOperator extends Operator {
    private Operator child = null;
    private final Object[] headVariables;
    private final List<Term> bodyVariables;

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

    public ProjectOperator(Operator child, Object[] headVariables, List<Term> bodyVariables) {
        this.child = child;
        this.headVariables = new Object[headVariables.length];
        System.arraycopy(headVariables, 0, this.headVariables, 0, headVariables.length);
        this.bodyVariables = new ArrayList<>(bodyVariables);
    }
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

        return new Tuple(projectValues);
    }

    @Override
    public void reset() {
        child.reset();
    }
}
