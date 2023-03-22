package ed.inf.adbs.minibase.base;

import java.io.IOException;
import java.util.List;

/**
 * Operator is the abstract class for all operators.
 */
public abstract class Operator {
    /**
     * @return the next tuple in the operator's output
     */
    public abstract Tuple getNextTuple() throws IOException;

    /**
     * Resets the operator to its initial state.
     */
    public abstract void reset();

    /**
     * @return the list of variables that are returned by the operator
     */
    public List<Term> getReturnVariables() {
        return null;
    }
}
