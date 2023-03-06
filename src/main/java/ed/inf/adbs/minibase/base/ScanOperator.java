package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ScanOperator extends Operator {
    private final String relationName;
    private final String dbPath;
    private Scanner scanner;

    public ScanOperator(String dbPath, Query query) {
        this.relationName = ((RelationalAtom) query.getBody().get(0)).getName();
        this.dbPath = dbPath;
        try {
            this.scanner = new Scanner(new File(DatabaseCatalog.getInstance(dbPath).getDataFile(relationName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Tuple getNextTuple() throws IOException {
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            return new Tuple(line);
        }
        return null;
    }

    @Override
    public void reset() {
        try {
            this.scanner = new Scanner(new File(DatabaseCatalog.getInstance(dbPath).getDataFile(relationName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
