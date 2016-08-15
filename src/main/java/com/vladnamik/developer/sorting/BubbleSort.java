package com.vladnamik.developer.sorting;

@SuppressWarnings("unused")
public class BubbleSort implements Sorting {
    @Override
    public void sort(Comparable[] array) {
        for (int i = 0; i < array.length - 1; i++)
            for (int j = i + 1; j < array.length; j++)
                if (array[i].compareTo(array[j]) > 0) {
                    Comparable a = array[i];
                    array[i] = array[j];
                    array[j] = a;
                }
    }
}
