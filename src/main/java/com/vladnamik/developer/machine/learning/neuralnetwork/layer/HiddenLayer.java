package com.vladnamik.developer.machine.learning.neuralnetwork.layer;


import com.vladnamik.developer.datastructures.Matrix;
import com.vladnamik.developer.machine.learning.neuralnetwork.Neuron;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class HiddenLayer extends Layer {
    private List<Neuron> neurons;

    private Random random;

    public HiddenLayer(List<Neuron> neurons, long seed) {
        this.neurons = neurons;
        this.random = new Random(seed);
    }

    public HiddenLayer(List<Neuron> neurons, Random random) {
        this.neurons = neurons;
        this.random = random;
    }

    public HiddenLayer(int neuronsQuantity, int numberOfInputs, long seed) {
        neurons = new ArrayList<>(neuronsQuantity);
        random = new Random(seed);
        for (int i = 0; i < neuronsQuantity; i++) {
            neurons.add(i, new Neuron(numberOfInputs, random));
        }
    }

    public HiddenLayer(int neuronsQuantity, int numberOfInputs, Random random) {
        neurons = new ArrayList<>(neuronsQuantity);
        for (int i = 0; i < neuronsQuantity; i++) {
            neurons.add(i, new Neuron(numberOfInputs, random));
        }
    }

    @Override
    public Matrix getResult(Matrix singleInput) {
        int neuronsQuantity = getNeuronsQuantity();
        Matrix resultVector = new Matrix(1, neuronsQuantity);

        for (int i = 0; i < neuronsQuantity; i++) {
            resultVector.set(0, i, neurons.get(i).forwardPass(singleInput));
        }

        return resultVector;
    }

    @Override
    public int getNeuronsQuantity() {
        return neurons.size();
    }

//    @Override
//    public Matrix getErrors(Matrix inputs, Matrix answers, Layer nextLayer) {
//        Matrix errorsOfNextLayer = nextLayer.getErrors();
//        Matrix weightsOfNextLayer = nextLayer.getWeights();
//
//        int inputsNumber = MatrixService.size(inputs)[0];
//        int neuronsQuantity = getNeuronsQuantity();
//        Matrix sigmaDerivative = new Matrix(neuronsQuantity);
//
//        Neuron currentNeuron;
//        Matrix singleInput;
//        Matrix derivativeVector = new Matrix(inputsNumber, 1);
//        for (int i = 0; i < neuronsQuantity; i++) {
//            currentNeuron = neurons.get(i);
//
//            for (int j = 0; j < inputsNumber; j++) {
//                singleInput = inputs.getMatrix(j, j, 0, neuronsQuantity - 1);
//                derivativeVector.set(j, 0, currentNeuron.getActivationFunction()
//                        .functionDerivative(currentNeuron.summatoryFunction(singleInput)));
//            }
//            sigmaDerivative.set(i, 0, currentNeuron.getObjectiveFunction().costFunctionSum(derivativeVector));
//        }
//
//        Matrix errors = weightsOfNextLayer.transpose().times(errorsOfNextLayer).arrayTimes(sigmaDerivative);
//        this.errors = errors;
//        return errors;
//    }

    // W_(l+1).t*errors_(l+1).t*sigma'(summatory)
    // ((n_l, n_(l+1)) * (n_(l+1),n) .* (n_l, n))).t = (n, n_l)
    @Override
    public Matrix getErrors(Matrix inputs, Matrix answers, Layer nextLayer) {
        //размер (n, n_(l+1))
        Matrix errorsOfNextLayer = nextLayer.getErrors();
        //размер (n_(l+1), n_l)
        Matrix weightsOfNextLayer = nextLayer.getWeights();

        int inputsNumber = inputs.size()[0];
        int neuronsQuantity = getNeuronsQuantity();
        //размер (n_l, n)
        Matrix sigmaDerivative = new Matrix(neuronsQuantity, inputsNumber);

        Neuron currentNeuron;
        Matrix singleInput;
        //размер (n, 1)
        Matrix derivativeVector = new Matrix(inputsNumber, 1);
        for (int i = 0; i < neuronsQuantity; i++) {
            currentNeuron = neurons.get(i);

            for (int j = 0; j < inputsNumber; j++) {
                singleInput = inputs.getMatrix(j, j, 0, currentNeuron.getWeights().size()[0] - 1);
                derivativeVector.set(j, 0, currentNeuron.getActivationFunction()
                        .functionDerivative(currentNeuron.summatoryFunction(singleInput)));
            }
            sigmaDerivative.setMatrix(i, i, 0, inputsNumber - 1, derivativeVector.transpose());
        }

        Matrix errors = (weightsOfNextLayer.transpose()
                .times(errorsOfNextLayer.transpose())
                .arrayTimes(sigmaDerivative)
        ).transpose();

        this.errors = errors;
        return errors;
    }

    @Override
    public Matrix getWeights() {
        int neuronsQuantity = neurons.size();
        int weightsQuantityOnNeuron = neurons.get(0).getWeights().size()[0];
        Matrix weightsMatrix = new Matrix(neuronsQuantity, weightsQuantityOnNeuron);

        for (int i = 0; i < neuronsQuantity; i++) {
            weightsMatrix.setMatrix(i, i, 0, weightsQuantityOnNeuron - 1, neurons.get(i).getWeights().transpose());
        }

        return weightsMatrix;
    }

    @Override
    public List<Neuron> getNeurons() {
        return neurons;
    }

    public Random getRandom() {
        return random;
    }

    public void setNeurons(List<Neuron> neurons) {
        this.neurons = neurons;
    }
}
