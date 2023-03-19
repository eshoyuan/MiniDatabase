package ed.inf.adbs.minibase.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ed.inf.adbs.minibase.base.ComparisonOperator.EQ;

public class JoinOperator extends Operator {

    private Operator leftChild = null;
    private Operator rightChild = null;
    private Operator projectChild = null;
    private List<ComparisonAtom> selectionAtoms = new ArrayList<>();
    private List<ComparisonAtom> joinAtoms = new ArrayList<>();
    private final List<RelationalAtom> relationalAtoms = new ArrayList<>();
    private List<Term> returnVariables = new ArrayList<>();
    private Tuple leftTuple = null;
    public JoinOperator(JoinOperator joinOperator) {
        this.leftChild = joinOperator.leftChild;
        this.rightChild = joinOperator.rightChild;
        // this.query = new Query(joinOperator.query);
        // this.dbPath = joinOperator.dbPath;
        this.selectionAtoms = new ArrayList<>(joinOperator.selectionAtoms);
        this.joinAtoms =  new ArrayList<>(joinOperator.joinAtoms);
        this.relationalAtoms.addAll(joinOperator.relationalAtoms);
        this.returnVariables = new ArrayList<>(joinOperator.returnVariables);
    }
    public List<Term> getReturnVariables() {
        List<Term> leftReturnVariables = null;
        if (leftChild instanceof ScanOperator) {
            leftReturnVariables =  ((ScanOperator) leftChild).getReturnVariables();
        } else if (leftChild instanceof JoinOperator) {
            leftReturnVariables = ((JoinOperator) leftChild).getReturnVariables();
        }
        else if (leftChild instanceof SelectOperator) {
            leftReturnVariables = ((SelectOperator) leftChild).getReturnVariables();
        }
        List<Term> rightReturnVariables = null;
        if (rightChild instanceof ScanOperator) {
            rightReturnVariables =  ((ScanOperator) rightChild).getReturnVariables();
        } else if (rightChild instanceof JoinOperator) {
            rightReturnVariables = ((JoinOperator) rightChild).getReturnVariables();
        }
        else if (rightChild instanceof SelectOperator) {
            rightReturnVariables = ((SelectOperator) rightChild).getReturnVariables();
        }
        List<Term> returnVariables = new ArrayList<>();
        returnVariables.addAll(leftReturnVariables);
        for (Term term : rightReturnVariables) {
            if (!leftReturnVariables.contains(term)) {
                returnVariables.add(term);
            }
        }
        return returnVariables;
    }

