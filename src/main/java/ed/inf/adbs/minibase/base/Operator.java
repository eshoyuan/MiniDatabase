package ed.inf.adbs.minibase.base;

import java.io.IOException;
import java.util.List;

public abstract class Operator {
    public abstract Tuple getNextTuple() throws IOException;

    public abstract void reset();

    public List<Term> getReturnVariables() {
        return null;
    }
}
