package com.vladnamik.developer.machine.learning.neuralnetwork;


import com.vladnamik.developer.datastructures.Matrix;
import com.vladnamik.developer.machine.learning.neuralnetwork.layer.HiddenLayer;
import com.vladnamik.developer.machine.learning.neuralnetwork.layer.InputLayer;
import com.vladnamik.developer.machine.learning.neuralnetwork.layer.Layer;
import com.vladnamik.developer.machine.learning.neuralnetwork.layer.OutputLayer;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

@SuppressWarnings("unused")
public class NeuralNetwork {
    private static final int DEFAULT_MAX_STEPS = 1000;

    private static final double DEFAULT_BATCH_SIZE_PART = 0.2;

    private double learningRate = 1;

    private Random random;

    private InputLayer inputLayer;

    private List<HiddenLayer> hiddenLayers;

    private OutputLayer outputLayer;

    private List<Layer> allLayers;

    public NeuralNetwork(InputLayer inputLayer, List<HiddenLayer> hiddenLayers
            , OutputLayer outputLayer, Random random) {
        this.inputLayer = inputLayer;
        this.hiddenLayers = hiddenLayers;
        this.outputLayer = outputLayer;
        combineAllLayers();
        this.random = random;
    }

    public NeuralNetwork(InputLayer inputLayer, List<HiddenLayer> hiddenLayers
            , OutputLayer outputLayer, Random random, double learningRate) {
        this(inputLayer, hiddenLayers, outputLayer, random);
        this.learningRate = learningRate;
    }

//    public NeuralNetwork(int inputsQuantity, int[] hiddenLayersNeuronsQuantities, int outputsQuantity) {
//        inputLayer = new InputLayer(inputsQuantity);
//        createHiddenLayers(hiddenLayersNeuronsQuantities);
//        createOutputLayer(outputsQuantity);
//        combineAllLayers();
//    }
//
//    private void createOutputLayer(int outputsQuantity) {
//
//    }
//
//    private void createHiddenLayers(int[] hiddenLayersNeuronsQuantities) {
//
//    }

    private void combineAllLayers() {
        allLayers = new ArrayList<>();
        allLayers.add(inputLayer);
        allLayers.addAll(hiddenLayers);
        allLayers.add(outputLayer);
    }

    /**
     * Прямое распространение активации.
     * Ответ нейронной сети на входные значения.
     *
     * @param inputs матрица входных значений размера (n, n_l0),
     *               где n_l0 — количество возможных параметров —
     *               количество нейронов во входном слое,
     *               n — количество примеров.
     * @return матрица ответов размера (n, n_L),
     * где n_L — размер выходного вектора —
     * количество нейронов в выходном слое.
     */
    public Matrix forwardPropagation(Matrix inputs) {
        Matrix bufferMatrix = inputs;
        for (Layer layer : allLayers) {
            bufferMatrix = layer.getResults(bufferMatrix);
        }
        return bufferMatrix;
    }

    /**
     * Алгоритм обратного распространения ошибки.
     * Обучение нейронной сети на примерах.
     *
     * @param inputs  матрица входных значений размера (n, n_l0),
     *                где n_l0 — количество возможных параметров —
     *                количество нейронов во входном слое,
     *                n — количество примеров.
     * @param answers матрица правильных ответов размера (n, n_L),
     *                где n_L — размер выходного вектора —
     *                количество нейронов в выходном слое.
     */
    public void backPropagationStep(Matrix inputs, Matrix answers) {
        forwardPropagation(inputs);

        int layersQuantity = allLayers.size() - 2;
        //работаем с последним слоем
        outputLayer.getErrors(allLayers.get(layersQuantity).getLastResults(), answers, null);

        //работаем со всеми остальными слоями (кроме первого)
        for (int i = layersQuantity; i > 0; i--) {
            allLayers.get(i).getErrors(allLayers.get(i - 1).getLastResults(), null, allLayers.get(i + 1));
        }

        Layer currentLayer;
        Matrix derivationOnBias;
        Matrix derivationOnWeight;
        for (int i = layersQuantity + 1; i > 0; i--) {
            currentLayer = allLayers.get(i);
            derivationOnBias = currentLayer.objectiveFunctionDerivationOnBias();
            derivationOnWeight = currentLayer.objectiveFunctionDerivationOnWeight(allLayers.get(i - 1).getLastResults());

            Neuron currentNeuron;
            for (int j = 0; j < currentLayer.getNeuronsQuantity(); j++) {
                currentNeuron = currentLayer.getNeurons().get(j);
                currentNeuron.setBias(currentNeuron.getBias() - learningRate * derivationOnBias.get(j, 0));
            }

            int weightsQuantityOnNeuron = allLayers.get(i - 1).getNeuronsQuantity();
            for (int j = 0; j < currentLayer.getNeuronsQuantity(); j++) {
                currentNeuron = currentLayer.getNeurons().get(j);

                currentNeuron.setWeights(currentNeuron.getWeights()
                        .minus(derivationOnWeight.getMatrix(j, j, 0, weightsQuantityOnNeuron - 1)
                                .transpose().times(learningRate)
                        )
                );
            }
        }
    }

    public void backPropagation(Matrix inputs, Matrix answers, int maxSteps, int batchSize) {
        Queue<Pair<Matrix, Matrix>> batches;

        for (int stepsCounter = 0; stepsCounter < maxSteps; stepsCounter++) {
            batches = getMiniBatches(inputs, answers, batchSize);

            for (Pair<Matrix, Matrix> pair : batches) {
                backPropagationStep(pair.getKey(), pair.getValue());
            }
        }
    }

    public void backPropagation(Matrix inputs, Matrix answers, int maxSteps) {
        backPropagation(inputs, answers, maxSteps, (int) (inputs.size()[0] * DEFAULT_BATCH_SIZE_PART) + 1);
    }

    public void backPropagation(Matrix inputs, Matrix answers) {
        backPropagation(inputs, answers, DEFAULT_MAX_STEPS);
    }

    private Queue<Pair<Matrix, Matrix>> getMiniBatches(Matrix inputX, Matrix inputY, int batchSize) {
        return Matrix.getMiniBatches(inputX, inputY, batchSize, random);
    }

    public InputLayer getInputLayer() {
        return inputLayer;
    }

    public List<HiddenLayer> getHiddenLayers() {
        return hiddenLayers;
    }

    public OutputLayer getOutputLayer() {
        return outputLayer;
    }

    public Random getRandom() {
        return random;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
}
