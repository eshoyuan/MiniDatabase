package ed.inf.adbs.minibase.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ed.inf.adbs.minibase.base.ComparisonOperator.EQ;

/**
 * SelectOperator is the operator that selects the tuples from the child operator.
 * We use ScanOperator as the child operator.
 * We first transform the constant in the RelationAtom to a ComparisonAtom.
 * The SelectOperator will compare the tuple with the ComparisonAtom in the query's body.
 * If the tuple satisfies the ComparisonAtom, then the tuple will be returned.
 */
public class SelectOperator extends Operator {

    private final Operator child;
    private final List<ComparisonAtom> comparisonAtoms = new ArrayList<>();
    private final RelationalAtom relationalAtom;

    /**
     * Constructor of SelectOperator
     *
     * @param dbPath the path of the database
     * @param query  the query
     */
    public SelectOperator(String dbPath, Query query) {
        this.child = new ScanOperator(dbPath, query);
        // extract ComparisonAtom in query's body
        for (Atom atom : query.getBody()) {
            if (atom instanceof ComparisonAtom) {
                comparisonAtoms.add((ComparisonAtom) atom);
            }
        }
        // if there is a constant in the RelationAtom, then transform it to a ComparisonAtom
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                for (int i = 0; i < ((RelationalAtom) atom).getTerms().size(); i++) {
                    if (((RelationalAtom) atom).getTerms().get(i) instanceof Constant) {
                        String newVar = RandomName.generate();
                        comparisonAtoms.add(new ComparisonAtom(((RelationalAtom) atom).getTerms().get(i), new Variable(newVar), EQ));
                        ((RelationalAtom) atom).getTerms().set(i, new Variable(newVar));
                    }
                }
            }
        }
        relationalAtom = (RelationalAtom) query.getBody().get(0);
    }

    /**
     * @return the list of variables that are returned by the operator
     */
    public List<Term> getReturnVariables() {
        return child.getReturnVariables();
    }

    /**
     * Function to get the next tuple
     *
     * @return the next tuple
     */
    @Override
    public Tuple getNextTuple() throws IOException {
        Tuple tuple = child.getNextTuple();
        while (tuple != null) {
            if (compare(tuple, comparisonAtoms, relationalAtom)) {
                return tuple;
            }
            tuple = child.getNextTuple();
        }
        return null;
    }

    /**
     * Function to reset the operator by resetting the child operator
     */
    @Override
    public void reset() {
        child.reset();
    }

    /**
     * Function to check if the tuple satisfies all the ComparisonAtom
     *
     * @param tuple          the tuple to be compared
     * @param atoms          the list of ComparisonAtom
     * @param relationalAtom the RelationalAtom
     * @return true if the tuple satisfies all the ComparisonAtom
     */
    public boolean compare(Tuple tuple, List<ComparisonAtom> atoms, RelationalAtom relationalAtom) {
        for (ComparisonAtom atom : atoms) {
            if (!singleCompare(tuple, atom, relationalAtom)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Function to check if the tuple satisfies a single ComparisonAtom
     *
     * @param tuple          the tuple to be compared
     * @param atom           the ComparisonAtom
     * @param relationalAtom the RelationalAtom
     * @return true if the tuple satisfies the ComparisonAtom
     */
    public boolean singleCompare(Tuple tuple, ComparisonAtom atom, RelationalAtom relationalAtom) {
        ComparisonOperator op = atom.getOp();
        Term term1 = atom.getTerm1();
        Term term2 = atom.getTerm2();
        Object value1 = term2value(tuple, relationalAtom, term1);
        Object value2 = term2value(tuple, relationalAtom, term2);

        switch (op) {
            case EQ:
                return value1.equals(value2);
            case NEQ:
                return !value1.equals(value2);
            case GT:
                if (value1 instanceof String) {
                    return ((String) value1).compareTo((String) value2) > 0;
                }
                return (Integer) value1 > (Integer) value2;
            case GEQ:
                if (value1 instanceof String) {
                    return ((String) value1).compareTo((String) value2) >= 0;
                }
                return (Integer) value1 >= (Integer) value2;
            case LT:
                if (value1 instanceof String) {
                    return ((String) value1).compareTo((String) value2) < 0;
                }
                return (Integer) value1 < (Integer) value2;
            case LEQ:
                if (value1 instanceof String) {
                    return ((String) value1).compareTo((String) value2) <= 0;
                }
                return (Integer) value1 <= (Integer) value2;
            default:
                return false;
        }
    }

    /**
     * Function to get the value of a Term
     *
     * @param tuple          the tuple to be compared
     * @param relationalAtom the RelationalAtom
     * @param term           the Term
     * @return the value of the Term
     */
    private Object term2value(Tuple tuple, RelationalAtom relationalAtom, Term term) {
        if (term instanceof IntegerConstant) {
            return ((IntegerConstant) term).getValue();
        } else if (term instanceof StringConstant) {
            return ((StringConstant) term).getValue();
        } else if (term instanceof Variable) {
            Integer index = relationalAtom.getTerms().indexOf(term);
            return tuple.get(index);
        }
        return null;
    }
}
