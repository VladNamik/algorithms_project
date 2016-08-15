package com.vladnamik.developer.machine.learning.neuralnetwork.layer;


import com.vladnamik.developer.datastructures.Matrix;
import com.vladnamik.developer.machine.learning.neuralnetwork.Neuron;
import com.vladnamik.developer.machine.learning.neuralnetwork.objfunc.ObjectiveFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class OutputLayer extends Layer {
    private List<Neuron> neurons;

    private Random random;

    public OutputLayer(List<Neuron> neurons, long seed) {
        this.neurons = neurons;
        this.random = new Random(seed);
    }

    public OutputLayer(List<Neuron> neurons, Random random) {
        this.neurons = neurons;
        this.random = random;
    }

    /**
     * @param neuronsQuantity количество нейронов на этом слое.
     * @param numberOfInputs  количество нейронов на предыдущем слое.
     * @param seed            семя для рандома.
     */
    public OutputLayer(int neuronsQuantity, int numberOfInputs, long seed) {
        neurons = new ArrayList<>(neuronsQuantity);
        random = new Random(seed);
        for (int i = 0; i < neuronsQuantity; i++) {
            neurons.add(i, new Neuron(numberOfInputs, random));
        }
    }

    public OutputLayer(int neuronsQuantity, int numberOfInputs, Random random) {
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


    @Override
    public Matrix getErrors(Matrix inputs, Matrix answers, Layer nextLayer) {
        int inputsNumber = inputs.size()[0];
        int neuronsQuantity = getNeuronsQuantity();
        Matrix errors = new Matrix(inputsNumber, neuronsQuantity);

        ObjectiveFunction objectiveFunction;
        Matrix derivativesOnActivations;
        double derivativeOnActivation;
        Matrix singleInput;

        for (int i = 0; i < neuronsQuantity; i++) {
            objectiveFunction = neurons.get(i).getObjectiveFunction();
            derivativesOnActivations = new Matrix(inputsNumber, 1);
            for (int j = 0; j < inputsNumber; j++) {
                singleInput = inputs.getMatrix(j, j, 0, neurons.get(i).getWeights().size()[0] - 1);
                derivativeOnActivation = objectiveFunction.costFunctionDerivativeOnActivation(
                        neurons.get(i), singleInput, answers.get(j, 0));

                derivativesOnActivations.set(j, 0, derivativeOnActivation *
                        neurons.get(i).getActivationFunction().functionDerivative(
                                neurons.get(i).summatoryFunction(singleInput)
                        )
                );
            }

            errors.setMatrix(0, inputsNumber - 1, i, i, derivativesOnActivations);
        }

        this.errors = errors;
        return errors;
    }

    @Override
    public Matrix getWeights() {
        int neuronsQuantity = getNeuronsQuantity();
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

    public void setRandom(Random random) {
        this.random = random;
    }


}
