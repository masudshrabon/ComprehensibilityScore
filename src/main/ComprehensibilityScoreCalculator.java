package main;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class ComprehensibilityScoreCalculator {
	   // Variables to exclude from readability scoring (e.g., loop counters)
    private static final String[] EXCLUDED_VARIABLES = {"i", "j"};
    private static final Set<String> BUILTIN_READABLES = new HashSet<>(Arrays.asList(
    	    "id", "url", "api", "http", "ip", "sql", "xml", "json", "db", "cpu", "gpu"
    	));

    // Dictionary for scoring, loaded once when the program starts
    private static final Set<String> LOCAL_DICTIONARY = loadDictionary("Dictionary(Modified).txt");
    // Class to store metadata for each detected entity
    private static class EntityInfo {
        String entity;
        String type;
        double score;
        String readability;

        EntityInfo(String entity, String type, double score, String readability) {
            this.entity = entity;
            this.type = type;
            this.score = score;
            this.readability = readability;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select the language to analyze:");
        System.out.println("1. Java");
        System.out.println("2. C#");
        System.out.println("3. Python");
        System.out.println("4. JavaScript");
        System.out.println("5. C/C++");
        System.out.print("Enter your choice (1–5): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        // Prompt for input path (file or folder)
        System.out.print("Enter the path to the project directory: ");
        String folderPath = scanner.nextLine().replace("\\", "/");

        // Route to appropriate analysis handler
        switch (choice) {
            case 1:
                runJavaAnalysis(folderPath);
                break;
            case 2:
                runCSharpAnalysis(folderPath);
                break;

            case 3:
                runPythonAnalysis(folderPath);
                break;
            case 4:
            	 runJavaScriptAnalysis(folderPath);
                break;
            case 5:
            	runCppAnalysis(folderPath);
               break;
            default:
                System.out.println("Invalid choice.");
        }
    }


    // ========== JAVA ANALYSIS ==========

    // Analyzes either a single Java file or an entire folder recursively
    private static void runJavaAnalysis(String path) {
        try {
            List<File> javaFiles = getFilesOrSingle(path, ".java");

            if (javaFiles.isEmpty()) {
                System.out.println("No valid Java files found.");
                return;
            }
            // Store per-file entity data
            Map<File, List<EntityInfo>> fileEntityMap = new LinkedHashMap<>();

            for (File file : javaFiles) {
                List<EntityInfo> entityList = new ArrayList<>();
                List<String[]> entities = extractEntities(file.getAbsolutePath());
                calculateComprehensibilityScoreUnified(entities, entityList);

                fileEntityMap.put(file, entityList);
            }
            // Generate two reports
            writeDetailedCSV(fileEntityMap, "Java");
            writeSummaryCSV(fileEntityMap, "Java");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
 // Uses regex patterns to extract all relevant entities from Java files
    private static List<String[]> extractEntities(String filePath) throws IOException {
        List<String[]> entities = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        // Patterns for different Java elements
        Pattern packagePattern = Pattern.compile("^\\s*package\\s+([a-zA-Z0-9_.]+);?");
        Pattern importPattern = Pattern.compile("^\\s*import\\s+([a-zA-Z0-9_.]+);?");
        Pattern classPattern = Pattern.compile("\\bclass\\s+(\\w+)");
        Pattern methodPattern = Pattern.compile("(public|private|protected|static|final|void)?\\s+\\w+\\s+(\\w+)\\s*\\(");
        Pattern variablePattern = Pattern.compile("^(?!\\s*(class|package|import)).*?\\b(\\w+)\\s+(\\w+)\\s*(=\\s*.*)?;");

        while ((line = reader.readLine()) != null) {
            // Detect package
            Matcher pkgMatcher = packagePattern.matcher(line);
            if (pkgMatcher.find()) {
                String[] parts = pkgMatcher.group(1).split("\\.");
                String pkg = parts[parts.length - 1];
                if (!isExcludedVariable(pkg)) {
                    entities.add(new String[]{pkg, "Package"});
                }
            }

            // Detect import
            Matcher importMatcher = importPattern.matcher(line);
            if (importMatcher.find()) {
                String fullImport = importMatcher.group(1);  // e.g., java.util.Scanner
                if (!isExcludedVariable(fullImport)) {
                    entities.add(new String[]{fullImport, "Import"});
                }
            }
            // Detect class
            Matcher classMatcher = classPattern.matcher(line);
            if (classMatcher.find()) {
                entities.add(new String[]{classMatcher.group(1), "Class"});
            }

            // Detect methods
            Matcher methodMatcher = methodPattern.matcher(line);
            while (methodMatcher.find()) {
                String methodName = methodMatcher.group(2);
                if (!isExcludedVariable(methodName)) {
                    entities.add(new String[]{methodName, "Method"});
                }
            }

            // Detect variables
            Matcher varMatcher = variablePattern.matcher(line);
            while (varMatcher.find()) {
                String variableName = varMatcher.group(3); // use group 3 for variable name
                if (!isExcludedVariable(variableName)) {
                    entities.add(new String[]{variableName, "Variable"});
                }
            }
        }

        reader.close();
        return entities;
    }
    
    // ========== C#  ANALYSIS ==========

    private static void runCSharpAnalysis(String path) {
        try {
            List<File> csFiles = getFilesOrSingle(path, ".cs");

            if (csFiles.isEmpty()) {
                System.out.println("No valid C# files found.");
                return;
            }

            Map<File, List<EntityInfo>> fileEntityMap = new LinkedHashMap<>();

            for (File file : csFiles) {
                List<EntityInfo> entityList = new ArrayList<>();
                List<String[]> entities = extractCSharpEntities(file.getAbsolutePath());
                calculateComprehensibilityScoreUnified(entities, entityList);

                fileEntityMap.put(file, entityList);
            }

            writeDetailedCSV(fileEntityMap, "CSharp");
            writeSummaryCSV(fileEntityMap, "CSharp");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    private static List<String[]> extractCSharpEntities(String filePath) throws IOException {
        List<String[]> entities = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        // 1) Using / Import
        Pattern importPattern = Pattern.compile("^\\s*using\\s+([A-Za-z0-9_.]+);");

        // 2) Namespace → Package
        Pattern namespacePattern = Pattern.compile("^\\s*namespace\\s+([A-Za-z0-9_.]+)");

        // 3) Class declarations
        Pattern classPattern = Pattern.compile("\\bclass\\s+(\\w+)");

        // 4) Method / Constructor / Operator
        Pattern methodPattern = Pattern.compile(
            "\\b(?:public|private|protected|internal|static|virtual|override|async|unsafe|sealed|extern)?\\s*" +
            "(?:[A-Za-z_][A-Za-z0-9_<>,\\s]*?)\\s+" +
            "(~?\\w+)\\s*\\("
        );

        // 5) Variable / Field declarations
     // Only allow true C# types (int, string, var, dynamic, or user‐defined generic names), not "using"
        Pattern variablePattern = Pattern.compile(
          "^\\s*(?:public|private|protected|internal|static|readonly|volatile|const)?\\s+" 
        + "(?:bool|byte|char|decimal|double|float|int|long|object|string|var|dynamic|[A-Za-z_][A-Za-z0-9_<>,]*)\\s+" 
        + "([A-Za-z_][A-Za-z0-9_]*)\\b" 
        + "(?:\\s*(?:=|;))"
        );

        while ((line = reader.readLine()) != null) {
            Matcher m;

            // === 1. USING / IMPORT ===
            m = importPattern.matcher(line);
            while (m.find()) {
                String fullImport = m.group(1);
                if (!isExcludedVariable(fullImport)) {
                    entities.add(new String[]{ fullImport, "Import" });
                }
            }

            // === 2. NAMESPACE → Package ===
            m = namespacePattern.matcher(line);
            while (m.find()) {
                String pkg = m.group(1);
                if (!isExcludedVariable(pkg)) {
                    entities.add(new String[]{ pkg, "Package" });
                }
            }

            // === 3. CLASS DECLARATIONS ===
            m = classPattern.matcher(line);
            while (m.find()) {
                String cls = m.group(1);
                if (!isExcludedVariable(cls)) {
                    entities.add(new String[]{ cls, "Class" });
                }
            }

            // === 4. METHOD / CONSTRUCTOR / OPERATOR ===
            m = methodPattern.matcher(line);
            while (m.find()) {
                String methodName = m.group(1);
                if (!isExcludedVariable(methodName)) {
                    entities.add(new String[]{ methodName, "Method" });
                }
            }

            // === 5. VARIABLE / FIELD DECLARATIONS ===
            m = variablePattern.matcher(line);
            while (m.find()) {
                String varName = m.group(1);
                if (!isExcludedVariable(varName)) {
                    entities.add(new String[]{ varName, "Variable" });
                }
            }
        }

        reader.close();
        return entities;
    }

    // ========== PYTHON  ANALYSIS ==========
    
    private static void runPythonAnalysis(String path) {
        try {
            List<File> pyFiles = getFilesOrSingle(path, ".py");
            if (pyFiles.isEmpty()) {
                System.out.println("No valid Python files found.");
                return;
            }

            Map<File, List<EntityInfo>> fileEntityMap = new LinkedHashMap<>();

            for (File file : pyFiles) {
                List<EntityInfo> entityList = new ArrayList<>();
                List<String[]> entities = extractPythonEntities(file.getAbsolutePath());
                calculateComprehensibilityScoreUnified(entities, entityList);

                fileEntityMap.put(file, entityList);
            }

            writeDetailedCSV(fileEntityMap, "Python");
            writeSummaryCSV(fileEntityMap, "Python");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    
    private static List<String[]> extractPythonEntities(String filePath) throws IOException {
        List<String[]> entities = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        Pattern importPattern = Pattern.compile("^\\s*import\\s+(\\w+)");
        Pattern fromImportPattern = Pattern.compile("^\\s*from\\s+(\\w+)\\s+import\\s+(\\w+)(?:\\s+as\\s+(\\w+))?");
        Pattern classPattern = Pattern.compile("^\\s*class\\s+(\\w+)");
        Pattern methodPattern = Pattern.compile("^\\s*def\\s+(\\w+)\\s*\\(");
        Pattern variablePattern = Pattern.compile("^\\s*(\\w+)\\s*=\\s*");

        while ((line = reader.readLine()) != null) {
            Matcher m;

            Matcher importMatcher = importPattern.matcher(line);
            if (importMatcher.find()) {
                String module = importMatcher.group(1);
                if (!isExcludedVariable(module)) {
                    entities.add(new String[]{module, "Module"});
                }
            }

            Matcher fromImportMatcher = fromImportPattern.matcher(line);
            if (fromImportMatcher.find()) {
                String module = fromImportMatcher.group(1);
                String symbol = fromImportMatcher.group(2);
                String alias = fromImportMatcher.group(3); // May be null

                if (!isExcludedVariable(module)) {
                    entities.add(new String[]{module, "Module"});
                }
                if (!isExcludedVariable(symbol)) {
                    entities.add(new String[]{symbol, "Imported Symbol"});
                }
                if (alias != null && !isExcludedVariable(alias)) {
                    entities.add(new String[]{alias, "Alias"});
                }
            }


            m = classPattern.matcher(line);
            if (m.find()) {
                entities.add(new String[]{m.group(1), "Class"});
            }

            m = methodPattern.matcher(line);
            if (m.find()) {
                entities.add(new String[]{m.group(1), "Method"});
            }

            m = variablePattern.matcher(line);
            if (m.find()) {
                String var = m.group(1);
                if (!isExcludedVariable(var)) {
                    entities.add(new String[]{var, "Variable"});
                }
            }
        }

        reader.close();
        return entities;
    }
    
    
    // ========== JAVASCRIPT  ANALYSIS ==========
    
    private static void runJavaScriptAnalysis(String path) {
        try {
            List<File> jsFiles = getFilesOrSingle(path, ".js");

            if (jsFiles.isEmpty()) {
                System.out.println("No valid JavaScript files found.");
                return;
            }

            Map<File, List<EntityInfo>> fileEntityMap = new LinkedHashMap<>();

            for (File file : jsFiles) {
                List<EntityInfo> entityList = new ArrayList<>();
                List<String[]> entities = extractJavaScriptEntities(file.getAbsolutePath());
                calculateComprehensibilityScoreUnified(entities, entityList);

                fileEntityMap.put(file, entityList);
            }

            writeDetailedCSV(fileEntityMap, "JavaScript");
            writeSummaryCSV(fileEntityMap, "JavaScript");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
 

    private static List<String[]> extractJavaScriptEntities(String filePath) throws IOException {
        List<String[]> entities = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;


        Pattern importPattern = Pattern.compile(
            "\\bimport\\s+(?:.*?\\s+from\\s+)?['\"]([^'\"]+)['\"];?"
        );

        Pattern classPattern = Pattern.compile("\\bclass\\s+(\\w+)");

    
        Pattern functionPattern = Pattern.compile("\\bfunction\\s+(\\w+)\\s*\\(");
        Pattern arrowFunctionPattern = Pattern.compile("\\b(?:const|let|var)\\s+(\\w+)\\s*=\\s*\\([^)]*\\)\\s*=>");


        Pattern variablePattern = Pattern.compile("\\b(?:let|const|var)\\s+(\\w+)\\b");

        while ((line = reader.readLine()) != null) {
            Matcher m;

     
            m = importPattern.matcher(line);
            while (m.find()) {
                String pkgPath = m.group(1);
                if (!isExcludedVariable(pkgPath)) {
                    // Label it "Package" so we’ll split on "/" or "." later if needed
                    entities.add(new String[]{ pkgPath, "Package" });
                }
            }

           
            m = classPattern.matcher(line);
            while (m.find()) {
                String clsName = m.group(1);
                if (!isExcludedVariable(clsName)) {
                    entities.add(new String[]{ clsName, "Class" });
                }
            }

            m = functionPattern.matcher(line);
            while (m.find()) {
                String fnName = m.group(1);
                if (!isExcludedVariable(fnName)) {
                    entities.add(new String[]{ fnName, "Method" });
                }
            }

         
            m = arrowFunctionPattern.matcher(line);
            while (m.find()) {
                String arrowFnName = m.group(1);
                if (!isExcludedVariable(arrowFnName)) {
                    entities.add(new String[]{ arrowFnName, "Method" });
                }
            }

            m = variablePattern.matcher(line);
            while (m.find()) {
                String varName = m.group(1);
                if (!isExcludedVariable(varName)) {
                    entities.add(new String[]{ varName, "Variable" });
                }
            }
        }

        reader.close();
        return entities;
    }

    // ========== C/C++ ANALYSIS ==========

    private static void runCppAnalysis(String path) {
        try {
            List<File> cppFiles = getFilesOrSingle(path, ".cpp");
            cppFiles.addAll(getFilesOrSingle(path, ".c"));
            if (cppFiles.isEmpty()) {
                System.out.println("No valid C/C++ files found.");
                return;
            }

            Map<File, List<EntityInfo>> fileEntityMap = new LinkedHashMap<>();

            for (File file : cppFiles) {
                List<EntityInfo> entityList = new ArrayList<>();
                List<String[]> entities = extractCppEntities(file.getAbsolutePath());
                calculateComprehensibilityScoreUnified(entities, entityList);
                fileEntityMap.put(file, entityList);
            }

            writeDetailedCSV(fileEntityMap, "Cpp");
            writeSummaryCSV(fileEntityMap, "Cpp");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private static List<String[]> extractCppEntities(String filePath) throws IOException {
        List<String[]> entities = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        Pattern includePattern = Pattern.compile("^\\s*#include\\s+[<\"](\\w+)");
        Pattern definePattern = Pattern.compile("^\\s*#define\\s+(\\w+)");
        Pattern structPattern = Pattern.compile("\\bstruct\\s+(\\w+)");
        Pattern classPattern = Pattern.compile("\\bclass\\s+(\\w+)");
        Pattern enumPattern = Pattern.compile("\\benum\\s+(\\w+)");
        Pattern typedefPattern = Pattern.compile("\\btypedef\\s+\\w+\\s+(\\w+)");
        Pattern unionPattern = Pattern.compile("\\bunion\\s+(\\w+)");
        Pattern namespacePattern = Pattern.compile("\\bnamespace\\s+(\\w+)");
        Pattern templatePattern = Pattern.compile("\\btemplate\\s*<[^>]+>\\s*(class|typename)?\\s*(\\w+)?");
        Pattern functionPattern = Pattern.compile("\\b(\\w+)\\s+(\\w+)\\s*\\(");
        Pattern variablePattern = Pattern.compile("\\b(\\w+)\\s+(\\w+)\\s*(=\\s*[^;]+)?;");

        while ((line = reader.readLine()) != null) {
            Matcher m;

            m = includePattern.matcher(line);
            if (m.find()) entities.add(new String[]{m.group(1), "Include"});

            m = definePattern.matcher(line);
            if (m.find()) entities.add(new String[]{m.group(1), "Macro"});

            m = structPattern.matcher(line);
            if (m.find()) entities.add(new String[]{m.group(1), "Struct"});

            m = classPattern.matcher(line);
            if (m.find()) entities.add(new String[]{m.group(1), "Class"});

            m = enumPattern.matcher(line);
            if (m.find()) entities.add(new String[]{m.group(1), "Enum"});

            m = typedefPattern.matcher(line);
            if (m.find()) entities.add(new String[]{m.group(1), "Typedef"});

            m = unionPattern.matcher(line);
            if (m.find()) entities.add(new String[]{m.group(1), "Union"});

            m = namespacePattern.matcher(line);
            if (m.find()) entities.add(new String[]{m.group(1), "Namespace"});

            m = templatePattern.matcher(line);
            if (m.find()) entities.add(new String[]{m.group(2) != null ? m.group(2) : "T", "Template"});

            m = functionPattern.matcher(line);
            while (m.find()) {
                String func = m.group(2);
                if (!isExcludedVariable(func)) {
                    entities.add(new String[]{func, "Function"});
                }
            }

            m = variablePattern.matcher(line);
            while (m.find()) {
                String var = m.group(2);
                if (!isExcludedVariable(var)) {
                    entities.add(new String[]{var, "Variable"});
                }
            }
        }

        reader.close();
        return entities;
    }

    
    // ========== File & Dictionary Utilities  ==========
    
    
    // Returns files if path is folder, or single file if valid
    private static List<File> getFilesOrSingle(String path, String ext) {
        List<File> files = new ArrayList<>();
        File target = new File(path);
        if (target.isFile() && target.getName().endsWith(ext)) {
            files.add(target);
        } else if (target.isDirectory()) {
            files = getFilesWithExtension(target, ext);
        }
        return files;
    }
    
    private static List<File> getFilesWithExtension(File dir, String ext) {
        List<File> files = new ArrayList<>();
        File[] items = dir.listFiles();
        if (items != null) {
            for (File file : items) {
                if (file.isDirectory()) {
                    files.addAll(getFilesWithExtension(file, ext));
                } else if (file.getName().endsWith(ext)) {
                    files.add(file);
                }
            }
        }
        return files;
    }
    
    // Loads a dictionary file into a HashSet for lookup
    private static Set<String> loadDictionary(String filePath) {
        Set<String> dictionary = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionary.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
        }
        return dictionary;
    }
    
    // Helper to skip excluded variables like i, j
    private static boolean isExcludedVariable(String variable) {
        for (String excluded : EXCLUDED_VARIABLES) {
            if (excluded.equals(variable)) return true;
        }
        return false;
    }
    
    
    

    // ========== Scoring Logic ==========
    private static double evaluateEntityScore(String entity, String type) {
        if (type.equals("Import")) {
            // Split on dot, score each part, and average
            String[] parts = entity.split("\\.");
            double total = 0.0;
            int count = 0;
            for (String part : parts) {
                double pieceScore = evaluateWordScore(part.toLowerCase());
                total += pieceScore;
                count++;
            }
            return (count == 0) ? 0.0 : (total / count);
        }

        // For Package / Class / Method / Variable, use camelCase & punctuation split rules
        String[] words = entity.split(
            "(?<=[a-z])(?=[A-Z])" +       // camelCase split
            "|(?<=[A-Z])(?=[A-Z][a-z])" + // ALLCaps → ProperCase
            "|(?<=[a-z])(?=[0-9])" +      // letter→digit
            "|(?<=[0-9])(?=[A-Za-z])" +   // digit→letter
            "|[-_]"                       // hyphens or underscores
        );
        if (words.length == 0) return 0.0;

        double totalScore = 0.0;
        for (String w : words) {
            totalScore += evaluateWordScore(w.toLowerCase());
        }
        return totalScore / words.length;
    }

    // Delegates to full or partial match scoring
    private static double evaluateWordScore(String word) {
        String lowerWord = word.toLowerCase();

        // Built-in readable tokens
        if (BUILTIN_READABLES.contains(lowerWord)) return 1.0;

        // Existing dictionary checks
        if (isFullMatch(word)) return 1.0;
        return getPartialMatchScore(word);
    }

    private static boolean isFullMatch(String word) {
        return word.length() >= 3 && LOCAL_DICTIONARY.contains(word.toLowerCase());
    }

    private static double getPartialMatchScore(String word) {
        String lowercaseWord = word.toLowerCase();
        if (lowercaseWord.length() < 3) return 0.0;

        for (String dictWord : LOCAL_DICTIONARY) {
            if (dictWord.length() >= 3) {
                for (int i = 0; i <= lowercaseWord.length() - 3; i++) {
                    String substring = lowercaseWord.substring(i, i + 3);
                    if (dictWord.contains(substring)) {
                        return 0.5;
                    }
                }
            }
        }
        return 0.0;
    }
    
    
    // ========== Comprehensibility Score Calculator ==========
  
    private static void calculateComprehensibilityScoreUnified(List<String[]> entities, List<EntityInfo> resultList) {
        for (String[] entityData : entities) {
            String entity = entityData[0];
            String type = entityData[1];
             double score = evaluateEntityScore(entity, type);
;

            String readability;
            if (score == 1) readability = "Well Readable";
            else if (score >= 0.5) readability = "Moderate Readable";
            else readability = "Non Readable";

            resultList.add(new EntityInfo(entity, type, score, readability));
        }
    }

    
    
    
    // ========== CSV Generation CODE ==========
    
    
    private static void writeDetailedCSV(Map<File, List<EntityInfo>> fileEntityMap, String language) {
        String fileName = language + "_Detailed_Comprehensibility_Report.csv";
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println("Class Name with Path,Entity Name,Entity Type,Comprehensibility Score,Comprehensibility Category");

            for (Map.Entry<File, List<EntityInfo>> entry : fileEntityMap.entrySet()) {
                String filePath = entry.getKey().getPath();
                List<EntityInfo> entities = entry.getValue();

                // Print file/class path as a section header (in first column)
                writer.printf("%s,,,,%n", filePath);

                // Print entity rows (leave file column blank)
                for (EntityInfo info : entities) {
                    writer.printf(",%s,%s,%.2f,%s%n",
                            info.entity, info.type, info.score, info.readability);
                }
            }

            System.out.println("Detailed CSV report generated: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing detailed CSV: " + e.getMessage());
        }
    }

    private static void writeSummaryCSV(Map<File, List<EntityInfo>> fileEntityMap, String language) {
        String fileName = language + "_Summary_Comprehensibility_Report.csv";
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println("Class Name with Path,Comprehensibility Score");

            double total = 0.0;
            int count = 0;

            for (Map.Entry<File, List<EntityInfo>> entry : fileEntityMap.entrySet()) {
                List<EntityInfo> entities = entry.getValue();
                double fileScore = entities.stream().mapToDouble(e -> e.score).average().orElse(0.0);
                writer.printf("%s,%.2f%n", entry.getKey().getPath(), fileScore);
                total += fileScore;
                count++;
            }

            double average = count == 0 ? 0.0 : total / count;
            writer.printf("Average Comprehensibility Score,%.2f%n", average);
            System.out.println("Summary CSV report generated: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing summary CSV: " + e.getMessage());
        }
    }

}