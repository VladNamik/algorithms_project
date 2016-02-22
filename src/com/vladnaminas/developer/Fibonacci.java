package com.vladnaminas.developer;


public class Fibonacci {
    public static long fibNumber(int num)//��������� ����� ��������� �� ������
    {
        long fib0 = 0;
        long fib1 = 1;
        long fib2 = 1;
        for (int i = 2; i<=num; i++)
        {
            fib2=fib0+fib1;
            fib0= fib1;
            fib1=fib2;
        }
        return fib2;
    }
    public static long fibLastDigit(long num)//��������� ����� ����� ��������� �� ������
    {
        return fibModulo(num, 10);
    }
    public static long fibModulo(long num, int m)//������� �� ������� ����� ��������� �� ������ num �� m
    {
        int fib0 = 0;
        int fib1 = 1;
        int fib2 = 1;
        int period = 0;
        for (int i = 2; i<=num; i++)
        {
            fib2=(fib0+fib1)%m;
            fib0 = fib1;
            fib1=fib2;
            if (fib0 == 0 && fib1 == 1) {
                period = i-1;
                break;
            }
        }
        if (period!=0) {
            period = (int) (num % (long) period);
            for (int i = 2; i <= period; i++)
            {
                fib2=(fib0+fib1)%m;
                fib0 = fib1;
                fib1=fib2;
            }
            if (period == 0)
                fib2=fib0;
        }
        return fib2;
    }
}
