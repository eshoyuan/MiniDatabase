package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Unit test for Minibase.
 */
public class MinibaseTest {
    /**
     * Rigorous Test :-)
     */
    private boolean compareFiles(String file1, String file2) throws IOException {
        Path path1 = Paths.get(file1);
        Path path2 = Paths.get(file2);
        byte[] content1 = Files.readAllBytes(path1);
        byte[] content2 = Files.readAllBytes(path2);
        return Arrays.equals(content1, content2);
    }
    public static List<String> sortedBody(String path) throws IOException {
        Path file = Paths.get(path);
        String content = new String(Files.readAllBytes(file));
        content = content.replace("\n","");
        content = content.split(" :- ")[1];
        List<String> body = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\([^\\)]*\\)|[^,])*");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String part = matcher.group().trim();
            if (!part.isEmpty()) {
                body.add(part);
            }
        }
        Collections.sort(body);
        return body;
    }
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void testCQMinimizer () throws Exception {
        CQMinimizer.minimizeCQ("data/minimization/input/query1.txt", "data/minimization/output/query1.txt");
        assertEquals(sortedBody("data/minimization/expected_output/query1.txt"), sortedBody("data/minimization/output/query1.txt"));

        CQMinimizer.minimizeCQ("data/minimization/input/query2.txt", "data/minimization/output/query2.txt");
        assertEquals(sortedBody("data/minimization/expected_output/query2.txt"), sortedBody("data/minimization/output/query2.txt"));

        CQMinimizer.minimizeCQ("data/minimization/input/query3.txt", "data/minimization/output/query3.txt");
        assertEquals(sortedBody("data/minimization/expected_output/query3.txt"), sortedBody("data/minimization/output/query3.txt"));

        CQMinimizer.minimizeCQ("data/minimization/input/query4.txt", "data/minimization/output/query4.txt");
        assertEquals(sortedBody("data/minimization/expected_output/query4.txt"), sortedBody("data/minimization/output/query4.txt"));
    }

    @Test
    public void testCheckHomomorphism()
    {
        try {
            Query query1 = QueryParser.parse(Paths.get("data/minimization/input/query1.txt"));
            Query query2 = QueryParser.parse(Paths.get("data/minimization/expected_output/query1.txt"));
            assertTrue(CQMinimizer.check_Homomorphism(query1, query2, new HashMap<String,String>()));

            query1 = QueryParser.parse(Paths.get("data/minimization/input/query2.txt"));
            query2 = QueryParser.parse(Paths.get("data/minimization/expected_output/query2.txt"));
            assertTrue(CQMinimizer.check_Homomorphism(query1, query2, new HashMap<String,String>()));

            query1 = QueryParser.parse(Paths.get("data/minimization/input/query3.txt"));
            query2 = QueryParser.parse(Paths.get("data/minimization/expected_output/query3.txt"));
            assertTrue(CQMinimizer.check_Homomorphism(query1, query2, new HashMap<String,String>()));

            query1 = QueryParser.parse(Paths.get("data/minimization/input/query3.txt"));
            query2 = QueryParser.parse(Paths.get("data/minimization/expected_output/query3_false.txt"));
            assertFalse(CQMinimizer.check_Homomorphism(query1, query2, new HashMap<String,String>()));

            query1 = QueryParser.parse(Paths.get("data/minimization/input/query4.txt"));
            query2 = QueryParser.parse(Paths.get("data/minimization/expected_output/query4.txt"));
            assertTrue(CQMinimizer.check_Homomorphism(query1, query2, new HashMap<String,String>()));

            query1 = QueryParser.parse(Paths.get("data/minimization/input/query4.txt"));
            query2 = QueryParser.parse(Paths.get("data/minimization/expected_output/query4_false.txt"));
            assertFalse(CQMinimizer.check_Homomorphism(query1, query2, new HashMap<String,String>()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDatabaseCatalog() throws Exception {
        DatabaseCatalog catalog = DatabaseCatalog.getInstance("data/evaluation/db/");
        assertEquals(catalog.getRelationSchema("R"),new String[]{"int", "int", "string"});
        assertEquals(catalog.getDataFile("R"),"data/evaluation/db/files/R.csv");

    }

    @Test
    public void testScanOperator() throws Exception {
        Query query = QueryParser.parse(Paths.get("data/evaluation/input/query1.txt"));

        ScanOperator scanOperator = new ScanOperator("data/evaluation/db/", query);
        Tuple t = scanOperator.getNextTuple();
        while (t != null) {
            System.out.println(t);
            t = scanOperator.getNextTuple();
        }
        System.out.println("\n");
        scanOperator.reset();
        t = scanOperator.getNextTuple();
        while (t != null) {
            System.out.println(t);
            t = scanOperator.getNextTuple();
        }
    }
    @Test
    public void testSelectOperator() throws Exception {
        Query query = QueryParser.parse(Paths.get("data/evaluation/input/query4.txt"));
        SelectOperator selectOperator = new SelectOperator("data/evaluation/db/", query);
        Tuple t = selectOperator.getNextTuple();
        while (t != null) {
            System.out.println(t);
            t = selectOperator.getNextTuple();
        }
        selectOperator.reset();
        t = selectOperator.getNextTuple();
        while (t != null) {
            System.out.println(t);
            t = selectOperator.getNextTuple();
        }
    }

    @Test
    public void testProjectOperator() throws Exception {
        Query query = QueryParser.parse(Paths.get("data/evaluation/input/query4.txt"));
        ProjectOperator projectOperator = new ProjectOperator("data/evaluation/db/", query);
        Tuple t = projectOperator.getNextTuple();
        while (t != null) {
            System.out.println(t);
            t = projectOperator.getNextTuple();
        }
        System.out.println("\n");
        projectOperator.reset();
        t = projectOperator.getNextTuple();
        while (t != null) {
            System.out.println(t);
            t = projectOperator.getNextTuple();
        }
    }

    @Test
    public void testJoinOperator() throws Exception {
//        Query query2 = QueryParser.parse(Paths.get("data/evaluation/input/query_join1.txt"));
//        Query query1 = QueryParser.parse(Paths.get("data/evaluation/input/query_join2.txt"));
//        List<ComparisonAtom> joinConditions = new ArrayList<>();
//        joinConditions.add(new ComparisonAtom(new Variable("x"), new Variable("j"), ComparisonOperator.EQ));
//        JoinOperator joinOperator = new JoinOperator("data/evaluation/db/", query1, query2, joinConditions);
//        Tuple t = joinOperator.getNextTuple();
//        while (t != null) {
//            System.out.println(t);
//            t = joinOperator.getNextTuple();
//        }
        Query query = QueryParser.parse(Paths.get("data/evaluation/input/query6.txt"));
        JoinOperator joinOperator = new JoinOperator("data/evaluation/db/", query);
        Tuple t = joinOperator.getNextTuple();
        while (t != null) {
            System.out.println(t);
            t = joinOperator.getNextTuple();
        }
        System.out.println("\n");
        joinOperator.reset();
        t = joinOperator.getNextTuple();
        while (t != null) {
            System.out.println(t);
            t = joinOperator.getNextTuple();
        }
    }

    @Test
    public void testSumOperator() throws Exception {
        Query query = QueryParser.parse(Paths.get("data/evaluation/input/query8.txt"));
        SumOperator sumOperator = new SumOperator("data/evaluation/db/", query);
        Tuple t = sumOperator.getNextTuple();
        while (t != null) {
            System.out.println(t);
            t = sumOperator.getNextTuple();
        }
        System.out.println("\n");
        sumOperator.reset();
        t = sumOperator.getNextTuple();
        while (t != null) {
            System.out.println(t);
            t = sumOperator.getNextTuple();
        }
    }

    @Test
    public void testEvaluateCQ () throws Exception {
        Minibase.evaluateCQ("data/evaluation/db", "data/evaluation/input/query1.txt", "data/evaluation/output/query1.csv");
        assertTrue(compareFiles("data/evaluation/output/query1.csv", "data/evaluation/expected_output/query1.csv"));

        Minibase.evaluateCQ("data/evaluation/db", "data/evaluation/input/query2.txt", "data/evaluation/output/query2.csv");
        assertTrue(compareFiles("data/evaluation/output/query2.csv", "data/evaluation/expected_output/query2.csv"));

        Minibase.evaluateCQ("data/evaluation/db", "data/evaluation/input/query3.txt", "data/evaluation/output/query3.csv");
        assertTrue(compareFiles("data/evaluation/output/query3.csv", "data/evaluation/expected_output/query3.csv"));

        Minibase.evaluateCQ("data/evaluation/db", "data/evaluation/input/query3_new.txt", "data/evaluation/output/query3_new.csv");
        assertTrue(compareFiles("data/evaluation/output/query3_new.csv", "data/evaluation/expected_output/query3.csv"));

        Minibase.evaluateCQ("data/evaluation/db", "data/evaluation/input/query4.txt", "data/evaluation/output/query4.csv");
        assertTrue(compareFiles("data/evaluation/output/query4.csv", "data/evaluation/expected_output/query4.csv"));

        Minibase.evaluateCQ("data/evaluation/db", "data/evaluation/input/query5.txt", "data/evaluation/output/query5.csv");
        assertTrue(compareFiles("data/evaluation/output/query5.csv", "data/evaluation/expected_output/query5.csv"));

        Minibase.evaluateCQ("data/evaluation/db", "data/evaluation/input/query6.txt", "data/evaluation/output/query6.csv");
        assertTrue(compareFiles("data/evaluation/output/query6.csv", "data/evaluation/expected_output/query6.csv"));

        Minibase.evaluateCQ("data/evaluation/db", "data/evaluation/input/query7.txt", "data/evaluation/output/query7.csv");
        assertTrue(compareFiles("data/evaluation/output/query7.csv", "data/evaluation/expected_output/query7.csv"));

        Minibase.evaluateCQ("data/evaluation/db", "data/evaluation/input/query8.txt", "data/evaluation/output/query8.csv");
        assertTrue(compareFiles("data/evaluation/output/query8.csv", "data/evaluation/expected_output/query8.csv"));

        Minibase.evaluateCQ("data/evaluation/db", "data/evaluation/input/query9.txt", "data/evaluation/output/query9.csv");
        assertTrue(compareFiles("data/evaluation/output/query9.csv", "data/evaluation/expected_output/query9.csv"));
    }

}

