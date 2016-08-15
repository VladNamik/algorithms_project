package com.vladnamik.developer.data.mining.spamfilters;


import javafx.util.Pair;

import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class NearestNeighbourSpamFilter implements SpamFilter {
    /**
     * Nearest neighbors quantity.
     */
    private int k_nearest_neighbors = 20;
    private static final double LAMBDA = 0.5;

    private Map<String, Boolean> messages = new HashMap<>(); // true, if spam
    private Map<String, Map<String, Integer>> wordsQuantitiesFromMessage = new HashMap<>();

    public NearestNeighbourSpamFilter(int k_nearest_neighbors) {
        if (k_nearest_neighbors >= 1) {
            this.k_nearest_neighbors = k_nearest_neighbors;
        }
    }

    public NearestNeighbourSpamFilter() {
    }

//    public double distance(String firstMessage, String secondMessage) {
//        Map<String, Integer> wordsFromFirstMessage;
//        if (wordsQuantitiesFromMessage.containsKey(firstMessage)) {
//            wordsFromFirstMessage = wordsQuantitiesFromMessage.get(firstMessage);
//        } else {
//            wordsFromFirstMessage = SpamFiltersService.getWordsQuantitiesFromMessage(firstMessage);
//        }
//        Map<String, Integer> wordsFromSecondMessage;
//        if (wordsQuantitiesFromMessage.containsKey(secondMessage)) {
//            wordsFromSecondMessage = wordsQuantitiesFromMessage.get(secondMessage);
//        } else {
//            wordsFromSecondMessage = SpamFiltersService.getWordsQuantitiesFromMessage(secondMessage);
//        }
//
//        Set<String> allWords = new HashSet<>();
//        allWords.addAll(wordsFromFirstMessage.keySet());
//        allWords.addAll(wordsFromSecondMessage.keySet());
//        int wordsInFirstMessage = SpamFiltersService.getWordsFromMessage(firstMessage).length;
//        int wordsInSecondMessage = SpamFiltersService.getWordsFromMessage(secondMessage).length;
//
//        double[] distance = {0.0, 0.0}; // a, b
//        allWords.forEach((word) -> {
//
//            int inFirstMessageQuantity = 0;
//            int inSecondMessageQuantity = 0;
//            if (wordsFromFirstMessage.containsKey(word)) {
//                inFirstMessageQuantity = wordsFromFirstMessage.get(word);
//            }
//            if (wordsFromSecondMessage.containsKey(word)) {
//                inSecondMessageQuantity = wordsFromSecondMessage.get(word);
//            }
//
//            distance[0] += Math.min(inFirstMessageQuantity,inSecondMessageQuantity);
//            distance[1] += Math.abs(inFirstMessageQuantity - inSecondMessageQuantity);
//        });
//
//        return (distance[0] / wordsInFirstMessage + distance[0] / wordsInSecondMessage)
//                * (wordsInFirstMessage + wordsInSecondMessage) * Math.log(distance[1] + 1) + 1;
//    }

//    public double distance(String firstMessage, String secondMessage) {
//        Map<String, Integer> wordsFromFirstMessage;
//        if (wordsQuantitiesFromMessage.containsKey(firstMessage)) {
//            wordsFromFirstMessage = wordsQuantitiesFromMessage.get(firstMessage);
//        } else {
//            wordsFromFirstMessage = SpamFiltersService.getWordsQuantitiesFromMessage(firstMessage);
//        }
//        Map<String, Integer> wordsFromSecondMessage;
//        if (wordsQuantitiesFromMessage.containsKey(secondMessage)) {
//            wordsFromSecondMessage = wordsQuantitiesFromMessage.get(secondMessage);
//        } else {
//            wordsFromSecondMessage = SpamFiltersService.getWordsQuantitiesFromMessage(secondMessage);
//        }
//
//        Set<String> allWords = new HashSet<>();
//        allWords.addAll(wordsFromFirstMessage.keySet());
//        allWords.addAll(wordsFromSecondMessage.keySet());
//        int wordsInFirstMessage = SpamFiltersService.getWordsFromMessage(firstMessage).length;
//        int wordsInSecondMessage = SpamFiltersService.getWordsFromMessage(secondMessage).length;
//
//        double[] distance = {0.0};
//        allWords.forEach((word) -> {
//
//            int inFirstMessageQuantity = 0;
//            int inSecondMessageQuantity = 0;
//            if (wordsFromFirstMessage.containsKey(word)) {
//                inFirstMessageQuantity = wordsFromFirstMessage.get(word);
//            }
//            if (wordsFromSecondMessage.containsKey(word)) {
//                inSecondMessageQuantity = wordsFromSecondMessage.get(word);
//            }
//
//            distance[0] += Math.min(inFirstMessageQuantity,inSecondMessageQuantity);
//        });
//
//        return (1 - (distance[0] / wordsInFirstMessage + distance[0] / wordsInSecondMessage) / 2) * 1000 ;
//    }

    public double distance(String firstMessage, String secondMessage) {
        Map<String, Integer> wordsFromFirstMessage;
        if (wordsQuantitiesFromMessage.containsKey(firstMessage)) {
            wordsFromFirstMessage = wordsQuantitiesFromMessage.get(firstMessage);
        } else {
            wordsFromFirstMessage = SpamFiltersService.getWordsQuantitiesFromMessage(firstMessage);
        }
        Map<String, Integer> wordsFromSecondMessage;
        if (wordsQuantitiesFromMessage.containsKey(secondMessage)) {
            wordsFromSecondMessage = wordsQuantitiesFromMessage.get(secondMessage);
        } else {
            wordsFromSecondMessage = SpamFiltersService.getWordsQuantitiesFromMessage(secondMessage);
        }

        Set<String> allWords = new HashSet<>();
        allWords.addAll(wordsFromFirstMessage.keySet());
        allWords.addAll(wordsFromSecondMessage.keySet());

        double[] distance = {0.0};
        allWords.forEach((word) -> {

            int inFirstMessageQuantity = 0;
            int inSecondMessageQuantity = 0;
            if (wordsFromFirstMessage.containsKey(word)) {
                inFirstMessageQuantity = wordsFromFirstMessage.get(word);
            }
            if (wordsFromSecondMessage.containsKey(word)) {
                inSecondMessageQuantity = wordsFromSecondMessage.get(word);
            }

            distance[0] += Math.pow(Math.abs(inFirstMessageQuantity - inSecondMessageQuantity), 2);
        });

        return Math.sqrt(distance[0]);
    }

//
//    public double distance(String firstMessage, String secondMessage) {
//        Map<String, Integer> wordsFromFirstMessage;
//        if (wordsQuantitiesFromMessage.containsKey(firstMessage)) {
//            wordsFromFirstMessage = wordsQuantitiesFromMessage.get(firstMessage);
//        } else {
//            wordsFromFirstMessage = SpamFiltersService.getWordsQuantitiesFromMessage(firstMessage);
//        }
//        Map<String, Integer> wordsFromSecondMessage;
//        if (wordsQuantitiesFromMessage.containsKey(secondMessage)) {
//            wordsFromSecondMessage = wordsQuantitiesFromMessage.get(secondMessage);
//        } else {
//            wordsFromSecondMessage = SpamFiltersService.getWordsQuantitiesFromMessage(secondMessage);
//        }
//
//        Set<String> allWords = new HashSet<>();
//        allWords.addAll(wordsFromFirstMessage.keySet());
//        allWords.addAll(wordsFromSecondMessage.keySet());
//
//        double[] distance = {0.0};
//        allWords.forEach((word) -> {
//
//            int inFirstMessageQuantity = 0;
//            int inSecondMessageQuantity = 0;
//            if (wordsFromFirstMessage.containsKey(word)) {
//                inFirstMessageQuantity = wordsFromFirstMessage.get(word);
//            }
//            if (wordsFromSecondMessage.containsKey(word)) {
//                inSecondMessageQuantity = wordsFromSecondMessage.get(word);
//            }
//
//            distance[0] += Math.abs(inFirstMessageQuantity - inSecondMessageQuantity);
//        });
//
//        return distance[0];
//    }

    @Override
    public boolean isSpam(String messageToCheck) {
        LinkedList<Pair<String, Double>> messagesDistances = new LinkedList<>();
        messages.forEach((message, isSpam) -> messagesDistances
                .add(new Pair<>(message, distance(message, SpamFiltersService.toAddableMessage(messageToCheck)))));
        messagesDistances.sort((firstPair, secondPair) -> Double.compare(firstPair.getValue(), secondPair.getValue()));
//        int[] spamMessagesQuantity = {0};
//        messages.forEach((message, isSpam) -> {
//            if (isSpam) {
//                spamMessagesQuantity[0]++;
//            }
//        } );

        int i = 0;
        double forSpamVoices = 0;
        double forNotSpamVoices = 0;
        for (Pair<String, Double> messageDistance : messagesDistances) {
            if (i >= k_nearest_neighbors) {
                break;
            }

            if (messages.get(messageDistance.getKey())) {
                forSpamVoices += 1 / Math.pow(messageDistance.getValue(), 2);
            } else {
                forNotSpamVoices += 1 / Math.pow(messageDistance.getValue(), 2);
            }

            i++;
        }
//        forSpamVoices /= spamMessagesQuantity[0];
//        forNotSpamVoices /= (messages.size() - spamMessagesQuantity[0]);
        return forSpamVoices > forNotSpamVoices;
    }

    @Override
    public void clearInfo() {
        messages.clear();
        wordsQuantitiesFromMessage.clear();
    }

    @Override
    public void addMessage(String message, boolean isSpam) {
        String addableMessage = SpamFiltersService.toAddableMessage(message);
        messages.put(addableMessage, isSpam);
        wordsQuantitiesFromMessage.put(addableMessage, SpamFiltersService.getWordsQuantitiesFromMessage(addableMessage));
    }

    @Override
    public void addMessages(Map<String, Boolean> messagesToCheck) {
        messagesToCheck.entrySet().forEach(x -> addMessage(x.getKey(), x.getValue()));
    }

    @Override
    public void saveData(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(Integer.toString(k_nearest_neighbors));
            writer.newLine();
            for (Map.Entry<String, Boolean> message : messages.entrySet()) {
                writer.write(message.getValue().toString());
                writer.newLine();
                writer.write(message.getKey());
                writer.newLine();
            }
            writer.flush();
        }
    }

    @Override
    public void takeDataFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            k_nearest_neighbors = Integer.parseInt(reader.readLine());
            boolean isSpam;
            String message;
            while (reader.ready()) {
                isSpam = Boolean.parseBoolean(reader.readLine());
                message = reader.readLine();
                messages.put(message, isSpam);
            }
        }
        fillWordsQuantitiesDictionary();
    }

    private void fillWordsQuantitiesDictionary() {
        messages.forEach((message, isSpam) ->
                wordsQuantitiesFromMessage.put(message, SpamFiltersService.getWordsQuantitiesFromMessage(message)));
    }

    public int getK_nearest_neighbors() {
        return k_nearest_neighbors;
    }

    public Map<String, Boolean> getMessages() {
        return messages;
    }
}
