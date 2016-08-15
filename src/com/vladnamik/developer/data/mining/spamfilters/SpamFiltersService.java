package com.vladnamik.developer.data.mining.spamfilters;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SpamFiltersService {

    public static final String PATH_TO_MESSAGES = "D:\\android и java\\spam datasets\\teach";

    /**
     * If word can be found in more than {@code MOST_COMMON_PERCENT_LIMIT}
     * percents of files it can be labeled as common.
     */
    private static final double MOST_COMMON_WORD_PERCENT_LIMIT = 0.1;

    public static void uploadDataFromDirectory(SpamFilter spamFilter, String path, boolean isSpam) throws IOException {
        Files.walk(Paths.get(path)).forEach(filePath -> {
            try {
                uploadDataFromFile(spamFilter, filePath, isSpam);
            } catch (IOException e) {
                if (Files.isRegularFile(filePath))
                    e.printStackTrace();
            }
        });
    }

    public static void uploadDataFromFile(SpamFilter spamFilter, Path path, boolean isSpam) throws IOException {
        spamFilter.addMessage(fileToString(path), isSpam);
    }

    public static String fileToString(Path path) throws IOException {
        final StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            reader.lines().forEach(str -> builder.append(str).append(" "));
        }
        return toAddableMessage(builder.toString());
    }

    public static double getPercentOfCorrectAnswers(SpamFilter spamFilter, String path,
                                                    boolean isSpam) throws IOException {
        int[] messagesNumber = {0};
        int[] correctAnswersNumber = {0};

        Files.walk(Paths.get(path)).forEach(filePath -> {
            try {
                if (spamFilter.isSpam(fileToString(filePath)) == isSpam) {
                    correctAnswersNumber[0]++;
                }
                messagesNumber[0]++;
            } catch (IOException e) {
                //arise when we have a directory instead of file
                if (Files.isRegularFile(filePath))
                    e.printStackTrace();
            }
            System.out.println(messagesNumber[0]); //delete this someday
        });
        return (double) correctAnswersNumber[0] * 100 / messagesNumber[0];
    }

    /**
     * Walks on directories with spam and not spam files
     * and counts a percents of correct answers, that spam filter
     * returns.
     * Returns array with 3 numbers:
     * 1) Percent of correct answers for spam messages;
     * 2) Percent of correct answers for not spam messages;
     * 3) Total percent of correct answers (accuracy).
     *
     * @param spamFilter    spam filter object.
     * @param pathToSpam    path to directory or file with spam messages.
     * @param pathToNotSpam path to directory or file with not spam messages.
     * @return array with 3 numbers
     * @throws IOException
     */
    public static double[] getPercentOfAllCorrectAnswers(SpamFilter spamFilter, String pathToSpam,
                                                         String pathToNotSpam) throws IOException {
        double percentOfCorrectSpamAnswers = SpamFiltersService
                .getPercentOfCorrectAnswers(spamFilter, pathToSpam, true);
        int spamCount = countFiles(pathToSpam);

        double percentOfCorrectNotSpamAnswers = SpamFiltersService
                .getPercentOfCorrectAnswers(spamFilter, pathToNotSpam, false);
        int notSpamCount = countFiles(pathToNotSpam);

        double totalPercentOfCorrectAnswers = (notSpamCount * percentOfCorrectNotSpamAnswers +
                spamCount * percentOfCorrectSpamAnswers) / (notSpamCount + spamCount);

        return new double[]{percentOfCorrectSpamAnswers, percentOfCorrectNotSpamAnswers, totalPercentOfCorrectAnswers};
    }

    public static int countFiles(String path) throws IOException {
        int[] count = {0};
        Files.walk(Paths.get(path)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                count[0]++;
            }
        });
        return count[0];
    }

    public static String[] getWordsFromMessage(String message) {
        String[] words = message.split("[\\W\\s]");
        Arrays.stream(words).forEach(SpamFiltersService::toAddableWord);
        return words;
    }

    public static void saveWordsToFile(Collection<String> words, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            words.forEach(word -> {
                try {
                    writer.write(word + " ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static Map<String, Integer> getWordsQuantitiesFromMessage(String message) {
        Map<String, Integer> wordsQuantities = new HashMap<>();
        Arrays.stream(SpamFiltersService.getWordsFromMessage(toAddableMessage(message))).forEach(word -> {
            String addableWord = toAddableWord(word);
            if (wordsQuantities.containsKey(addableWord)) {
                wordsQuantities.put(addableWord, wordsQuantities.get(addableWord) + 1);
            } else {
                wordsQuantities.put(addableWord, 1);
            }
        });
        return wordsQuantities;
    }

    public static String toAddableWord(String word) {
        return word.toLowerCase().trim();
    }

    public static String toAddableMessage(String message) {
        return message.toLowerCase().trim();
    }

    public static Set<String> getAllWordsFromFiles(String path) throws IOException {
        Set<String> words = new HashSet<>();
        Files.walk(Paths.get(path)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
                    String line;
                    while (reader.ready()) {
                        line = reader.readLine();
                        words.addAll(Arrays.asList(getWordsFromMessage(toAddableMessage(line))));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return words;
    }

    public static Set<String> getMostCommonWords(String path) throws IOException {
        Map<String, Integer> filesWithWordQuantity = new HashMap<>();
        int[] filesQuantity = {0};

        Files.walk(Paths.get(path)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                Map<String, Integer> wordsQuantityFromMessage;
                filesQuantity[0]++;

                try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
                    StringBuilder fileBuffer = new StringBuilder();
                    reader.lines().forEach(line -> fileBuffer.append(line).append(" "));
                    wordsQuantityFromMessage = getWordsQuantitiesFromMessage(toAddableMessage(fileBuffer.toString()));

                    wordsQuantityFromMessage.forEach((word, quantity) -> {
                        if (filesWithWordQuantity.containsKey(word)) {
                            filesWithWordQuantity.put(word, filesWithWordQuantity.get(word) + 1);
                        } else {
                            filesWithWordQuantity.put(word, 0);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Set<String> mostCommonWords = new HashSet<>();
        filesWithWordQuantity.forEach((word, quantity) -> {
            if ((double) quantity / filesQuantity[0] > MOST_COMMON_WORD_PERCENT_LIMIT) {
                mostCommonWords.add(word);
            }
        });

        return mostCommonWords;
    }
}
