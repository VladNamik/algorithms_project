package com.vladnamik.developer.sorting;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static com.vladnamik.developer.AlgorithmsUtils.medianOf;
import static com.vladnamik.developer.AlgorithmsUtils.swap;

@SuppressWarnings("unused")
public class QuickSort implements Sorting {
    @Override
    public void sort(Comparable[] inputArray) {
        Queue<Comparable[]> queue = new LinkedList<>();
        Queue<Integer> indexQueue = new LinkedList<>();
        indexQueue.add(0);
        queue.add(inputArray);
        Comparable[] array;
        int left;
        int right;
        Comparable median;
        int currentIndex;
        int trueIndex;
        Comparable[] outputArray = new Comparable[inputArray.length];
        while (queue.size() > 0) {
            array = queue.remove();
            trueIndex = indexQueue.remove();
            left = 0;
            right = array.length - 1;
            currentIndex = left;
            if ((median = medianOf(array[left], array[right], array[(right - left) / 2])) == array[right])
                swap(array, left, right);
            else if (median == array[(right - left) / 2])
                swap(array, left, (right - left) / 2);
            for (int i = left + 1; i <= right; i++) {
                if (array[i].compareTo(array[left]) <= 0) {
                    currentIndex++;
                    swap(array, currentIndex, i);
                }
            }
            swap(array, currentIndex, left);
            int leftSimilarIndex = currentIndex;
            while (leftSimilarIndex >= 0 && array[leftSimilarIndex] == array[currentIndex])
                leftSimilarIndex--;
            leftSimilarIndex++;
            if (leftSimilarIndex > 0) {
                queue.add(Arrays.copyOfRange(array, 0, leftSimilarIndex));
                indexQueue.add(trueIndex);
            }
            if (currentIndex + 1 < array.length) {
                queue.add(Arrays.copyOfRange(array, currentIndex + 1, array.length));
                indexQueue.add(trueIndex + currentIndex + 1);
            }
            for (int i = leftSimilarIndex; i <= currentIndex; i++)
                outputArray[i + trueIndex] = array[i];
        }
        for (int i = 0; i < outputArray.length; i++)
            inputArray[i] = outputArray[i];
    }
}
