package com.vladnamik.developer.datastructures;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Стэк с поддержкой максимума
 */
public class MaxValueStack {
    private Deque<Integer> mainStack = new ArrayDeque<>();
    private Deque<Integer> maxValStack = new ArrayDeque<>();

    public void push(Integer val) {
        mainStack.add(val);
        if (!maxValStack.isEmpty()) {
            maxValStack.add(Math.max(maxValStack.getLast(), val));
        } else {
            maxValStack.add(val);
        }
    }

    public Integer pop() {
        maxValStack.removeLast();
        return mainStack.removeLast();
    }

    public Integer getMax() {
        return maxValStack.getLast();
    }

    public int size() {
        return mainStack.size();
    }
}
