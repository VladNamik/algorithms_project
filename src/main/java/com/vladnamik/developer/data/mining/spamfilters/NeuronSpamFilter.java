package com.vladnamik.developer.data.mining.spamfilters;

import java.io.IOException;
import java.util.Map;

public class NeuronSpamFilter implements SpamFilter {

    @Override
    public boolean isSpam(String message) {
        return false;
    }

    @Override
    public void clearInfo() {

    }

    @Override
    public void addMessage(String message, boolean isSpam) {

    }

    @Override
    public void addMessages(Map<String, Boolean> messages) {

    }

    @Override
    public void saveData(String filePath) throws IOException {

    }

    @Override
    public void takeDataFromFile(String filePath) throws IOException {

    }
}
