package com.vladnamik.developer.machine.learning.neuralnetwork.activation;


import com.vladnamik.developer.datastructures.Matrix;

public abstract class ActivationFunction {

    public abstract double function(double x);

    public Matrix function(Matrix x) {
        return x.apply(this::function);
    }

    public abstract double functionDerivative(double x);

    public Matrix functionDerivative(Matrix x) {
        return x.apply(this::functionDerivative);
    }
}
