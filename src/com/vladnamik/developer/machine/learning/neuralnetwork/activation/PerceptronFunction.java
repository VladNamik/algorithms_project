package com.vladnamik.developer.machine.learning.neuralnetwork.activation;


public class PerceptronFunction extends ActivationFunction {
    @Override
    public double function(double x) {
        return x > 0 ? 1 : 0;
    }

    /**
     * Не совсем производная, но для дальнейших расчетов подходит.
     */
    @Override
    public double functionDerivative(double x) {
        return 1;
    }
}
