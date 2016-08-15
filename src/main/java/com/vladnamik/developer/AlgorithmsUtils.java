package com.vladnamik.developer;

import com.vladnamik.developer.datastructures.Pair;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Some common algorithms.
 */
@SuppressWarnings("unused")
public class AlgorithmsUtils {

    private static int[] primeNumbers = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107};

    /**
     * @return the {@code n}-th Fibonacci number
     */
    public static long fibNumber(int n) {
        if (n == 0) {
            return 0;
        } else if (n < 0) {
            throw new IllegalArgumentException("Sequence number can not be less than zero (your sequence number = " + n + ")");
        }
        long fib0 = 0;
        long fib1 = 1;
        long fib2 = 1;
        for (int i = 2; i <= n; i++) {
            fib2 = fib0 + fib1;
            fib0 = fib1;
            fib1 = fib2;
        }

        return fib2;
    }

    /**
     * @return finds the remainder after division of {@code n}-th Fibonacci number on {@code m}
     */
    public static long fibModulo(long n, int m) {
        int fib0 = 0;
        int fib1 = 1;
        int fib2 = 1;
        int period = 0;
        for (int i = 2; i <= n; i++) {
            fib2 = (fib0 + fib1) % m;
            fib0 = fib1;
            fib1 = fib2;
            if (fib0 == 0 && fib1 == 1) {
                period = i - 1;
                break;
            }
        }
        if (period != 0) {
            period = (int) (n % (long) period);
            for (int i = 2; i <= period; i++) {
                fib2 = (fib0 + fib1) % m;
                fib0 = fib1;
                fib1 = fib2;
            }
            if (period == 0)
                fib2 = fib0;
        }
        return fib2;
    }

    /**
     * Euclidean algorithm: greatest common divisor.
     *
     * @param a first number
     * @param b second number
     * @return greatest common divisor of {@code a} and {@code b}
     */
    public static long gcd(long a, long b) {
        long max = Math.max(a, b);
        long min = Math.min(a, b);
        long buf;

        while (min != 0) {
            buf = min;
            min = max % min;
            max = buf;
        }
        return max;
    }

    /**
     * @param a array comparable objects
     * @return median element of array {@code a}
     */
    public static Comparable medianOf(Comparable... a) {
        return orderStatistic((a.length - 1) / 2, a);
    }

    public static Comparable orderStatistic(int k, Comparable... a) {
        Comparable[] array = Arrays.copyOf(a, a.length);
        int left = 0;
        int right = array.length - 1;
        int stat = -1;
        int currentIndex;
        while (stat != k) {
            currentIndex = left + (int) (Math.random() * (right - left) / 2);
            swap(array, left, currentIndex);
            currentIndex = left + 1;
            for (int i = left + 1; i <= right; i++) {
                if (array[left].compareTo(array[i]) > 0) {
                    swap(array, currentIndex, i);
                    currentIndex++;
                }
            }
            swap(array, currentIndex - 1, left);
            if (currentIndex - 1 >= k)
                right = currentIndex - 2;
            else
                left = currentIndex;
            if (left > right && (currentIndex - 1) != k)
                return null;
            stat = currentIndex - 1;
        }
        return array[stat];
    }

    /**
     * Returns the number of {@code element} in ordered {@code array} using binary search algorithm.
     *
     * @param array   ordered array
     * @param element element to find
     * @param <T>     type of element
     * @return number of {@code element} in {@code array} or -1 if it doesn't contain {@code element}
     */
    public static <T> int binarySearch(Comparable<T>[] array, T element) {
        int left = 0;
        int right = array.length - 1;
        int index;
        int compare;
        while (left <= right) {
            index = left + (right - left) / 2;
            if ((compare = array[index].compareTo(element)) == 0)
                return index + 1;
            else if (compare > 0)
                right = index - 1;
            else
                left = index + 1;
        }
        return -1;
    }

    public static void swap(Object[] array, int a, int b) {
        Object k = array[a];
        array[a] = array[b];
        array[b] = k;
    }

    public static int min(int... array) {
        int min = array[0];
        for (int k : array)
            if (k < min)
                min = k;
        return min;
    }

    public static int max(int... array) {
        int max = array[0];
        for (int k : array)
            if (k > max)
                max = k;
        return max;
    }

    /**
     * Returns list of all prime numbers that are dividers of {@code k}.
     * For example, for number '12' it will be '2, 2, 3'.
     */
    public static List<Integer> primeFactorization(int k) {
        List<Integer> list = new LinkedList<>();
        if (k <= 1)
            return list;
        int currentDividerNumberInArray = 0;
        int currentDivider = primeNumbers[currentDividerNumberInArray];
        while (k != 1) {
            if (currentDivider >= Math.sqrt(k) + 1) {
                list.add(k);
                break;
            }
            while (k % currentDivider == 0) {
                list.add(currentDivider);
                k = k / currentDivider;
            }
            currentDividerNumberInArray++;
            if (currentDividerNumberInArray >= primeNumbers.length)
                currentDivider++;
            else
                currentDivider = primeNumbers[currentDividerNumberInArray];
        }
        return list;
    }

    public static Pair<Integer, Integer> getPairOfIndexOfMaxSum(int[] firstArray, int[] secondArray) {
        int[] firstArrayMaxElementsIndex = new int[firstArray.length];
        int bufIndexOfMax = 0;
        for (int i = 0; i < firstArray.length; i++) {
            if (firstArray[i] > firstArray[bufIndexOfMax])
                bufIndexOfMax = i;
            firstArrayMaxElementsIndex[i] = bufIndexOfMax;
        }
        Pair<Integer, Integer> pair = new Pair<>(firstArray.length - 1, secondArray.length - 1);
        int maxSum = firstArray[pair.getFirst()] + secondArray[pair.getSecond()];
        bufIndexOfMax = secondArray.length - 1;
        for (int i = secondArray.length - 1; i >= 0; i--) {
            if (secondArray[bufIndexOfMax] <= secondArray[i])
                bufIndexOfMax = i;
            if (firstArray[firstArrayMaxElementsIndex[i]] + secondArray[bufIndexOfMax] >= maxSum) {
                pair.setFirst(firstArrayMaxElementsIndex[i]);
                pair.setSecond(bufIndexOfMax);
                maxSum = firstArray[firstArrayMaxElementsIndex[i]] + secondArray[bufIndexOfMax];
            }
        }
        return pair;
    }

    public static int[] getTheNearestElementsOfSecondArrayInFirstArray(int[] firstArray, int[] secondArray) {
        int[] outputArray = new int[secondArray.length];
        int bufIndex;
        for (int i = 0; i < secondArray.length; i++) {
            bufIndex = binarySearchForTheNearest(firstArray, secondArray[i]);
            if (bufIndex == -1) {
                outputArray[i] = 0;
            } else if (bufIndex == firstArray.length - 1) {
                outputArray[i] = bufIndex;
            } else if ((secondArray[i] - firstArray[bufIndex]) <= (firstArray[bufIndex + 1] - secondArray[i])) {
                outputArray[i] = bufIndex;
            } else
                outputArray[i] = bufIndex + 1;
        }
        return outputArray;
    }

    public static int binarySearchForTheNearest(int[] array, int element) {
        int begin = 0;
        int end = array.length;
        int index = -1;
        while (begin < end) {
            index = begin + (end - begin) / 2;
            if (array[index] >= element) {
                end = index;
                index = end - 1;
            } else {
                begin = index + 1;
                index = begin - 1;
            }
        }
        return index;
    }
}
