package com.vladnamik.developer.concurrency.integration.methods;


import java.util.function.Function;

@SuppressWarnings("unused")
public class RectangularRightNodeMethod implements IntegrationMethod {

    public Double integrate(Function<Double, Double> function, Double begin, Double end) {
        return (end - begin) * function.apply(end);
    }

}
