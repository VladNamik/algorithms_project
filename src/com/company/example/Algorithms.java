package com.company.example;

import java.util.LinkedList;
import java.util.List;

import static com.company.example.DivideAndConquerAlgorithms.*;
import static com.company.example.DynamicProgramming.*;
public class Algorithms {
    public static long gcd(long a, long b)//нахождение наибольшего общего делителя
    {
        while ((a!=0) && (b!=0))
        {
            if (Math.max(a,b)==a)
                a=a - ((long)(a/b))*b;
            else
                b = b - ((long)(b/a))*a;
        }
        return a+b;
    }
    public static Comparable medianOf(Comparable... a)//медиана
    {
        return orderStatistic((a.length-1)/2,a);
    }
    public static void swap(Object[] array, int a, int b)//меняет местами два элемента массива по индексам
    {
        Object k = array[a];
        array[a]=array[b];
        array[b]=k;
    }

    public static int min(int... array)//находит минимум среди целых чисел
    {
        int min=array[0];
        for (int k : array)
            if (k<min)
                min=k;
        return min;
    }
    public static int max(int... array)//находит максимум среди целых чисел
    {
        int max=array[0];
        for (int k : array)
            if (k>max)
                max=k;
        return max;
    }

    private static int[] primeNumbers = {2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107};
    public static List<Integer> primeFactorization(int k)//разделяет число на простые множители
    {
        List<Integer> list = new LinkedList<>();
        if (k<=1)
            return list;
        int currentDividerNumberInArray=0;
        int currentDivider=primeNumbers[currentDividerNumberInArray];
        while(k!=1)
        {
            if (currentDivider>=Math.sqrt(k)+1)//проверяем, не осталось ли у нас простое число
            {
                list.add(k);
                break;
            }
            while(k%currentDivider==0)
            {
                list.add(currentDivider);
                k=k/currentDivider;
            }
            currentDividerNumberInArray++;
            if (currentDividerNumberInArray>=primeNumbers.length)
                currentDivider++;
            else
                currentDivider=primeNumbers[currentDividerNumberInArray];
        }
        return list;
    }


    public static Pair<Integer, Integer> getPairOfIndexOfMaxSum(int[] firstArray, int[] secondArray)//Даны два массива целых чисел одинаковой длины A[0..n?1] и B[0..n?1].Необходимо найти первую пару индексов i0 и j0, i0?j0, такую что A[i0]+B[j0]=max{A[i]+B[j],где0?i<n,0?j<n,i?j}.Время работы – O(n).
    {
        int[] firstArrayMaxElementsIndex = new int[firstArray.length];//массив индексов максимальных элементов первого массива на отрезках от 0 до индекса массива индексов
        int bufIndexOfMax=0;//буферный элемент, показывает индекс максимума на итерации
        for (int i=0; i<firstArray.length; i++)
        {
            if (firstArray[i]>firstArray[bufIndexOfMax])
                bufIndexOfMax=i;
            firstArrayMaxElementsIndex[i]=bufIndexOfMax;
        }
        Pair<Integer, Integer> pair = new Pair<>(firstArray.length-1, secondArray.length-1);
        int maxSum=firstArray[pair.first]+secondArray[pair.second];
        bufIndexOfMax=secondArray.length-1;
        for (int i=secondArray.length-1; i>=0; i--)
        {
            if (secondArray[bufIndexOfMax]<=secondArray[i])
            bufIndexOfMax=i;
            if (firstArray[firstArrayMaxElementsIndex[i]]+secondArray[bufIndexOfMax]>=maxSum)
            {
                pair.first=firstArrayMaxElementsIndex[i];
                pair.second=bufIndexOfMax;
                maxSum=firstArray[firstArrayMaxElementsIndex[i]]+secondArray[bufIndexOfMax];
            }
        }
        return pair;
    }

    public static int[] getTheNearestElementsOfSecondArrayInFirstArray(int[] firstArray, int[] secondArray)
    {
        int[] outputArray = new int[secondArray.length];
        int bufIndex;
        for(int i=0; i<secondArray.length; i++)
        {
            bufIndex = binarySearchForTheNearest(firstArray, secondArray[i]);
            if (bufIndex==-1)
            {
                outputArray[i]=0;
            }
            else if (bufIndex==firstArray.length-1)
            {
                outputArray[i]=bufIndex;
            }
            else if ((secondArray[i]-firstArray[bufIndex])<=(firstArray[bufIndex+1]-secondArray[i]))
            {
                outputArray[i]=bufIndex;
            }
            else
                outputArray[i]=bufIndex+1;
        }
        return outputArray;
    }
    public static int binarySearchForTheNearest(int[] array, int element)//возвращает индекс ближайшего меньшего
    {
        int begin=0;
        int end=array.length;
        int index=-1;//искомый индекс
        while (begin<end)
        {
            index=begin+(end-begin)/2;
            if (array[index]>=element)
            {
                end=index;
                index=end-1;
            }
            else
            {
                begin=index+1;
                index=begin-1;
            }
        }
        return index;
    }
}
