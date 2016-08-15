package com.vladnamik.developer.data.mining.spamfilters;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class BayesSpamFilter implements SpamFilter {
    private Map<String, Double> wordSpamProbabilityDictionary = new HashMap<>();
    private Map<String, Double> normalizedWordSpamProbabilityDictionary = new HashMap<>();
    private Map<String, Integer> wordQuantityDictionary = new HashMap<>();// сколько раз слово встречалось всего
    private Map<String, Integer> wordQuantityInSpamDictionary = new HashMap<>();//сколько раз слово встречалось в спам-сообщении
    private Integer messagesQuantity = 0;
    private Integer spamMessagesQuantity = 0;
    private Double spamProbability = 0.5;

    public BayesSpamFilter(double spamProbability) {
        this.spamProbability = spamProbability;
    }

    public BayesSpamFilter() {

    }


    public double normalizeProbabilityOfSingleWord(int wordQuantity, double baseProbability) {
        double smth = wordQuantity * baseProbability;
        return (wordQuantity * baseProbability + 0.5) / (wordQuantity + 1); // 0.5 = 1 / classes_quantity
    }

    public Map<String, Double> toNormilizedDictionary(Map<String, Double> wordProbabilityDictionary, Map<String, Integer> wordQuantityDictionary) {
        Map<String, Double> normalizedDictionary = new HashMap<>();
        wordProbabilityDictionary
                .entrySet()
                .forEach(x ->
                        normalizedDictionary.put(x.getKey(),
                                normalizeProbabilityOfSingleWord(wordQuantityDictionary.get(x.getKey()), x.getValue())
                        )
                );
        return normalizedDictionary;
    }

    public boolean isSpam(String message) {
        updateNormilizedDictionary();
        updateSpamProbability();
        double messageSpamProbability = 1d;
        double messageNotSpamProbability = 1d;

        String[] wordsFromMessage = SpamFiltersService
                .getWordsFromMessage(SpamFiltersService.toAddableMessage(message));

        for (String word : wordsFromMessage) {
            if (normalizedWordSpamProbabilityDictionary.containsKey(word) && !word.equals("")) {
                messageSpamProbability *= normalizedWordSpamProbabilityDictionary.get(word);
                messageNotSpamProbability *= 1d - normalizedWordSpamProbabilityDictionary.get(word);
            }
        }
        messageSpamProbability *= spamProbability;
        messageNotSpamProbability *= 1d - spamProbability;
        return messageSpamProbability > messageNotSpamProbability;
    }

    public void updateNormilizedDictionary() {
        if (normalizedWordSpamProbabilityDictionary.size() == wordSpamProbabilityDictionary.size())
            return;

        normalizedWordSpamProbabilityDictionary = toNormilizedDictionary(wordSpamProbabilityDictionary, wordQuantityDictionary);
    }

    public void updateSpamProbability() {
//        spamProbability = (double) spamMessagesQuantity / messagesQuantity;
    }

    public void clearInfo() {
        wordQuantityDictionary.clear();
        wordSpamProbabilityDictionary.clear();
        normalizedWordSpamProbabilityDictionary.clear();
        wordQuantityInSpamDictionary.clear();
        messagesQuantity = 0;
        spamMessagesQuantity = 0;
    }

    public void addMessage(String message, boolean isSpam) {
        String[] words = SpamFiltersService.getWordsFromMessage(SpamFiltersService.toAddableMessage(message));

        messagesQuantity++;
        if (isSpam) {
            spamMessagesQuantity++;
        }

        if (isSpam) //если спам
            for (String word : words) {
                if (!word.equals("")) {
                    if (wordQuantityDictionary.containsKey(word)) {
                        wordQuantityDictionary.put(word, wordQuantityDictionary.get(word) + 1);
                        wordQuantityInSpamDictionary.put(word, wordQuantityInSpamDictionary.get(word) + 1);

                        double wordQuantityInSpam = (double) wordQuantityInSpamDictionary.get(word);
                        double wordSpamProbability = (wordQuantityInSpam / spamMessagesQuantity) /
                                ((wordQuantityInSpam / spamMessagesQuantity) +
                                        (wordQuantityDictionary.get(word) - wordQuantityInSpam)
                                                / (messagesQuantity - spamMessagesQuantity));
                        if (messagesQuantity - spamMessagesQuantity == 0)
                            wordSpamProbability = 1d;
                        else if (spamMessagesQuantity == 0)
                            wordSpamProbability = 0d;
                        wordSpamProbabilityDictionary.put(word, wordSpamProbability);
                    } else {
                        wordQuantityDictionary.put(word, 1);
                        wordQuantityInSpamDictionary.put(word, 1);
                        wordSpamProbabilityDictionary.put(word, 1d);
                    }
                }
            }
        else //если не спам
            for (String word : words) {
                if (!word.equals("")) {
                    if (wordQuantityDictionary.containsKey(word)) {
                        wordQuantityDictionary.put(word, wordQuantityDictionary.get(word) + 1);

                        double wordQuantityInSpam = (double) wordQuantityInSpamDictionary.get(word);
                        double wordSpamProbability = (wordQuantityInSpam / spamMessagesQuantity) /
                                ((wordQuantityInSpam / spamMessagesQuantity) +
                                        (wordQuantityDictionary.get(word) - wordQuantityInSpam)
                                                / (messagesQuantity - spamMessagesQuantity));
                        if (messagesQuantity - spamMessagesQuantity == 0)
                            wordSpamProbability = 1d;
                        else if (spamMessagesQuantity == 0)
                            wordSpamProbability = 0d;
                        wordSpamProbabilityDictionary.put(word, wordSpamProbability);
                    } else {
                        wordQuantityDictionary.put(word, 1);
                        wordQuantityInSpamDictionary.put(word, 0);
                        wordSpamProbabilityDictionary.put(word, 0d);
                    }
                }
            }
    }

    @Deprecated
    private void dontUseThisAddMessage(String message, boolean isSpam) {
        String[] words = message.toLowerCase().split("[\\W \\n\\t]");

        messagesQuantity++;
        if (isSpam) {
            spamMessagesQuantity++;
        }

        if (isSpam)
            for (String word : words) {
                if (!word.equals("")) {
                    if (wordQuantityDictionary.containsKey(word)) {
                        wordSpamProbabilityDictionary.put(word,
                                (wordSpamProbabilityDictionary.get(word) * wordQuantityDictionary.get(word) + 1) /
                                        (wordQuantityDictionary.get(word) + 1)
                        );
                        wordQuantityDictionary.put(word, wordQuantityDictionary.get(word) + 1);
                    } else {
                        wordQuantityDictionary.put(word, 1);
                        wordSpamProbabilityDictionary.put(word, 1d);
                    }
                }
            }
        else
            for (String word : words) {
                if (!word.equals("")) {
                    if (wordQuantityDictionary.containsKey(word)) {
                        wordSpamProbabilityDictionary.put(word,
                                (wordSpamProbabilityDictionary.get(word) * wordQuantityDictionary.get(word)) /
                                        (wordQuantityDictionary.get(word) + 1)
                        );
                        wordQuantityDictionary.put(word, wordQuantityDictionary.get(word) + 1);
                    } else {
                        wordQuantityDictionary.put(word, 1);
                        wordSpamProbabilityDictionary.put(word, 0d);
                    }
                }
            }
    }

    //аргумент — словарь: сообщение и является ли оно спамом
    public void addMessages(Map<String, Boolean> messages) {
        messages.entrySet().forEach(x -> addMessage(x.getKey(), x.getValue()));
    }

    public Map<String, Double> getWordSpamProbabilityDictionary() {
        return wordSpamProbabilityDictionary;
    }

    public Map<String, Double> getNormalizedWordSpamProbabilityDictionary() {
        updateNormilizedDictionary();
        updateSpamProbability();
        return normalizedWordSpamProbabilityDictionary;
    }

    public Map<String, Integer> getWordQuantityDictionary() {
        return wordQuantityDictionary;
    }

    public double getSpamProbability() {
        updateSpamProbability();
        return spamProbability;
    }

    public void saveData(String filePath) throws IOException {
        updateSpamProbability();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(spamProbability.toString());
            writer.newLine();
            writer.write(spamMessagesQuantity.toString());
            writer.newLine();
            writer.write(messagesQuantity.toString());
            writer.newLine();
            for (Map.Entry<String, Integer> word : wordQuantityDictionary.entrySet()) {
                writer.write(word.getKey() + ":" + word.getValue() + " ");
            }
            writer.newLine();
            writer.newLine();
            for (Map.Entry<String, Double> word : wordSpamProbabilityDictionary.entrySet()) {
                writer.write(word.getKey() + ":" + word.getValue() + " ");
            }
            writer.newLine();
            writer.newLine();
            for (Map.Entry<String, Integer> word : wordQuantityInSpamDictionary.entrySet()) {
                writer.write(word.getKey() + ":" + word.getValue() + " ");
            }
            writer.flush();
        }

    }

    //при этом остальные данные обнулятся
    public void takeDataFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            spamProbability = Double.parseDouble(reader.readLine());

            String line = reader.readLine();
            spamMessagesQuantity = Integer.parseInt(line);

            line = reader.readLine();
            messagesQuantity = Integer.parseInt(line);

            wordQuantityDictionary.clear();
            wordSpamProbabilityDictionary.clear();
            wordQuantityInSpamDictionary.clear();

            line = reader.readLine();
            while (reader.ready() && !line.equals("")) {
                String[] quantities = line.split(" ");
                for (String quantity : quantities) {
                    String[] keyValue = quantity.split(":");
                    wordQuantityDictionary.put(keyValue[0], Integer.parseInt(keyValue[1]));
                }
                line = reader.readLine();
            }

            line = reader.readLine();
            while (reader.ready() && !line.equals("")) {
                String[] probabilities = line.split(" ");
                for (String probability : probabilities) {
                    String[] keyValue = probability.split(":");
                    wordSpamProbabilityDictionary.put(keyValue[0], Double.parseDouble(keyValue[1]));
                }
                line = reader.readLine();
            }

            while (reader.ready()) {
                line = reader.readLine();
                String[] quantities = line.split(" ");
                for (String quantity : quantities) {
                    String[] keyValue = quantity.split(":");
                    wordQuantityInSpamDictionary.put(keyValue[0], Integer.parseInt(keyValue[1]));
                }
            }
        }
    }
}
