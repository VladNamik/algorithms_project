package com.vladnamik.developer.datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Heap<T> {
    private Comparator<T> comparator;
    private ArrayList<T> heapArray;

    public Heap(Comparator<T> comparator) {
        this.comparator = comparator;
        heapArray = new ArrayList<>();
    }

    public Heap(ArrayList<T> data, Comparator<T> comparator) {
        makeHeap(data);
        this.comparator = comparator;
    }

    public void makeHeap(ArrayList<T> data) {
        heapArray = data;
        int size = data.size();
        for (int i = (size / 2 - 1); i >= 0; i--) {
            siftDown(i);
        }
    }

    public ArrayList<T> getHeapArray() {
        return heapArray;
    }

    public T getHead() {
        return heapArray.get(0);
    }

    public T extractHead() {
        T el = getHead();
        Collections.swap(heapArray, 0, size() - 1);
        heapArray.remove(size() - 1);
        siftDown(0);
        return el;
    }

    public int size() {
        return heapArray.size();
    }

    public void insert(T element) {
        heapArray.add(element);
        siftUp(size() - 1);
    }

    public void sift(int index) {
        if (index != 0 && comparator.compare(heapArray.get(getParentIndex(index)), heapArray.get(index)) > 0) {
            siftDown(index);
        } else {
            siftUp(index);
        }
    }

    public T remove(int index) {
        while (index != 0) {
            Collections.swap(heapArray, getParentIndex(index), index);
            index = getParentIndex(index);
        }
        return extractHead();
    }

    private void siftUp(int index) {
        while (index != 0 && comparator.compare(heapArray.get(getParentIndex(index)), heapArray.get(index)) < 0) {
            Collections.swap(heapArray, getParentIndex(index), index);
            index = getParentIndex(index);
        }
    }

    private void siftDown(int index) {

        while (index <= (size()) / 2 - 1) {
            int leftIndex = getLeftChildIndex(index);
            int maxChildElIndex = leftIndex;
            if (leftIndex < (size() - 1)) {
                maxChildElIndex = comparator.compare(heapArray.get(maxChildElIndex), heapArray.get(maxChildElIndex + 1)) > 0 ? maxChildElIndex : maxChildElIndex + 1;
            }

            if (comparator.compare(heapArray.get(index), heapArray.get(maxChildElIndex)) > 0) {
                break;
            }
            Collections.swap(heapArray, index, maxChildElIndex);

            index = maxChildElIndex;
        }
    }

    private int getParentIndex(int childIndex) {
        return (childIndex - 1) / 2;
    }

    private int getLeftChildIndex(int parentIndex) {
        return parentIndex * 2 + 1;
    }
}
