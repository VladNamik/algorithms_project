package com.vladnamik.developer.sorting;


import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("unused")
public class MergeSort implements Sorting {
    private int inversionCounter = 0;

    /**
     * Inversion counter problem solving.
     * <p>
     * Inversion Count for an array indicates – how far (or close) the array is from being sorted.
     * If array is already sorted then inversion count is 0.
     * If array is sorted in reverse order that inversion count is the maximum.
     * Formally speaking, two elements a[i] and a[j] form an inversion if a[i] > a[j] and i < j.
     * <p>
     * Example:
     * The sequence 2, 4, 1, 3, 5 has three inversions (2, 1), (4, 1), (4, 3).
     *
     * @param array input array.
     * @return number of inversions.
     */
    public int getInversionCounter(Comparable[] array) {
        Comparable[] newArray = new Comparable[(int) Math.pow(2, (int) (Math.log(array.length) / Math.log(2)) + 1)];
        for (int i = 0; i < array.length; i++)
            newArray[i] = array[i];
        inversionCounter = 0;
        for (int i = array.length; i < newArray.length; i++)
            newArray[i] = Integer.MAX_VALUE;
        sort(newArray);

        return inversionCounter;
    }

    public void sort(Comparable[] array) {
        Queue<Comparable[]> queue = new LinkedList<>();
        for (Comparable element : array)
            queue.add(new Comparable[]{element});
        Comparable[] first, second;
        while (queue.size() > 1) {
            first = queue.remove();
            second = queue.remove();
            queue.add(merge(first, second));
        }
        Comparable[] outArray = queue.remove();
        for (int i = 0; i < array.length; i++)
            array[i] = outArray[i];
    }

    private Comparable[] merge(Comparable[] firstArray, Comparable[] secondArray) {
        Comparable[] outArray = new Comparable[firstArray.length + secondArray.length];
        int i = 0, j = 0;
        for (; (i < firstArray.length) && (j < secondArray.length); )
            if (firstArray[i].compareTo(secondArray[j]) > 0) {
                outArray[i + j] = secondArray[j];
                j++;
                inversionCounter += firstArray.length - i;
            } else {
                outArray[i + j] = firstArray[i];
                i++;
            }
        for (; i < firstArray.length; i++)
            outArray[i + j] = firstArray[i];
        for (; j < secondArray.length; j++)
            outArray[i + j] = secondArray[j];
        return outArray;
    }
}
