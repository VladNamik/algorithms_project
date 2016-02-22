package com.vladnaminas.developer;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SortAlgorithms {

    private static class EncodedNumber//���������
    {
        static int[] digitArray = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32};
        static boolean[] digitSignificance = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true};

        public static int get(int digit)
        {
            return digitArray[digit-1];
        }
    }
    private static int getDigit(int number, int digit)//��������� �������� ������� � �����
    {
        return (number >> (digit-1))%2;
    }
    private static void digitRadixSort(int[] array, int digit)//��������������� ��� ����������� ����������
    {
        int zeroQuantity =0;
        int oneQuantity = 0;



        for (int i : array)//counting zero and one
            if (getDigit(i,digit)==0)
                zeroQuantity++;
            else
                oneQuantity++;



        int[] newArray = new int[array.length];

        if (EncodedNumber.digitSignificance[digit-1])//significance
            oneQuantity=array.length;
        else
            zeroQuantity=array.length;

        zeroQuantity-=1;
        oneQuantity-=1;


        for (int i = array.length-1; i>=0; i--)
            if (getDigit(array[i],digit)==0) {
                newArray[zeroQuantity]=array[i];
                zeroQuantity--;
            }
            else{
                newArray[oneQuantity]=array[i];
                oneQuantity--;
            }

        for (int i=0; i<array.length; i++)
            array[i]=newArray[i];
    }
    public static void intRadixSort(int[] array)//����������� ���������� � �������������� ��������� EncodedNumber
    {
        int k=0;
        for (int i=0; i < array.length; i++)
        {
            if (array[i]<0)
                k++;
        }
        int[] reverseArray = new int[k];
        int[] notReverseArray = new int[array.length-k];
        int reverseCounter=0, counter=0;
        for (int i=0; i < array.length; i++)
        {
            if (array[i]<0)
            {
                reverseArray[reverseCounter]=-array[i];
                reverseCounter++;
            }
            else
            {
                notReverseArray[counter]=array[i];
                counter++;
            }
        }
        for (int i = 1 ; i<= 32; i++)
            digitRadixSort(reverseArray, EncodedNumber.get(i));
        for (int i=1 ; i<=32 ; i++)
            digitRadixSort(notReverseArray, EncodedNumber.get(i));
        counter=0;
        for (int i=reverseArray.length-1; i>=0; i--)
            array[counter++]=-reverseArray[i];
        for (int i=0; i< notReverseArray.length; i++)
            array[reverseArray.length+i]=notReverseArray[i];
    }
    public static int getInversionCounter(Comparable[] array)//������� ���������� ��������(���������� mergeSort � ����� �����������)
    {
        Comparable[] newArray= new Comparable[(int)Math.pow(2, (int)(Math.log(array.length)/Math.log(2))+1)];
        for (int i =0 ; i< array.length; i++)
            newArray[i]=array[i];
        inversionCounter=0;
        for (int i=array.length; i< newArray.length; i++)
            newArray[i]=Integer.MAX_VALUE;
        mergeSort(newArray);

        return inversionCounter;
    }
    private static int inversionCounter=0;
    public static  void mergeSort(Comparable[] array)//���������� ��������
    {
        Queue<Comparable[]> queue = new LinkedList<>();
        for (Comparable element : array)
            queue.add(new Comparable[]{element});
        Comparable[] first, second;
        while(queue.size()>1)
        {
            first=queue.remove();
            second=queue.remove();
            queue.add(merge(first, second));
        }
        Comparable[] outArray=queue.remove();
        for(int i =0; i<array.length; i++)
            array[i]=outArray[i];
    }
    private static Comparable[] merge(Comparable[] firstArray, Comparable[] secondArray)//��������������� ��� ���������� ��������
    {
        Comparable[] outArray = new Comparable[firstArray.length+secondArray.length];
        int i=0, j=0;
        for (; (i<firstArray.length) && (j<secondArray.length); )
            if (firstArray[i].compareTo(secondArray[j])>0)
            {
                outArray[i+j]=secondArray[j];
                j++;
                inversionCounter+=firstArray.length-i;
            }
            else
            {
                outArray[i+j]=firstArray[i];
                i++;
            }
        for (; i< firstArray.length;i++)
            outArray[i+j]=firstArray[i];
        for (; j< secondArray.length;j++)
            outArray[i+j]=secondArray[j];
        return outArray;
    }
    public static void bubleSort(Comparable[] array)// ���������� ���������
    {
        for (int i = 0; i< array.length-1; i++)
            for (int j = i+1; j< array.length; j++)
                if (array[i].compareTo(array[j])>0)
                {
                    Comparable a = array[i];
                    array[i]=array[j];
                    array[j]=a;
                }
    }
    public static void QuickSort(Comparable[] inputArray)// ������� ����������
    {
        Queue<Comparable[]> queue = new LinkedList<>();
        Queue<Integer> indexQueue = new LinkedList<>();
        indexQueue.add(0);
        queue.add(inputArray);
        Comparable[] array;
        int left ;
        int right;
        Comparable median;
        int currentIndex ;
        int trueIndex;
        Comparable[] outputArray=new Comparable[inputArray.length];
        while (queue.size()>0) {
            array=queue.remove();
            trueIndex=indexQueue.remove();
            left = 0;
            right = array.length - 1;
            currentIndex = left;
            if ((median = Algorithms.medianOf(array[left], array[right], array[(right - left) / 2])) == array[right])
                Algorithms.swap(array, left, right);
            else if (median == array[(right - left) / 2])
                Algorithms.swap(array, left, (right - left) / 2);
            for (int i = left + 1; i <= right; i++) {
                if (array[i].compareTo(array[left]) <= 0) {
                    currentIndex++;
                    Algorithms.swap(array, currentIndex, i);
                }
            }
            Algorithms.swap(array, currentIndex, left);
            int leftSimilarIndex=currentIndex;
            while (leftSimilarIndex>=0 && array[leftSimilarIndex]==array[currentIndex])
                leftSimilarIndex--;
            leftSimilarIndex++;
            if (leftSimilarIndex>0) {
                queue.add(Arrays.copyOfRange(array, 0, leftSimilarIndex));
                indexQueue.add(trueIndex);
            }
            if (currentIndex+1<array.length) {
                queue.add(Arrays.copyOfRange(array, currentIndex + 1, array.length));
                indexQueue.add(trueIndex + currentIndex + 1);
            }
            for (int i=leftSimilarIndex; i<=currentIndex; i++)
            outputArray[i+trueIndex]=array[i];
        }
        for (int i=0; i< outputArray.length; i++)
            inputArray[i]=outputArray[i];
    }

}