    public JoinOperator(String dbPath, Query query) {
        for (Atom atom : query.getBody()) {
            if (atom instanceof ComparisonAtom) {
                if (((ComparisonAtom) atom).getTerm1() instanceof Variable && ((ComparisonAtom) atom).getTerm2() instanceof Variable) {
                    joinAtoms.add((ComparisonAtom) atom);
                } else {
                    selectionAtoms.add((ComparisonAtom) atom);
                }
            }
            if (atom instanceof RelationalAtom) {
                relationalAtoms.add((RelationalAtom) atom);
            }
        }
        // if there is an implicit conditions in the RelationAtom, then transform it
        Set<Variable> appearedVariables = new HashSet<Variable>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                for (int i = 0; i < ((RelationalAtom) atom).getTerms().size(); i++) {
                    if (appearedVariables.contains(((RelationalAtom) atom).getTerms().get(i))) {
                        String newVar = RandomName.generate();
                        joinAtoms.add(new ComparisonAtom(((RelationalAtom) atom).getTerms().get(i), new Variable(newVar), EQ));
                        ((RelationalAtom) atom).getTerms().set(i, new Variable(newVar));
                    } else {
                        appearedVariables.add((Variable) ((RelationalAtom) atom).getTerms().get(i));
                    }
                }
            }
        }
        JoinOperator beforeJoinOperator = null;
        Head head1 = null;
        List<Atom> body1 = null;
        Head head2 = null;
        List<Atom> body2 = null;
        for (int i = 0; i < relationalAtoms.size() - 1; i++) {

            if (i == 0) {
                List<Term> headTerms1 = (relationalAtoms.get(i)).getTerms();
                List<Variable> headVariables1 = headTerms1.stream().filter(Variable.class::isInstance) // 过滤掉不是 Variable 类型的元素
                        .map(Variable.class::cast) // 将 Term 转换成 Variable
                        .collect(Collectors.toList()); // 转换成 List<Variable>;
                head1 = new Head(RandomName.generate(), headVariables1, null);
                body1 = new ArrayList<>();
                body1.add(relationalAtoms.get(i));
                for (ComparisonAtom atom : selectionAtoms) {
                    if (headVariables1.contains(atom.getTerm1()) || headVariables1.contains(atom.getTerm2())) {
                        body1.add(atom);
                    }
                }
                // this.leftChild = new ScanOperator(dbPath, new Query(head1, body1));

                List<Term> headTerms2 = (relationalAtoms.get(i+1)).getTerms();
                List<Variable> headVariables2 = headTerms2.stream().filter(Variable.class::isInstance) // 过滤掉不是 Variable 类型的元素
                        .map(Variable.class::cast) // 将 Term 转换成 Variable
                        .collect(Collectors.toList()); // 转换成 List<Variable>;
                head2 = new Head(RandomName.generate(), headVariables2, null);
                body2 = new ArrayList<>();
                body2.add(relationalAtoms.get(i + 1));
                for (ComparisonAtom atom : selectionAtoms) {
                    if (headVariables2.contains(atom.getTerm1()) || headVariables2.contains(atom.getTerm2())) {
                        body2.add(atom);
                    }
                }
                beforeJoinOperator = new JoinOperator(dbPath, new Query(head1, body1), new Query(head2, body2), joinAtoms);
            } else {
//                if (i==1){
//                    beforeJoinOperator = new JoinOperator(dbPath, new Query(head1, body1), new Query(head2, body2), joinAtoms);
//                }
//                else{
//                    beforeJoinOperator = new JoinOperator(dbPath, beforeJoinOperator, new Query(head2, body2), joinAtoms);
//                }
                List<Term> headTerms = (relationalAtoms.get(i+1)).getTerms();
                List<Variable> headVariables = headTerms.stream().filter(Variable.class::isInstance) // 过滤掉不是 Variable 类型的元素
                        .map(Variable.class::cast) // 将 Term 转换成 Variable
                        .collect(Collectors.toList()); // 转换成 List<Variable>;
                head2 = new Head(RandomName.generate(), headVariables, null);
                body2 = new ArrayList<>();
                body2.add(relationalAtoms.get(i + 1));
                for (ComparisonAtom atom : selectionAtoms) {
                    if (headVariables.contains(atom.getTerm1()) || headVariables.contains(atom.getTerm2())) {
                        body2.add(atom);
                    }
                }
                beforeJoinOperator = new JoinOperator(dbPath, new JoinOperator(beforeJoinOperator), new Query(head2, body2), joinAtoms);
            }
        }
        List<Term> bodyVariables =new ArrayList<>();
        bodyVariables.addAll(beforeJoinOperator.leftChild.getReturnVariables());
        bodyVariables.addAll(beforeJoinOperator.rightChild.getReturnVariables());
