package com.vladnamik.developer.machine.learning.neuralnetwork.layer;


import com.vladnamik.developer.datastructures.Matrix;
import com.vladnamik.developer.machine.learning.neuralnetwork.Neuron;

import java.util.List;

public class InputLayer extends Layer {
    /**
     * Количество параметров (количество "нейронов" входного слоя)
     */
    int paramsNumber;

    public InputLayer(int paramsNumber) {
        this.paramsNumber = paramsNumber;
    }


    @Override
    public Matrix getResult(Matrix singleInput) {
        return singleInput.copy();
    }

    @Override
    public int getNeuronsQuantity() {
        return paramsNumber;
    }

    @Override
    public Matrix getErrors(Matrix inputs, Matrix answers, Layer nextLayer) {
        return null;
    }

    @Override
    public Matrix getWeights() {
        return null;
    }

    @Override
    public List<Neuron> getNeurons() {
        return null;
    }

}
