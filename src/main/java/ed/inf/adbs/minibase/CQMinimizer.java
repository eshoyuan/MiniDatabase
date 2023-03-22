package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.Head;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * Minimization of conjunctive queries
 */
public class CQMinimizer {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        minimizeCQ(inputFile, outputFile);

//        parsingExample(inputFile);
    }

    /**
     * CQ minimization procedure
     * <p>
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     * @param inputFile the input file
     * @param outputFile the output file
     */
    public static void minimizeCQ(String inputFile, String outputFile) {


        try {
            Query query = QueryParser.parse(Paths.get(inputFile));
            boolean changed = true;

            while (changed) {
                // try to remove an atom from body
                Query removed_query = removeAtom(query);
                // if no atom can be removed, stop the loop
                if (removed_query.toString().equals(query.toString())) {
                    changed = false;
                } else {
                    query = new Query(removed_query);
                }
            }

            FileWriter writer = new FileWriter(outputFile);
            writer.write(query.toString());
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }

    }


    /**
     * Function to remove an atom from body, if no atom can be removed, return the original query.
     * We check every atom in the body, if the atom can be removed, we check if the two queries are homomorphism.
     *
     * @param query the original query
     * @return the query after removing an atom
     */
    public static Query removeAtom(Query query) {
        for (int i = 0; i < query.getBody().size(); i++) {
            Query removed_query = new Query(query);
            removed_query.getBody().remove(i);
            // check if the removed query is homomorphism
            if (check_Homomorphism(query, removed_query, new HashMap<String, String>())) {
                return removed_query;
            }
        }
        return query;
    }

    /**
     * Function to check if two queries are homomorphism
     * We try to map the variables in the head of query1 to the variables in the head of query2.
     * If the mapping is correct, the two queries are homomorphism.
     *
     * @param query1  the original query
     * @param query2  the query after removing an atom
     * @param mapping the mapping of variables in query1 to variables in query2
     * @return true if the two queries are homomorphism, false otherwise
     */
    public static boolean check_Homomorphism(Query query1, Query query2, HashMap<String, String> mapping) {
        if (query1.getBody().size() == 0) {
            return true;
        }
        // get the variables in head
        String head_variables = query1.getHead().getVariables().toString();

        // check the first atom in query1
        RelationalAtom relationalAtom1 = (RelationalAtom) query1.getBody().get(0);
        // find the same atom in query2
        for (Atom atom2 : query2.getBody()) {
            RelationalAtom relationalAtom2 = (RelationalAtom) atom2;
            boolean isSame = true;
            // if the atom has the same predicate and the same length of terms, it could be the same atom
            if (relationalAtom1.getName().equals(relationalAtom2.getName()) && relationalAtom1.getTerms().size() == relationalAtom2.getTerms().size()) {
                HashMap<String, String> new_mapping = new HashMap<String, String>(mapping);
                // check every term
                for (int i = 0; i < relationalAtom1.getTerms().size(); i++) {
                    if (relationalAtom1.getTerms().get(i).getClass().getName().equals("ed.inf.adbs.minibase.base.Variable")) {
                        // if the term is a variable, check if it has been mapped
                        if (new_mapping.containsKey(relationalAtom1.getTerms().get(i).toString())) {
                            // if it has been mapped, check if the mapping is correct
                            if (!new_mapping.get(relationalAtom1.getTerms().get(i).toString()).equals(relationalAtom2.getTerms().get(i).toString())) {
                                isSame = false;
                                break;
                            }
                        } else {
                            // if it has not been mapped, add the mapping

                            // check if the variable is in head
                            if (head_variables.contains(relationalAtom1.getTerms().get(i).toString())) {
                                if (!relationalAtom1.getTerms().get(i).toString().equals(relationalAtom2.getTerms().get(i).toString())) {
                                    isSame = false;
                                    break;
                                }
                            } else {
                                new_mapping.put(relationalAtom1.getTerms().get(i).toString(), relationalAtom2.getTerms().get(i).toString());
                            }
                        }
                    } else {
                        // if the term is a constant, check if it is the same
                        if (!relationalAtom1.getTerms().get(i).toString().equals(relationalAtom2.getTerms().get(i).toString())) {
                            isSame = false;
                        }
                    }
                }
                if (isSame) {
                    // if the atom is the same, check the rest of the atoms
                    Query new_query1 = new Query(query1.getHead(), query1.getBody().subList(1, query1.getBody().size()));
                    if (check_Homomorphism(new_query1, query2, new_mapping)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */
    public static void parsingExample(String filename) {

        try {
            Query query = QueryParser.parse(Paths.get(filename));
            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }
}
