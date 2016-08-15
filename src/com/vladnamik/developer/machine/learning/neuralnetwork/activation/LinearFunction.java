package com.vladnamik.developer.machine.learning.neuralnetwork.activation;

public class LinearFunction extends ActivationFunction {
    @Override
    public double function(double x) {
        return x;
    }

    @Override
    public double functionDerivative(double x) {
        return 1;
    }
}
