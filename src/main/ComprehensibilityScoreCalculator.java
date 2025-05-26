package main;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class ComprehensibilityScoreCalculator {
	   // Variables to exclude from readability scoring (e.g., loop counters)
    private static final String[] EXCLUDED_VARIABLES = {"i", "j"};

    // Dictionary for scoring, loaded once when the program starts
    private static final Set<String> LOCAL_DICTIONARY = loadDictionary("words_alpha2.txt");
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
        System.out.println("2. C# (Coming soon)");
        System.out.println("3. Python");
        System.out.println("4. Others");
        System.out.print("Enter your choice (1–4): ");
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
                System.out.println("C# analysis is not implemented yet.");
                break;
            case 3:
                runPythonAnalysis(folderPath);
                break;
            case 4:
                System.out.println("Other language support not available yet.");
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
                calculateComprehensibilityScore(file.getAbsolutePath(), entityList);
                fileEntityMap.put(file, entityList);
            }
            // Generate two reports
            writeDetailedCSV(fileEntityMap, "Java");
            writeSummaryCSV(fileEntityMap, "Java");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
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
    
    // Calculates comprehensibility score for one file's entities
    public static double calculateComprehensibilityScore(String filePath, List<EntityInfo> resultList) throws IOException {
        List<String[]> entities = extractEntities(filePath);
        double totalScore = 0.0;
        int totalCount = 0;

        for (String[] entityData : entities) {
            String entity = entityData[0];
            String type = entityData[1];
            double score = evaluateEntityScore(entity);

            String readability;
            if (score == 1 ) {
                readability = "Well Readable";
            } else if (score >= 0.5) {
                readability = "Moderate Readable";
            } else {
                readability = "Non Readable";
            }

            resultList.add(new EntityInfo(entity, type, score, readability));
            totalScore += score;
            totalCount++;
        }

        return totalCount == 0 ? 0.0 : (totalScore / totalCount) * 100;
    }

    // Uses regex patterns to extract all relevant entities from Java files
    private static List<String[]> extractEntities(String filePath) throws IOException {
        List<String[]> entities = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        Pattern importPattern = Pattern.compile("import\\s+([a-zA-Z0-9_.]+);?");
        Pattern classNamePattern = Pattern.compile("class\\s+(\\w+)");
        Pattern methodNamePattern = Pattern.compile("(public|private|protected|static|final|void)\\s+\\w+\\s*(\\w+)\\s*\\(");
        Pattern variableNamePattern = Pattern.compile("\\b\\w+\\s+(\\w+)\\s*(?:=\\s*.*)?;");

        while ((line = reader.readLine()) != null) {
            // Match imported classes
            Matcher importMatcher = importPattern.matcher(line);
            if (importMatcher.find()) {
                String[] importParts = importMatcher.group(1).split("\\.");
                String importedClass = importParts[importParts.length - 1];
                if (!isExcludedVariable(importedClass)) {
                    entities.add(new String[]{importedClass, "Import"});
                }
            }

            Matcher classNameMatcher = classNamePattern.matcher(line);
            if (classNameMatcher.find()) {
                entities.add(new String[]{classNameMatcher.group(1), "Class"});
            }

            Matcher methodNameMatcher = methodNamePattern.matcher(line);
            while (methodNameMatcher.find()) {
                String methodName = methodNameMatcher.group(2);
                if (!isExcludedVariable(methodName)) {
                    entities.add(new String[]{methodName, "Method"});
                }
            }

            Matcher variableNameMatcher = variableNamePattern.matcher(line);
            while (variableNameMatcher.find()) {
                String variableName = variableNameMatcher.group(1);
                if (!isExcludedVariable(variableName)) {
                    entities.add(new String[]{variableName, "Variable"});
                }
            }
        }

        reader.close();
        return entities;
    }
    
    
    // Helper to skip excluded variables like i, j
    private static boolean isExcludedVariable(String variable) {
        for (String excluded : EXCLUDED_VARIABLES) {
            if (excluded.equals(variable)) return true;
        }
        return false;
    }
    // Breaks camelCase, snake_case, etc. and computes average word score
    private static double evaluateEntityScore(String entity) {
        String[] words = entity.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[a-z])(?=[0-9])|(?<=[0-9])(?=[A-Z])|[-_]");
        if (words.length == 0) return 0.0;

        double totalScore = 0.0;
        for (String word : words) {
            totalScore += evaluateWordScore(word.toLowerCase());
        }

        return totalScore / words.length;
    }
    
    // Delegates to full or partial match scoring
    private static double evaluateWordScore(String word) {
        if (isFullMatch(word)) return 1.0;
        return getPartialMatchScore(word);
    }

    private static boolean isFullMatch(String word) {
        return word.length() >= 4 && LOCAL_DICTIONARY.contains(word.toLowerCase());
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
    
    // ========== Python  ANALYSIS ==========
    
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
                calculatePythonScore(file.getAbsolutePath(), entityList);
                fileEntityMap.put(file, entityList);
            }

            writeDetailedCSV(fileEntityMap, "Python");
            writeSummaryCSV(fileEntityMap, "Python");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
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
    private static void calculatePythonScore(String filePath, List<EntityInfo> resultList) throws IOException {
        List<String[]> entities = extractPythonEntities(filePath);
        for (String[] entityData : entities) {
            String entity = entityData[0];
            String type = entityData[1];
            double score = evaluateEntityScore(entity);

            String readability;
            if (score == 1) readability = "Well Readable";
            else if (score >= 0.5) readability = "Moderate Readable";
            else readability = "Non Readable";

            resultList.add(new EntityInfo(entity, type, score, readability));
        }
    }

    private static List<String[]> extractPythonEntities(String filePath) throws IOException {
        List<String[]> entities = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        Pattern importPattern = Pattern.compile("^\\s*(import\\s+|from\\s+)(\\w+)");
        Pattern classPattern = Pattern.compile("^\\s*class\\s+(\\w+)");
        Pattern methodPattern = Pattern.compile("^\\s*def\\s+(\\w+)\\s*\\(");
        Pattern variablePattern = Pattern.compile("^\\s*(\\w+)\\s*=\\s*");

        while ((line = reader.readLine()) != null) {
            Matcher m;

            m = importPattern.matcher(line);
            if (m.find()) {
                entities.add(new String[]{m.group(2), "Import"});
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