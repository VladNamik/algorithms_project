package com.vladnamik.developer;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.vladnamik.developer.AlgorithmsUtils.*;
import static org.junit.Assert.assertEquals;

public class AlgorithmsUtilsTest {
    long seed = 1225;

    @Test
    public void testFibNumberFirstNumbers() throws Exception {
        assertEquals(0, fibNumber(0));
        assertEquals(1, fibNumber(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFibNumberNegativeNumber() throws Exception {
        fibNumber(-15);
    }

    @Test
    public void testFibNumberLarge() throws Exception {
        assertEquals(121393, fibNumber(26));
        assertEquals(10610209857723L, fibNumber(64));
        assertEquals(160500643816367088L, fibNumber(84));
    }

    @Test(timeout = 2)
    public void testFibNumberPerformance() throws Exception {
        long k = 0;
        for (int i = 0; i < 100; i++) {
            k = fibNumber(i);
        }
    }

    @Test
    public void testFibModuloFirstNumbers() throws Exception {
        assertEquals(1, fibModulo(1, 22));
        assertEquals(2, fibModulo(5, 3));
        assertEquals(3, fibModulo(6, 5));
    }

    @Test
    public void testFibModuloLargeDividerIsTen() throws Exception {
        assertEquals(1, fibModulo(132941, 10));
        assertEquals(5, fibModulo(193150, 10));
        assertEquals(7, fibModulo(725516, 10));
    }

    @Test
    public void testFibModuloLarge() throws Exception {
        assertEquals(22937, fibModulo(16759108071127739L, 44408));
    }

    @Test(timeout = 100)
    public void testFibModuloPerformance() throws Exception {
        long k;
        for (long i = 7; i < 1000000000L; i+= 99971) {
            k = fibModulo(i, 10);
        }
    }

    @Test
    public void testGcdOneAndOne() throws Exception {
        assertEquals(1, gcd(1, 1));
    }

    @Test
    public void testGcdLarge() throws Exception {
        assertEquals(1836, gcd(530442432, 1671466860));
        assertEquals(33264, gcd(72448992,170211888));
        assertEquals(6204, gcd(260326044,143994840));
    }

    @Test
    public void testGcdAndPrimeFactorizationRandom() throws Exception {
        Random random = new Random(seed);
        for (int i = 0; i < 100; i++) {
            int number1 = Math.abs(random.nextInt());
            int number2 = Math.abs(random.nextInt());
            int divider = 1;
            List<Integer> dividersForNumber1 = primeFactorization(number1);
            List<Integer> dividersForNumber2 = primeFactorization(number2);
            while(dividersForNumber1.size() > 0 && dividersForNumber2.size() > 0) {
                if (dividersForNumber1.get(0).equals(dividersForNumber2.get(0))) {
                    divider *= dividersForNumber1.get(0);
                    dividersForNumber1.remove(0);
                    dividersForNumber2.remove(0);
                } else if (dividersForNumber1.get(0) < dividersForNumber2.get(0)) {
                    dividersForNumber1.remove(0);
                } else {
                    dividersForNumber2.remove(0);
                }
            }
            assertEquals(divider, gcd(number1, number2));
        }
    }

    @Test
    public void testOrderStatisticOrderedArray() throws Exception {
        int size = 1000;
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size * 2; i+= 2) {
            arr[i / 2] = i;
        }
        for (int i = 0; i < size; i++) {
            assertEquals(i * 2, orderStatistic(i, arr));
        }
    }

    @Test
    public void testOrderStatisticRandom() throws Exception {
        Random random = new Random(seed);
        int size = Math.abs(random.nextInt() % 500);
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt();
        }
        Integer[] sorted = new Integer[size];
        System.arraycopy(arr, 0, sorted, 0, size);
        Arrays.sort(sorted);

        for (int i = 0; i < size; i++) {
            assertEquals(sorted[i], orderStatistic(i, arr));
        }
    }
}