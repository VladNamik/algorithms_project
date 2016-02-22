package com.company.example;

import java.util.Arrays;
import java.util.Comparator;
import static com.company.example.Algorithms.*;

//Алгоритмы, построенные по принципу "Разделяй и властвуй"
public class DivideAndConquerAlgorithms {
    public static <T> int binarySearch(Comparable<T>[] array, T element)//Бинарный поиск. В отсортированном массиве возвращает индекс искомого элемента
    {
        int left=0;
        int right=array.length-1;
        int index;
        int compare;
        while(left<=right)
        {
            index=left+(right-left)/2;
            if ((compare=array[index].compareTo(element))==0)
                return index+1;
            else if (compare>0)
                right=index-1;
            else
                left=index+1;
        }
        return -1;
    }

    public static Comparable orderStatistic(int k, Comparable... a)//поиск k-той порядковой статистики в массиве
    {
        Comparable[] array = Arrays.copyOf(a, a.length);
        int left=0;
        int right=array.length-1;
        int stat=-1;
        int currentIndex;
        while (stat!=k)
        {
            currentIndex=left+(int)(Math.random()*(right-left)/2);
            swap(array,left,currentIndex);
            currentIndex=left+1;
            for (int i=left+1; i<=right; i++)
            {
                if (array[left].compareTo(array[i])>0) {
                    swap(array, currentIndex, i);
                    currentIndex++;
                }
            }
            swap(array, currentIndex-1, left);
            if (currentIndex-1>=k)
                right=currentIndex-2;
            else
                left=currentIndex;
            if (left>right && (currentIndex-1)!=k)
                return null;
            stat=currentIndex-1;
        }
        return array[stat];
    }
}
