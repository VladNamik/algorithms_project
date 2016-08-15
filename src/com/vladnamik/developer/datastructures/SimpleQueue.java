package com.vladnamik.developer.datastructures;

public interface SimpleQueue<T> {
    void insert(T element);

    T extractMin();

    T extractMax();

    int size();
}