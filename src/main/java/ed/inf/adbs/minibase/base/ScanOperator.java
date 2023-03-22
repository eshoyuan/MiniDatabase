package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ScanOperator is the operator that scans the tuples from the relation.
 * We use the scanner to read the tuples from the relation.
 * When we call the getNextTuple() function, we read the next line from the scanner.
 * If there is no next line, we return null.
 * Otherwise, we return the tuple.
 */
public class ScanOperator extends Operator {
    private final String relationName;
    private final String dbPath;
    private Scanner scanner;

    private List<Term> returnVariables = new ArrayList<>();

    /**
     * Constructor of ScanOperator
     *
     * @param dbPath the path of the database
     * @param query  the query
     */
    public ScanOperator(String dbPath, Query query) {
        this.relationName = ((RelationalAtom) query.getBody().get(0)).getName();
        this.dbPath = dbPath;
        try {
            this.scanner = new Scanner(new File(DatabaseCatalog.getInstance(dbPath).getDataFile(relationName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        returnVariables = ((RelationalAtom) query.getBody().get(0)).getTerms();
    }

    /**
     * @return the list of variables that are returned by the operator
     */
    public List<Term> getReturnVariables() {
        return returnVariables;
    }

    /**
     * Function to get the next tuple
     *
     * @return the next tuple if there is one, null otherwise
     */
    @Override
    public Tuple getNextTuple() throws IOException {
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            return new Tuple(line);
        }
        return null;
    }

    /**
     * Resets the operator to its initial state.
     * This function is used to re-read the input data.
     */
    @Override
    public void reset() {
        try {
            this.scanner = new Scanner(new File(DatabaseCatalog.getInstance(dbPath).getDataFile(relationName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