//        this.leftChild = beforeJoinOperator;
//        this.rightChild = new JoinOperator(dbPath, beforeJoinOperator, new Query(head2, body2), joinAtoms);
        List<Variable> headVariables = query.getHead().getVariables();
        if (query.getHead().getSumAggregate()!=null){
            for (Term productTerm : query.getHead().getSumAggregate().getProductTerms()) {
                   if (!headVariables.contains(productTerm)) {
                       if (productTerm instanceof Variable) {
                           headVariables.add((Variable) productTerm);
                       }
                }
            }
        }
        Object[] headVariablesArray = headVariables.toArray();
        this.projectChild  = new ProjectOperator(beforeJoinOperator, headVariablesArray,bodyVariables);
    }

    public JoinOperator(String dbPath, Query leftQuery, Query rightQuery, List<ComparisonAtom> joinAtoms) {
        this.leftChild = new SelectOperator(dbPath, leftQuery);
        this.rightChild = new SelectOperator(dbPath, rightQuery);
        this.joinAtoms = new ArrayList<>(joinAtoms);
        this.returnVariables = new ArrayList<>((((RelationalAtom) leftQuery.getBody().get(0)).getTerms()));
        for (Term term : ((RelationalAtom) rightQuery.getBody().get(0)).getTerms()) {
            if (!returnVariables.contains(term)) {
                returnVariables.add(term);
            }
        }
    }

    public JoinOperator(String dbPath, JoinOperator leftOperator, Query rightQuery, List<ComparisonAtom> joinAtoms) {
        this.leftChild = leftOperator;
        this.rightChild = new SelectOperator(dbPath, rightQuery);
        this.joinAtoms = new ArrayList<>(joinAtoms);
//        this.selectionAtoms = new ArrayList<>(selectionAtoms);
        this.returnVariables = new ArrayList<>(leftOperator.getReturnVariables());
        for (Term term : ((RelationalAtom) rightQuery.getBody().get(0)).getTerms()) {
            if (!returnVariables.contains(term)) {
                returnVariables.add(term);
            }
        }
    }

    @Override
    public Tuple getNextTuple() throws IOException {
        if (projectChild != null){
            return projectChild.getNextTuple();
        }

        List<Term> leftVariables = null;
        List<Term> rightVariables = null;
        if (leftChild instanceof JoinOperator){
            leftVariables = ((JoinOperator) leftChild).getReturnVariables();
        } else if (leftChild instanceof SelectOperator) {
            leftVariables = ((SelectOperator) leftChild).getReturnVariables();
        }
        else if (leftChild instanceof ScanOperator) {
            leftVariables = ((ScanOperator) leftChild).getReturnVariables();
        }

        if (rightChild instanceof SelectOperator){
            rightVariables = ((SelectOperator) rightChild).getReturnVariables();
        }
        else if (rightChild instanceof ScanOperator) {
            rightVariables = ((ScanOperator) rightChild).getReturnVariables();
        }
        if (leftTuple == null) {
            leftTuple = leftChild.getNextTuple();
        }
        while (leftTuple != null) {
            // rightChild.reset();
            Tuple rightTuple = rightChild.getNextTuple();
            while (rightTuple != null) {

                if (compare(leftTuple, rightTuple, joinAtoms, leftVariables, rightVariables)) {
//                    return merge(leftTuple, rightTuple);
                    return new Tuple(leftTuple.concat(rightTuple));
                }
                rightTuple = rightChild.getNextTuple();
            }
            leftTuple = leftChild.getNextTuple();
            rightChild.reset();
        }
        return null;
    }
    public Tuple nonDistinctGetNextTuple() throws IOException {
        if (projectChild != null){
            return ((ProjectOperator) projectChild).nonDistinctGetNextTuple();
        }

        List<Term> leftVariables = null;
        List<Term> rightVariables = null;
        if (leftChild instanceof JoinOperator){
            leftVariables = ((JoinOperator) leftChild).getReturnVariables();
        } else if (leftChild instanceof SelectOperator) {
            leftVariables = ((SelectOperator) leftChild).getReturnVariables();
        }
        else if (leftChild instanceof ScanOperator) {
            leftVariables = ((ScanOperator) leftChild).getReturnVariables();
        }

        if (rightChild instanceof SelectOperator){
            rightVariables = ((SelectOperator) rightChild).getReturnVariables();
        }
        else if (rightChild instanceof ScanOperator) {
            rightVariables = ((ScanOperator) rightChild).getReturnVariables();
        }
        if (leftTuple == null) {
            leftTuple = leftChild.getNextTuple();
        }
        while (leftTuple != null) {
            // rightChild.reset();
            Tuple rightTuple = rightChild.getNextTuple();
            while (rightTuple != null) {

                if (compare(leftTuple, rightTuple, joinAtoms, leftVariables, rightVariables)) {
//                    return merge(leftTuple, rightTuple);
                    return new Tuple(leftTuple.concat(rightTuple));
                }
                rightTuple = rightChild.getNextTuple();
            }
            leftTuple = leftChild.getNextTuple();
            rightChild.reset();
        }
        return null;
    }
    @Override
    public void reset() {
        if (projectChild != null){
            projectChild.reset();
        }
        if (leftChild != null){
            leftChild.reset();
        }
        if (rightChild != null){
            rightChild.reset();
        }
        leftTuple = null;
    }

    public boolean compare(Tuple leftTuple, Tuple rightTuple, List<ComparisonAtom> joinAtoms, List<Term> leftVariables, List<Term> rightVariables) {
        for (ComparisonAtom atom : joinAtoms) {
            if (!singleCompare(leftTuple, rightTuple, atom, leftVariables, rightVariables)) {
                return false;
            }
        }
        return true;
    }
    public boolean singleCompare(Tuple leftTuple, Tuple rightTuple, ComparisonAtom atom, List<Term> leftVariables, List<Term> rightVariables) {
        ComparisonOperator op = atom.getOp();
        Term term1 = atom.getTerm1();
        Term term2 = atom.getTerm2();
        if (leftVariables.indexOf(term1)==-1 && rightVariables.indexOf(term1)==-1){
            return true;
        }
        if (leftVariables.indexOf(term2)==-1 && rightVariables.indexOf(term2)==-1){
            return true;
        }
        Object value1 = term2value(leftTuple, rightTuple, leftVariables, rightVariables, term1);
        Object value2 = term2value(leftTuple, rightTuple, leftVariables, rightVariables, term2);

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

    private Object term2value(Tuple leftTuple, Tuple rightTuple, List<Term> leftVariables, List<Term> rightVariables, Term term) {
        Integer index = leftVariables.indexOf(term);
        if (index != -1) {
            return leftTuple.get(index);
        }
        index = rightVariables.indexOf(term);
        return rightTuple.get(index);

    }
}
