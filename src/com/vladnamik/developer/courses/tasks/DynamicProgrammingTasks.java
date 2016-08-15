package com.vladnamik.developer.courses.tasks;


import com.vladnamik.developer.datastructures.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.vladnamik.developer.AlgorithmsUtil.max;
import static com.vladnamik.developer.AlgorithmsUtil.min;

@SuppressWarnings("unused")
public class DynamicProgrammingTasks {
    private static Map<Pair<Integer, Integer>, Integer> knapsackMap;
    private static Map<Integer, Integer> minLengthOfSumMap;

    /**
     * Finds the longest not increasing subsequence in {@code array}.
     *
     * @return array of elements indexes in input array
     */
    public static int[] longestNotIncreasingSubsequence(int[] array) {
        ArrayList<Pair<Integer, Integer>> outList = new ArrayList<>();
        int[] prevArray = new int[array.length];
        int index;
        if (array.length == 0)
            return null;
        for (int i = 0; i < array.length; i++) {
            index = binarySearch(outList, array[i]);
            if (index == 0)
                prevArray[i] = -1;
            else
                prevArray[i] = outList.get(index - 1).getSecond();
            if (outList.size() <= index)
                outList.add(new Pair<>(array[i], i));
            else
                outList.set(index, new Pair<>(array[i], i));
        }
        int[] indexArray = new int[outList.size()];
        indexArray[indexArray.length - 1] = outList.get(outList.size() - 1).getSecond();
        for (int i = indexArray.length - 2; i >= 0; i--)
            indexArray[i] = prevArray[indexArray[i + 1]];
        return indexArray;
    }

    /**
     * Special realization of binary search algorithm for longest not increasing subsequence.
     */
    private static int binarySearch(ArrayList<Pair<Integer, Integer>> list, int element) {
        int begin = 0;
        int end = list.size();
        int index = 0;
        while (begin < end) {
            index = begin + (end - begin) / 2;
            if (list.get(index).getFirst() >= element) {
                begin = index + 1;
                index = begin;
            } else {
                end = index;
            }
        }
        return index;
    }

    /**
     * Edit distance problem solving.
     *
     * @param first  first array
     * @param second second array
     * @return edit distance of two strings
     * @see <a href="https://en.wikipedia.org/wiki/Edit_distance">https://en.wikipedia.org/wiki/Edit_distance</a>
     */
    public static int editDistance(char[] first, char[] second) {
        int[] prev = new int[first.length + 1];
        int[] next = new int[first.length + 1];
        int[] buf;
        for (int i = 0; i < prev.length; i++)
            prev[i] = i;
        for (int i = 1; i < second.length + 1; i++) {
            for (int j = 0; j < first.length + 1; j++) {
                if (j == 0) {
                    next[j] = i;
                    continue;
                }
                next[j] = min(prev[j] + 1, next[j - 1] + 1, first[j - 1] == second[i - 1] ? prev[j - 1] : prev[j - 1] + 1);
            }
            buf = prev;
            prev = next;
            next = buf;
        }
        return prev[first.length];
    }

    public static int knapsackTD(int w, Pair<Integer, Integer>[] pairs) {
        knapsackMap = new HashMap<>();
        if (pairs.length == 0)
            return 0;
        if (w - pairs[0].getFirst() < 0)
            return knapsackTDRecursionPart(w, pairs, 1);
        else
            return max(knapsackTDRecursionPart(w, pairs, 1), knapsackTDRecursionPart(w - pairs[0].getFirst(), pairs, 1) + pairs[0].getSecond());
    }

    private static int knapsackTDRecursionPart(int w, Pair<Integer, Integer>[] pairs, int pairIndex) {
        if (pairs.length == pairIndex)
            return 0;
        if (w == 0)
            return 0;
        if (knapsackMap.containsKey(new Pair<>(w, pairIndex)))
            return knapsackMap.get(new Pair<>(w, pairIndex));
        if (w - pairs[pairIndex].getFirst() < 0)
            return knapsackTDRecursionPart(w, pairs, pairIndex + 1);
        else {
            int p = max(knapsackTDRecursionPart(w, pairs, pairIndex + 1), knapsackTDRecursionPart(w - pairs[pairIndex].getFirst(), pairs, pairIndex + 1) + pairs[pairIndex].getSecond());
            knapsackMap.put(new Pair<>(w, pairIndex), p);
            return p;
        }
    }

    public static int maxSumFromStairs(int stairWeights[]) {
        int[] stairMaxSums = new int[stairWeights.length];
        if (stairWeights.length == 0)
            return 0;
        if (stairWeights.length == 1)
            return stairWeights[0];
        if (stairWeights.length == 2)
            return max(stairWeights[1], stairWeights[1] + stairWeights[0]);
        stairMaxSums[0] = stairWeights[0];
        stairMaxSums[1] = max(stairWeights[1], stairWeights[1] + stairWeights[0]);
        for (int i = 2; i < stairMaxSums.length; i++) {
            stairMaxSums[i] = max(stairMaxSums[i - 1] + stairWeights[i], stairMaxSums[i - 2] + stairWeights[i]);
        }
        return stairMaxSums[stairMaxSums.length - 1];
    }

    public static LinkedList<Integer> minLengthOfSum(int n) {
        minLengthOfSumMap = new HashMap<>();
        minLengthOfSumMap.put(1, 0);
        LinkedList<Integer> list = new LinkedList<>();
        list.add(n);
        minLengthOfSumRecursion(n);
        while (n != 1) {
            if (n % 3 == 0)
                n = n / 3;
            else if (n % 2 == 0) {
                if (minLengthOfSumMap.get(n / 2) < minLengthOfSumMap.get(n - 1))
                    n = n / 2;
                else
                    n = n - 1;
            } else
                n = n - 1;

            list.add(n);
        }
        return list;
    }

    private static int minLengthOfSumRecursion(int n) {
        int buf;
        if (minLengthOfSumMap.containsKey(n)) {
            return minLengthOfSumMap.get(n);
        }
        if (n % 3 == 0) {
            buf = minLengthOfSumRecursion(n / 3) + 1;
            minLengthOfSumMap.put(n, buf);
            return buf;
        } else if (n % 2 == 0) {
            buf = min(minLengthOfSumRecursion(n - 1), minLengthOfSumRecursion(n / 2)) + 1;
            minLengthOfSumMap.put(n, buf);
            return buf;
        } else {
            buf = minLengthOfSumRecursion(n - 1) + 1;
            minLengthOfSumMap.put(n, buf);
            return buf;
        }
    }
}
