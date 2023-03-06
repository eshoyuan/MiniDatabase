package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//public class DatabaseCatalog {
//    private static final String schemaPath = "data/evaluation/db/schema.txt";
//    private static final String dbPath = "data/evaluation/db/";
//    private static HashMap<String, String[]> relation2Schema = new HashMap<>();
//    private static HashMap<String, String> relation2Path = new HashMap<>();
//
//    static {
//        try {
//            Scanner scanner = new Scanner(new File("data/evaluation/db/schema.txt"));
//            while (scanner.hasNextLine()) {
//                String line = scanner.nextLine().trim();
//                if (!line.isEmpty()) {
//                    String[] parts = line.split("\\s+");
//                    String key = parts[0];
//                    String[] values = new String[parts.length - 1];
//                    for (int i = 1; i < parts.length; i++) {
//                        values[i - 1] = parts[i];
//                    }
//                    relation2Schema.put(key, values);
//                }
//            }
//            scanner.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    static {
//        for (String relation : relation2Schema.keySet()) {
//            relation2Path.put(relation, dbPath + relation + ".csv");
//        }
//    }
//    private DatabaseCatalog() {
//    }
//
//    public static Map<String, String[]> getRelation2Schema() {
//        return Collections.unmodifiableMap(relation2Schema);
//    }
//
//    public static Map<String, String> getRelation2Path() {
//        return Collections.unmodifiableMap(relation2Path);
//    }
//}

public class DatabaseCatalog {
    private static final String SCHEMA_FILE = "schema.txt";

    private static DatabaseCatalog instance = null;

    private HashMap<String, String[]> relationSchemas;
    private HashMap<String, String> relation2DataFiles;

    private DatabaseCatalog(String dataDir) {
        relationSchemas = new HashMap<>();
        relation2DataFiles = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File(dataDir + SCHEMA_FILE));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split("\\s+");
                    String relationName = parts[0];
                    String[] schema = new String[parts.length - 1];
                    for (int i = 1; i < parts.length; i++) {
                        schema[i - 1] = parts[i];
                    }
                    relationSchemas.put(relationName, schema);
                    relation2DataFiles.put(relationName, dataDir + "files/" + relationName + ".csv");
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseCatalog getInstance(String dataDir) {
        if (instance == null) {
            instance = new DatabaseCatalog(dataDir);
        }
        return instance;
    }

    public String[] getRelationSchema(String relationName) {
        return relationSchemas.get(relationName);
    }

    public String getDataFile(String relationName) {
        return relation2DataFiles.get(relationName);
    }
}
