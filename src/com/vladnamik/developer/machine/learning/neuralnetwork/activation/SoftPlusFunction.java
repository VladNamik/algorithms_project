package com.vladnamik.developer.machine.learning.neuralnetwork.activation;

public class SoftPlusFunction extends ActivationFunction {

    @Override
    public double function(double x) {
        return Math.log(1.0 + Math.pow(Math.E, x));
    }

    @Override
    public double functionDerivative(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
