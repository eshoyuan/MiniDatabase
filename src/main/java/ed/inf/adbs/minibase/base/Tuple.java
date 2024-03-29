package ed.inf.adbs.minibase.base;

import java.util.Arrays;

/**
 * Tuple is a class that represents a tuple in the database.
 */
public class Tuple {
    private final Object[] values;

    /** Creates a new tuple with the specified values. */
    public Tuple(String tupleString) {
        String[] stringValues = tupleString.split(", ");
        values = new Object[stringValues.length];
        for (int i = 0; i < stringValues.length; i++) {
            String stringValue = stringValues[i].trim();
            if (stringValue.startsWith("'") && stringValue.endsWith("'")) {
                values[i] = stringValue.substring(1, stringValue.length() - 1);
            } else {
                values[i] = Integer.parseInt(stringValue);
            }
        }
    }

    /** Creates a new tuple with the specified values.
     * @param tuple the tuple to copy
     */
    public Tuple(Tuple tuple) {
        this.values = new Object[tuple.values.length];
        System.arraycopy(tuple.values, 0, this.values, 0, tuple.values.length);
    }

    /**
     * Creates a new tuple with the specified values.
     * @param values the values of the tuple
     */
    public Tuple(Object[] values) {
        this.values = values;
    }

    /**
     * Gets the number of values in the tuple.
     * @return the number of values in the tuple
     * @param index the index of the value
     */
    public Object get(int index) {
        return values[index];
    }

    /**
     * Concatenates the tuple with another tuple.
     * @param tuple the tuple to concatenate
     * @return the concatenated tuple
     */
    public Tuple concat(Tuple tuple) {
        Object[] newValues = new Object[values.length + tuple.values.length];
        System.arraycopy(values, 0, newValues, 0, values.length);
        System.arraycopy(tuple.values, 0, newValues, values.length, tuple.values.length);
        return new Tuple(newValues);
    }


    /**
     * Converts the tuple to a string.
     * @return the string representation of the tuple
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof String) {
                sb.append("'");
            }
            sb.append(values[i]);
            if (values[i] instanceof String) {
                sb.append("'");
            }
            if (i < values.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Tuple other = (Tuple) obj;
        return Arrays.equals(values, other.values);
    }
}
