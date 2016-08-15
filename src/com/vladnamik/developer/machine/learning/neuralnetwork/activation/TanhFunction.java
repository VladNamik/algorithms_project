package com.vladnamik.developer.machine.learning.neuralnetwork.activation;

public class TanhFunction extends ActivationFunction {
    @Override
    public double function(double x) {
        return Math.tanh(x);
    }

    @Override
    public double functionDerivative(double x) {
        return 4 * Math.pow(Math.cosh(x), 2) / Math.pow(Math.cosh(2 * x) + 1, 2);
    }
}
