package com.vladnamik.developer.machine.learning.neuralnetwork.activation;


public class SigmoidFunction extends ActivationFunction {

    @Override
    public double function(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    @Override
    public double functionDerivative(double x) {
        return this.function(x) * (1 - this.function(x));
    }
}
