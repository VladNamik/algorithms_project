package com.vladnamik.developer.datastructures;

import java.util.Comparator;

public class PriorityQueue<T> {
    private Heap<T> heap;

    public PriorityQueue(Comparator<T> comparator) {
        heap = new Heap<>(comparator);
    }

    public void push(T element) {
        heap.insert(element);
    }

    public T pop() {
        return heap.extractHead();
    }
}
