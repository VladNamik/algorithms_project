package com.vladnamik.developer.data.mining.spamfilters;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings("unused")
public interface SpamFilter {
    boolean isSpam(String message);

    void clearInfo();

    void addMessage(String message, boolean isSpam);

    void addMessages(Map<String, Boolean> messages);

    void saveData(String filePath) throws IOException;

    void takeDataFromFile(String filePath) throws IOException;
}
