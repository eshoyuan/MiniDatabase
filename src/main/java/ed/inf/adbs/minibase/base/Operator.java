package ed.inf.adbs.minibase.base;

import java.io.IOException;

public abstract class Operator {
    public abstract Tuple getNextTuple() throws IOException;

    public abstract void reset();
}
