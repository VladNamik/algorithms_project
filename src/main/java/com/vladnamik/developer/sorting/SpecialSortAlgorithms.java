package com.vladnamik.developer.sorting;


@SuppressWarnings("unused")
public class SpecialSortAlgorithms {

    private static class EncodedNumber {
        static int[] digitArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
        static boolean[] digitSignificance = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};

        public static int get(int digit) {
            return digitArray[digit - 1];
        }
    }

    private static int getDigit(int number, int digit) {
        return (number >> (digit - 1)) % 2;
    }

    private static void digitRadixSort(int[] array, int digit) {
        int zeroQuantity = 0;
        int oneQuantity = 0;


        for (int i : array)//counting zero and one
            if (getDigit(i, digit) == 0)
                zeroQuantity++;
            else
                oneQuantity++;


        int[] newArray = new int[array.length];

        if (EncodedNumber.digitSignificance[digit - 1])//significance
            oneQuantity = array.length;
        else
            zeroQuantity = array.length;

        zeroQuantity -= 1;
        oneQuantity -= 1;


        for (int i = array.length - 1; i >= 0; i--)
            if (getDigit(array[i], digit) == 0) {
                newArray[zeroQuantity] = array[i];
                zeroQuantity--;
            } else {
                newArray[oneQuantity] = array[i];
                oneQuantity--;
            }

        for (int i = 0; i < array.length; i++)
            array[i] = newArray[i];
    }

    public static void intRadixSort(int[] array) {
        int k = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < 0)
                k++;
        }
        int[] reverseArray = new int[k];
        int[] notReverseArray = new int[array.length - k];
        int reverseCounter = 0, counter = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < 0) {
                reverseArray[reverseCounter] = -array[i];
                reverseCounter++;
            } else {
                notReverseArray[counter] = array[i];
                counter++;
            }
        }
        for (int i = 1; i <= 32; i++)
            digitRadixSort(reverseArray, EncodedNumber.get(i));
        for (int i = 1; i <= 32; i++)
            digitRadixSort(notReverseArray, EncodedNumber.get(i));
        counter = 0;
        for (int i = reverseArray.length - 1; i >= 0; i--)
            array[counter++] = -reverseArray[i];
        for (int i = 0; i < notReverseArray.length; i++)
            array[reverseArray.length + i] = notReverseArray[i];
    }

}
