package com.vladnamik.developer.datastructures;

import java.util.Comparator;

/**
 * Max heap implementation based on min heap
 *
 * @param <T>
 */
@SuppressWarnings("unused")
public class MaxHeap<T> implements SimpleQueue<T> {
    MinHeap<T> heap;

    MaxHeap(Comparator<T> comparator) {
        heap = new MinHeap<>((x, y) -> -comparator.compare(x, y));
    }

    @Override
    public void insert(T element) {
        heap.insert(element);
    }

    @Override
    public int size() {
        return heap.size();
    }

    @Override
    public T extractMin() {
        return heap.extractMax();
    }

    @Override
    public T extractMax() {
        return heap.extractMin();
    }

}