package com.vladnamik.developer.concurrency.integration.methods;

import java.util.function.Function;

@FunctionalInterface
public interface IntegrationMethod {
    Double integrate(Function<Double, Double> function, Double begin, Double end);
}
