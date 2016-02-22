package com.company.example;



import java.util.*;

import static com.company.example.Algorithms.*;

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
    public static int longestIncreasingSubsequence(int[] array)//O(n^2) наибольша€ возрастающа€ подпоследовательность
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

    public static int[] longestNotIncreasingSubsequence(int[] array)//O(n*log(n)) (возвращает массив индексов) наибольша€ невозрастающа€ подпоследовательность
    {
        ArrayList<Pair<Integer,Integer>> outList = new ArrayList<>();//массив пар элемент-индекс в изначальном массиве
        int[] prevArray= new int[array.length];//массив индексов предыдущих элементов последовательности
        int index;//буферный индекс
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

    private static int binarySearch(ArrayList<Pair<Integer, Integer>> list, int element)//возвращает индекс элемента, ближайшего меньшего данному, в упор€доченном по невозрастанию списке
    {
        int begin=0;
        int end=list.size();
        int index=0;//искомый индекс
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

    public static int editDistance(char[] first, char[] second)//возвращает наименьшее рассто€ние редактировани€ (стоимость) дл€ двух массивов (подразумеваетс€, что стоимость операций вставки, удалени€ и замены одинакова)
    {
        int[] prev = new int[first.length+1];//предыдуща€ строка
        int[] next = new int[first.length+1];//строка, которую мы заполн€ем
        int[] buf; //буферный массив
        for (int i=0; i<prev.length; i++)
            prev[i]=i;
        for (int i=1; i<second.length+1; i++)//номер строки
        {
            for (int j=0; j<first.length+1; j++)//номер столбца
            {
                if (j==0)
                {
                    next[j]=i;
                    continue;
                }
                next[j]=min(prev[j]+1, next[j-1]+1, first[j-1]==second[i-1]? prev[j-1] : prev[j-1]+1);
            }
            buf=prev;
            prev=next;
            next=buf;
        }
        return prev[first.length];
    }

    private static Map<Pair<Integer, Integer>, Integer> knapsackMap;//ассоциативный массив дл€ задачи knapsackTD
    public static int knapsackTD(int w, Pair<Integer, Integer>[] pairs)//находим максимальный вес элементов, которые мы можем унести в рюкзаке (w Ч вместимость рюкзака (вес), pairs Ч массив пар вес-стоимость)
    {
        knapsackMap=new HashMap<>();
        if (pairs.length==0)
            return 0;
        if (w-pairs[0].getKey()<0)
            return knapsackTDRecursionPart(w,pairs,1);
        else
            return max(knapsackTDRecursionPart(w,pairs,1), knapsackTDRecursionPart(w-pairs[0].getKey(), pairs, 1)+pairs[0].getValue());
    }

    private static int knapsackTDRecursionPart(int w, Pair<Integer, Integer>[] pairs, int pairIndex)//pairIndex Ч индекс пары, которую мы рассматриваем в данный момент
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
            int p = max(knapsackTDRecursionPart(w, pairs, pairIndex + 1), knapsackTDRecursionPart(w - pairs[pairIndex].getKey(), pairs, pairIndex + 1) + pairs[pairIndex].getValue());
            knapsackMap.put(new Pair<>(w, pairIndex), p);
            return p;
        }
    }

    public static int maxSumFromStairs(int stairWeights[])//ƒаны число 1?n?102 ступенек лестницы и целые числа ?104?a1,Е,an?104, которыми помечены ступеньки. Ќайдите максимальную сумму, которую можно получить, ид€ по лестнице снизу вверх (от нулевой до n-й ступеньки), каждый раз поднима€сь на одну или две ступеньки.
    {
        int[] stairMaxSums = new int[stairWeights.length];
        if (stairWeights.length==0)
            return 0;
        if (stairWeights.length==1)
            return stairWeights[0];
        if (stairWeights.length==2)
            return max(stairWeights[1], stairWeights[1]+stairWeights[0]);
        stairMaxSums[0]=stairWeights[0];
        stairMaxSums[1]=max(stairWeights[1], stairWeights[1]+stairWeights[0]);
        for (int i=2; i<stairMaxSums.length; i++)
        {
            stairMaxSums[i]=max(stairMaxSums[i-1]+stairWeights[i], stairMaxSums[i-2]+stairWeights[i]);
        }
        return stairMaxSums[stairMaxSums.length-1];
    }

    private static Map<Integer, Integer> minLengthOfSumMap;//ассоциативный массив дл€ задачи minLengthOfSum
    public static LinkedList<Integer> minLengthOfSum(int n)//” вас есть примитивный калькул€тор, который умеет выполн€ть всего три операции с текущим числом x: заменить x на 2x, 3x или x+1. ѕо данному целому числу 1?n?105 определите минимальное число операций k, необходимое, чтобы получить n из 1.
    {
        minLengthOfSumMap=new HashMap<>();//ключ Ч число, значение Ч стоимость
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
            buf = min(minLengthOfSumRecursion(n-1), minLengthOfSumRecursion(n/2))+1;
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
