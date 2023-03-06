package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
}

