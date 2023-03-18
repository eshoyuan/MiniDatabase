package ed.inf.adbs.minibase.base;

public class Tuple {
    private final Object[] values;

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
    public Tuple(Tuple tuple) {
        this.values = new Object[tuple.values.length];
        System.arraycopy(tuple.values, 0, this.values, 0, tuple.values.length);
    }
    public Tuple(Object[] values) {
        this.values = values;
    }

    public Object get(int index) {
        return values[index];
    }

    public Tuple concat(Tuple tuple) {
        Object[] newValues = new Object[values.length + tuple.values.length];
        System.arraycopy(values, 0, newValues, 0, values.length);
        System.arraycopy(tuple.values, 0, newValues, values.length, tuple.values.length);
        return new Tuple(newValues);
    }



    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]);
            if (i < values.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
