package com.vladnamik.developer.concurrency.integration;


import com.vladnamik.developer.concurrency.integration.methods.IntegrationMethod;
import mpi.MPI;

import java.util.Arrays;
import java.util.function.Function;

@SuppressWarnings("unused")
public class CalculateIntegral {
    public static Double calculateBySteps(Function<Double, Double> function, Double begin, Double end, double step, IntegrationMethod method) {
        double i;
        double result = 0;
        for (i = begin + step; i < end; i += step)
            result += method.integrate(function, i - step, i);
        result += method.integrate(function, i - step, end);
        return result;
    }


    public static Double integrateInOneThread(Function<Double, Double> function, Double begin, Double end, Double step, IntegrationMethod method) {
        return CalculateIntegral.calculateBySteps(function, begin, end, step, method);
    }

    public static Double integrateUsingJoin(Function<Double, Double> function, Double begin, Double end, Double step, IntegrationMethod method, int threadQuantity) {
        CalculateIntegralThread[] calculations = new CalculateIntegralThread[threadQuantity];
        double currentBegin;
        double currentEnd;
        double length = end - begin;
        for (int i = 0; i < threadQuantity; i++) {
            currentBegin = begin + i * length / threadQuantity;
            currentEnd = begin + (i + 1) * length / threadQuantity;
            calculations[i] = new CalculateIntegralThread(function, currentBegin, currentEnd, step, method);
            calculations[i].start();
        }

        double result = 0;
        for (CalculateIntegralThread calculation : calculations) {
            try {
                calculation.join();
                result += calculation.getResult();
            } catch (InterruptedException e) {
                System.out.println("Interruption");
            }
        }

        return result;
    }

    public static Double integrateUsingStreams(Function<Double, Double> function, Double begin, Double end, Double step, IntegrationMethod method, final int arraySize) {
        final double length = end - begin;
        double[] array = new double[arraySize];
        for (int i = 0; i < arraySize; i++)
            array[i] = begin + i * length / arraySize;

        return Arrays.stream(array)
                .parallel()
                .map((x) -> CalculateIntegral.calculateBySteps(function, x, x + length / arraySize, step, method))
                .sum();
    }


    /**
     * Integration in different processes using MPJ.
     */
    static double integrateUsingMPJWithReduce(int currentProcessId, int processQuantity,
                                              Function<Double, Double> function, double step,
                                              double mainBegin, double mainEnd, IntegrationMethod method, int root) {
        //Определение участка интегрирования для каждого процесса
        double length = mainEnd - mainBegin;
        double begin = mainBegin + currentProcessId * length / processQuantity;
        double end = mainBegin + (currentProcessId + 1) * length / processQuantity;

        //интегрирование
        CalculateIntegralThread calculationObj = new CalculateIntegralThread(function, begin, end, step, method);
        calculationObj.run();

        //ответ для одного
        double[] oneProcessResult = {calculationObj.getResult()};
        double[] mainResult = {0};
        MPI.COMM_WORLD.Reduce(oneProcessResult, 0, mainResult, 0, 1, MPI.DOUBLE, MPI.SUM, root);

        return mainResult[0];
    }

    /**
     * Integration in different processes using MPJ.
     */
    static double integrateUsingMPJWithScatter(int currentProcessId, int processQuantity,
                                               Function<Double, Double> function, double step,
                                               double mainBegin, double mainEnd, IntegrationMethod method, int root) {
        double length = mainEnd - mainBegin;
        double[] begins;

        //Определение границ интегрирования для каждого процесса
//        if (currentProcessId == root) {
        begins = new double[processQuantity];
        for (int i = 0; i < processQuantity; i++) {
            begins[i] = mainBegin + i * length / processQuantity;
        }
//        }
        double[] begin = {0.0};
        MPI.COMM_WORLD.Scatter(begins, 0, 1, MPI.DOUBLE, begin, 0, 1, MPI.DOUBLE, root);
        double currentBegin = begin[0];
        double currentEnd = currentBegin + length / processQuantity;

        //интегрирование
        CalculateIntegralThread calculationObj = new CalculateIntegralThread(function, currentBegin, currentEnd, step, method);
        calculationObj.run();

        //передаём ответы
        double[] oneProcessResult = {calculationObj.getResult()};
        double[] allResults;
//        if (currentProcessId == root) {
        allResults = new double[processQuantity];
//        }
        MPI.COMM_WORLD.Gather(oneProcessResult, 0, 1, MPI.DOUBLE, allResults, 0, 1, MPI.DOUBLE, root);

//        return allResults == null ? 0.0 : Arrays.stream(allResults).sum();
        return Arrays.stream(allResults).sum();
    }
}
