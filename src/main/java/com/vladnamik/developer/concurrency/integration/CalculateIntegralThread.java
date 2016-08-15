package com.vladnamik.developer.concurrency.integration;


import com.vladnamik.developer.concurrency.integration.methods.IntegrationMethod;

import java.util.function.Function;

public class CalculateIntegralThread extends Thread {
    Double begin;
    Double end;
    Function<Double, Double> function;
    Double result;
    double step;
    IntegrationMethod method;


    public CalculateIntegralThread(Function<Double, Double> function, Double begin, Double end, double step, IntegrationMethod method) {
        this.function = function;
        this.begin = begin;
        this.end = end;
        this.step = step;
        this.method = method;
    }

    public Double getResult() {
        return result;
    }

    public CalculateIntegralThread returnThis() {
        return this;
    }

    @Override
    public void run() {
        result = CalculateIntegral.calculateBySteps(function, begin, end, step, method);
    }

}
