package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComprehensibilityScoreCalculator {

    // Weights for each category
    private static final double WELL_READABLE_WEIGHT = 1.0;
    private static final double MODERATE_READABLE_WEIGHT = 0.5;
    private static final double NON_READABLE_WEIGHT = 0.0;

    // List of general loop variables to exclude
    private static final String[] EXCLUDED_VARIABLES = {"i", "j"};

    // Local dictionary
    private static final Set<String> LOCAL_DICTIONARY = loadDictionary("dictionary.txt");

    public static void main(String[] args) {
        String filePath = "./TestFiles/ExampleClass.java"; // Path to the .java file
        try {
            double comprehensibilityScore = calculateComprehensibilityScore(filePath);
            System.out.printf("Comprehensibility Score: %.2f%%\n", comprehensibilityScore);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    /**
     * Loads the local dictionary from a file.
     *
     * @param filePath Path to the dictionary file.
     * @return A set of words from the dictionary.
     */
    private static Set<String> loadDictionary(String filePath) {
        Set<String> dictionary = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionary.add(line.trim().toLowerCase()); // Add words in lowercase
            }
        } catch (IOException e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
        }
        return dictionary;
    }

    /**
     * Calculates the comprehensibility score of a .java class file.
     *
     * @param filePath Path to the .java file.
     * @return The comprehensibility score as a percentage.
     * @throws IOException If the file cannot be read.
     */
    public static double calculateComprehensibilityScore(String filePath) throws IOException {
        List<String> entities = extractEntities(filePath);
        int wellReadableCount = 0;
        int moderateReadableCount = 0;
        int nonReadableCount = 0;

        // Categorize each entity
        for (String entity : entities) {
            if (isWellReadable(entity)) {
                System.out.println("Well Readable: " + entity);
                wellReadableCount++;
            } else if (isModerateReadable(entity)) {
                System.out.println("Moderate Readable: " + entity);
                moderateReadableCount++;
            } else {
                System.out.println("Non Readable: " + entity);
                nonReadableCount++;
            }
        }

        // Calculate the comprehensibility score
        int totalEntities = wellReadableCount + moderateReadableCount + nonReadableCount;
        if (totalEntities == 0) {
            return 0.0; // Avoid division by zero
        }

        double score = (wellReadableCount * WELL_READABLE_WEIGHT +
                        moderateReadableCount * MODERATE_READABLE_WEIGHT +
                        nonReadableCount * NON_READABLE_WEIGHT) * 100 / totalEntities;

        return score;
    }

    /**
     * Extracts the bag of entities (class name, method names, variable names) from a .java file.
     *
     * @param filePath Path to the .java file.
     * @return A list of entities.
     * @throws IOException If the file cannot be read.
     */
    private static List<String> extractEntities(String filePath) throws IOException {
        List<String> entities = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        // Regex patterns to match class names, method names, and variable names
        Pattern classNamePattern = Pattern.compile("class\\s+(\\w+)");
        Pattern methodNamePattern = Pattern.compile("(public|private|protected|static|final|void)\\s+\\w+\\s*(\\w+)\\s*\\(");
        Pattern variableNamePattern = Pattern.compile("\\s*(\\w+)\\s*(?:=\\s*.*)?;");

        while ((line = reader.readLine()) != null) {
            // Match class name
            Matcher classNameMatcher = classNamePattern.matcher(line);
            if (classNameMatcher.find()) {
                String className = classNameMatcher.group(1);
                System.out.println("Class Name: " + className);
                entities.add(className);
            }

            // Match method names
            Matcher methodNameMatcher = methodNamePattern.matcher(line);
            while (methodNameMatcher.find()) {
                String methodName = methodNameMatcher.group(2);
                if (!isExcludedVariable(methodName)) {
                    System.out.println("Method Name: " + methodName);
                    entities.add(methodName);
                }
            }

            // Match variable names
            Matcher variableNameMatcher = variableNamePattern.matcher(line);
            while (variableNameMatcher.find()) {
                String variableName = variableNameMatcher.group(1);
                if (!isExcludedVariable(variableName)) {
                    System.out.println("Variable Name: " + variableName);
                    entities.add(variableName);
                }
            }
        }

        reader.close();
        return entities;
    }

    /**
     * Checks if a variable is excluded (e.g., general loop variables like i, j).
     *
     * @param variable The variable to check.
     * @return True if the variable is excluded, false otherwise.
     */
    private static boolean isExcludedVariable(String variable) {
        for (String excluded : EXCLUDED_VARIABLES) {
            if (excluded.equals(variable)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if an entity is well readable.
     *
     * @param entity The entity to check.
     * @return True if the entity is well readable, false otherwise.
     */
    private static boolean isWellReadable(String entity) {
        // Split camel case into individual words
        String[] words = entity.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        for (String word : words) {
            if (!isFullMatch(word)) {
                return false; // If any word is not a full match, the entity is not well readable
            }
        }
        return true; // All words are full matches
    }

    /**
     * Checks if an entity is moderately readable.
     *
     * @param entity The entity to check.
     * @return True if the entity is moderately readable, false otherwise.
     */
    private static boolean isModerateReadable(String entity) {
        // Split camel case into individual words
        String[] words = entity.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        for (String word : words) {
            if (isPartialMatch(word)) {
                return true; // If at least one word is a partial match, the entity is moderately readable
            }
        }
        return false; // No words are partial matches
    }

    /**
     * Checks if a word fully matches a dictionary word.
     *
     * @param word The word to check.
     * @return True if the word fully matches a dictionary word, false otherwise.
     */
    private static boolean isFullMatch(String word) {
        return LOCAL_DICTIONARY.contains(word.toLowerCase());
    }

    /**
     * Checks if a word partially matches a dictionary word (at least 3 consecutive letters).
     *
     * @param word The word to check.
     * @return True if the word partially matches a dictionary word, false otherwise.
     */
    private static boolean isPartialMatch(String word) {
        String lowercaseWord = word.toLowerCase();
        for (String dictWord : LOCAL_DICTIONARY) {
            if (dictWord.length() >= 3 && lowercaseWord.length() >= 3) {
                // Check if at least 3 consecutive letters match
                for (int i = 0; i <= lowercaseWord.length() - 3; i++) {
                    String substring = lowercaseWord.substring(i, i + 3);
                    if (dictWord.contains(substring)) {
                        return true; // Partial match found
                    }
                }
            }
        }
        return false; // No partial match found
    }
}