package com.vladnaminas.developer;



import java.util.*;

public class DynamicProgramming {
    public static class Pair<T,V>
    {
        T first;
        V second;
        public Pair(T first, V second)
        {
            this.first=first;
            this.second=second;
        }
        public T getKey()
        {
            return first;
        }

        public V getValue()
        {
            return second;
        }

        @Override
        public int hashCode() {
            return first.hashCode()+second.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Pair))
                return false;
            else
                return first.equals(((Pair) obj).first) && second.equals(((Pair)obj).second);
        }
    }
    public static int longestIncreasingSubsequence(int[] array)//O(n^2) ���������� ������������ ���������������������
    {
        int subSeqLengthArray[] = new int[array.length];
        for (int i = 0; i < array.length; i++)
        {
            subSeqLengthArray[i]=1;
            for (int j = 0; j < i; j++)
            {
                if (array[i]%array[j]==0 && (subSeqLengthArray[j]+1)>subSeqLengthArray[i])
                    subSeqLengthArray[i]=subSeqLengthArray[j]+1;
            }
        }
        int max=0;
        for (int i: subSeqLengthArray)
            if (i>max)
                max=i;
        return max;
    }

    public static int[] longestNotIncreasingSubsequence(int[] array)//O(n*log(n)) (���������� ������ ��������) ���������� �������������� ���������������������
    {
        ArrayList<Pair<Integer,Integer>> outList = new ArrayList<>();//������ ��� �������-������ � ����������� �������
        int[] prevArray= new int[array.length];//������ �������� ���������� ��������� ������������������
        int index;//�������� ������
        if (array.length==0)
            return null;
        for (int i=0; i < array.length ; i++)
        {
            index=binarySearch(outList, array[i]);
            if (index==0)
                prevArray[i]=-1;
            else
                prevArray[i]=outList.get(index-1).getValue();
            if (outList.size()<=index)
                outList.add(new Pair<>(array[i], i));
            else
                outList.set(index, new Pair<>(array[i],i));
        }
        int[] indexArray = new int[outList.size()];
        indexArray[indexArray.length-1]=outList.get(outList.size()-1).getValue();
        for (int i=indexArray.length-2; i>=0; i--)
            indexArray[i]=prevArray[indexArray[i+1]];
        return indexArray;
    }

    private static int binarySearch(ArrayList<Pair<Integer, Integer>> list, int element)//���������� ������ ��������, ���������� �������� �������, � ������������� �� ������������� ������
    {
        int begin=0;
        int end=list.size();
        int index=0;//������� ������
        while (begin<end)
        {
            index=begin+(end-begin)/2;
            if (list.get(index).getKey()>=element) {
                begin = index + 1;
                index=begin;
            }
            else {
                end = index;
            }
        }
        return index;
    }

    public static int editDistance(char[] first, char[] second)//���������� ���������� ���������� �������������� (���������) ��� ���� �������� (���������������, ��� ��������� �������� �������, �������� � ������ ���������)
    {
        int[] prev = new int[first.length+1];//���������� ������
        int[] next = new int[first.length+1];//������, ������� �� ���������
        int[] buf; //�������� ������
        for (int i=0; i<prev.length; i++)
            prev[i]=i;
        for (int i=1; i<second.length+1; i++)//����� ������
        {
            for (int j=0; j<first.length+1; j++)//����� �������
            {
                if (j==0)
                {
                    next[j]=i;
                    continue;
                }
                next[j]= Algorithms.min(prev[j]+1, next[j-1]+1, first[j-1]==second[i-1]? prev[j-1] : prev[j-1]+1);
            }
            buf=prev;
            prev=next;
            next=buf;
        }
        return prev[first.length];
    }

    private static Map<Pair<Integer, Integer>, Integer> knapsackMap;//������������� ������ ��� ������ knapsackTD
    public static int knapsackTD(int w, Pair<Integer, Integer>[] pairs)//������� ������������ ��� ���������, ������� �� ����� ������ � ������� (w � ����������� ������� (���), pairs � ������ ��� ���-���������)
    {
        knapsackMap=new HashMap<>();
        if (pairs.length==0)
            return 0;
        if (w-pairs[0].getKey()<0)
            return knapsackTDRecursionPart(w,pairs,1);
        else
            return Algorithms.max(knapsackTDRecursionPart(w,pairs,1), knapsackTDRecursionPart(w-pairs[0].getKey(), pairs, 1)+pairs[0].getValue());
    }

    private static int knapsackTDRecursionPart(int w, Pair<Integer, Integer>[] pairs, int pairIndex)//pairIndex � ������ ����, ������� �� ������������� � ������ ������
    {
        if (pairs.length==pairIndex)
            return 0;
        if (w==0)
            return 0;
        if (knapsackMap.containsKey(new Pair<>(w, pairIndex)))
            return knapsackMap.get(new Pair<>(w,pairIndex));
        if (w-pairs[pairIndex].getKey()<0)
            return knapsackTDRecursionPart(w,pairs,pairIndex + 1);
        else {
            int p = Algorithms.max(knapsackTDRecursionPart(w, pairs, pairIndex + 1), knapsackTDRecursionPart(w - pairs[pairIndex].getKey(), pairs, pairIndex + 1) + pairs[pairIndex].getValue());
            knapsackMap.put(new Pair<>(w, pairIndex), p);
            return p;
        }
    }

    public static int maxSumFromStairs(int stairWeights[])//���� ����� 1?n?102 �������� �������� � ����� ����� ?104?a1,�,an?104, �������� �������� ���������. ������� ������������ �����, ������� ����� ��������, ��� �� �������� ����� ����� (�� ������� �� n-� ���������), ������ ��� ���������� �� ���� ��� ��� ���������.
    {
        int[] stairMaxSums = new int[stairWeights.length];
        if (stairWeights.length==0)
            return 0;
        if (stairWeights.length==1)
            return stairWeights[0];
        if (stairWeights.length==2)
            return Algorithms.max(stairWeights[1], stairWeights[1]+stairWeights[0]);
        stairMaxSums[0]=stairWeights[0];
        stairMaxSums[1]= Algorithms.max(stairWeights[1], stairWeights[1]+stairWeights[0]);
        for (int i=2; i<stairMaxSums.length; i++)
        {
            stairMaxSums[i]= Algorithms.max(stairMaxSums[i-1]+stairWeights[i], stairMaxSums[i-2]+stairWeights[i]);
        }
        return stairMaxSums[stairMaxSums.length-1];
    }

    private static Map<Integer, Integer> minLengthOfSumMap;//������������� ������ ��� ������ minLengthOfSum
    public static LinkedList<Integer> minLengthOfSum(int n)//� ��� ���� ����������� �����������, ������� ����� ��������� ����� ��� �������� � ������� ������ x: �������� x �� 2x, 3x ��� x+1. �� ������� ������ ����� 1?n?105 ���������� ����������� ����� �������� k, �����������, ����� �������� n �� 1.
    {
        minLengthOfSumMap=new HashMap<>();//���� � �����, �������� � ���������
        minLengthOfSumMap.put(1, 0);
        LinkedList<Integer> list=new LinkedList<>();
        list.add(n);
        minLengthOfSumRecursion(n);
        while(n!=1)
        {
            if (n%3==0)
                n=n/3;
            else if (n%2==0)
            {
                if (minLengthOfSumMap.get(n/2) < minLengthOfSumMap.get(n-1))
                    n=n/2;
                else
                    n=n-1;
            }
            else
                n=n-1;

            list.add(n);
        }
        return list;
    }
    private static int minLengthOfSumRecursion(int n)
    {
        int buf;
        if (minLengthOfSumMap.containsKey(n)) {
            return minLengthOfSumMap.get(n);
        }
        if (n%3==0) {
            buf = minLengthOfSumRecursion(n/3) + 1;
            minLengthOfSumMap.put(n, buf);
            return buf;
        }
        else if(n%2==0)
        {
            buf = Algorithms.min(minLengthOfSumRecursion(n-1), minLengthOfSumRecursion(n/2))+1;
            minLengthOfSumMap.put(n, buf);
            return buf;
        }
        else
        {
            buf = minLengthOfSumRecursion(n-1) + 1;
            minLengthOfSumMap.put(n, buf);
            return buf;
        }
    }
}
