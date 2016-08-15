package com.vladnamik.developer.datastructures;

import java.util.Comparator;

@SuppressWarnings("unused")
public class MinHeap<T> implements SimpleQueue<T> {
    private final Comparator<T> comparator;
    private T data;
    private int size = 0;
    private MinHeap<T> left;
    private MinHeap<T> right;
    private MinHeap<T> parent;

    public MinHeap(final Comparator<T> comparator) {
        this.comparator = new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (o1 == null && o2 == null)
                    return 0;
                else if (o1 == null)
                    return 1;
                else if (o2 == null)
                    return -1;
                else
                    return comparator.compare(o1, o2);
            }
        };
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void insert(T element) {
        size++;
        if (size == 1) {
            data = element;
            return;
        }
        MinHeap<T> newElement = new MinHeap<>(comparator);
        newElement.data = element;
        newElement.left = null;
        newElement.right = null;

        MinHeap<T> bufferElement = this;
        int height = (int) (Math.log(size) / Math.log(2));
        int placeNumber = size % (int) Math.pow(2, height);
        for (int i = 1; i < (int) (Math.log(size) / Math.log(2)); i++) {
            if (placeNumber >= (int) Math.pow(2, height - 1))
                bufferElement = bufferElement.right;
            else
                bufferElement = bufferElement.left;
            height--;
            placeNumber = placeNumber % (int) Math.pow(2, height);
        }
        if (placeNumber == 1) {
            bufferElement.right = newElement;
            newElement.parent = bufferElement;
        } else {
            bufferElement.left = newElement;
            newElement.parent = bufferElement;
        }
        sift(newElement);
    }

    private MinHeap<T> getLastElement() {
        MinHeap<T> bufferElement = this;
        int height = (int) (Math.log(size) / Math.log(2));
        int placeNumber = size % (int) Math.pow(2, height);
        for (int i = 0; i < (int) (Math.log(size) / Math.log(2)); i++) {
            if (placeNumber >= (int) Math.pow(2, height - 1))
                bufferElement = bufferElement.right;
            else
                bufferElement = bufferElement.left;
            height--;
            placeNumber = placeNumber % (int) Math.pow(2, height);
        }
        return bufferElement;
    }

    @Override
    public T extractMin() {
        T res = data;
        data = null;
        if (left == null) {
            size = 0;
            return res;
        }
        MinHeap<T> removedElement = sift(this);
        swapData(removedElement, getLastElement());
        sift(removedElement);
        MinHeap<T> removeElementParent = getLastElement().parent;
        if (removeElementParent.left.data == null)
            removeElementParent.left = null;
        else
            removeElementParent.right = null;
        size--;
        return res;
    }

    @Override
    public T extractMax() {
        return extractMin();
    }

    private void swapData(MinHeap<T> first, MinHeap<T> second) {
        T bufData = first.data;
        first.data = second.data;
        second.data = bufData;
    }

    private MinHeap<T> sift(MinHeap<T> element) {
        return siftDown(siftUp(element));
    }

    private MinHeap<T> siftUp(MinHeap<T> element) {
        MinHeap<T> bufElement = element;
        while (bufElement.parent != null && comparator.compare(bufElement.parent.data, bufElement.data) > 0) {
            swapData(bufElement, bufElement.parent);
            bufElement = bufElement.parent;
        }
        return bufElement;
    }

    private MinHeap<T> siftDown(MinHeap<T> element) {
        MinHeap<T> bufElement = element;
        while (bufElement.left != null && bufElement.right != null && (comparator.compare(bufElement.left.data, bufElement.data) < 0 || comparator.compare(bufElement.right.data, bufElement.data) < 0)) {
            if (comparator.compare(bufElement.left.data, bufElement.right.data) >= 0) {
                swapData(bufElement, bufElement.right);
                bufElement = bufElement.right;
            } else {
                swapData(bufElement, bufElement.left);
                bufElement = bufElement.left;
            }
        }
        if (bufElement.left != null && bufElement.right == null && comparator.compare(bufElement.left.data, bufElement.data) < 0) {
            swapData(bufElement, bufElement.left);
            bufElement = bufElement.left;
        }
        return bufElement;
    }
}