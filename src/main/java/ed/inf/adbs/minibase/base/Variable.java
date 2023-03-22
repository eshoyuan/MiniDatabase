package ed.inf.adbs.minibase.base;

public class Variable extends Term {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Variable) {
            Variable otherVar = (Variable) other;
            return name.equals(otherVar.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
