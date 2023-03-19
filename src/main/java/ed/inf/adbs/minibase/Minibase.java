package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * In-memory database system
 *
 */
public class Minibase {

    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.err.println("Usage: Minibase database_dir input_file output_file");
            return;
        }

        String databaseDir = args[0];
        if (!databaseDir.endsWith("/")) {
            databaseDir += "/";
        }
        String inputFile = args[1];
        String outputFile = args[2];

        evaluateCQ(databaseDir, inputFile, outputFile);

        parsingExample(inputFile);
    }

    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) throws IOException {
        // TODO: add your implementation
        if (!databaseDir.endsWith("/")) {
            databaseDir += "/";
        }
        Query query = QueryParser.parse(Paths.get(inputFile));
        Operator operator;
        if (query.getHead().getSumAggregate()!=null) {
            operator = new SumOperator(databaseDir, query);
        }
        else {
            Integer numOfRelationAtoms = 0;
            for (Atom atom : query.getBody()) {
                if (atom instanceof RelationalAtom) {
                    numOfRelationAtoms++;
                }
            }
            if (numOfRelationAtoms == 1) {
                operator = new ProjectOperator(databaseDir, query);
            }
            else {
                operator = new JoinOperator(databaseDir, query);
            }
        }
        Tuple tuple = operator.getNextTuple();
        FileWriter writer = new FileWriter(outputFile);
        while (tuple != null) {
            writer.write(tuple.toString().substring(1, tuple.toString().length() - 1)+ "\n");
            tuple = operator.getNextTuple();
        }
        writer.close();
    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static void parsingExample(String filename) {
        try {
            Query query = QueryParser.parse(Paths.get(filename));
            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w), z < w");
            // Query query = QueryParser.parse("Q(SUM(x * 2 * x)) :- R(x, 'z'), S(4, z, w), 4 < 'test string' ");

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
