package com.vladnamik.developer.data.mining.spamfilters;

import com.vladnamik.developer.datastructures.Matrix;
import com.vladnamik.developer.machine.learning.neuralnetwork.NeuralNetwork;
import com.vladnamik.developer.machine.learning.neuralnetwork.Neuron;
import com.vladnamik.developer.machine.learning.neuralnetwork.activation.TanhFunction;
import com.vladnamik.developer.machine.learning.neuralnetwork.layer.HiddenLayer;
import com.vladnamik.developer.machine.learning.neuralnetwork.layer.InputLayer;
import com.vladnamik.developer.machine.learning.neuralnetwork.layer.Layer;
import com.vladnamik.developer.machine.learning.neuralnetwork.layer.OutputLayer;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("unused")
public class NeuralNetworkSpamFilter implements SpamFilter {
    /**
     * Словарь всех доступных слов.
     * Индекс слова.
     */
    private Map<String, Integer> wordsDict = new HashMap<>();

    private NeuralNetwork neuralNetwork;

    private Random random;
    private long randomSeed;

    //    private Map<Matrix, Matrix> inputs = new HashMap<>();// примеры для обучения
    private List<Pair<String[], Boolean>> messages = new ArrayList<>();// сообщения, раздробленные на слова, спам / не спам

    public NeuralNetworkSpamFilter(String pathToWordsDictFile, long seed) throws IOException {
        //заполняем словарь слов.
        int[] wordIndex = {0};
        Arrays.stream(SpamFiltersService.getWordsFromMessage(
                SpamFiltersService.fileToString(Paths.get(pathToWordsDictFile))
        )).forEach(word -> wordsDict.put(word, wordIndex[0]++));

        this.randomSeed = seed;
        random = new Random(seed);

        neuralNetwork = createNeuralNetwork();
    }

    private NeuralNetwork createNeuralNetwork() {
        InputLayer inputLayer = new InputLayer(wordsDict.size());

        List<Neuron> hiddenLayerNeurons = new ArrayList<>(4);
        Neuron neuron11 = new Neuron(wordsDict.size(), random);
        Neuron neuron12 = new Neuron(wordsDict.size(), random);
        Neuron neuron13 = new Neuron(wordsDict.size(), random);
        Neuron neuron14 = new Neuron(wordsDict.size(), random);
        neuron11.setActivationFunction(new TanhFunction());
        neuron12.setActivationFunction(new TanhFunction());
        neuron13.setActivationFunction(new TanhFunction());
        neuron14.setActivationFunction(new TanhFunction());
        hiddenLayerNeurons.add(neuron11);
        hiddenLayerNeurons.add(neuron12);
        hiddenLayerNeurons.add(neuron13);
        hiddenLayerNeurons.add(neuron14);
        HiddenLayer hiddenLayer = new HiddenLayer(hiddenLayerNeurons, random);

        List<Neuron> hiddenLayerNeurons2 = new ArrayList<>(2);
        Neuron neuron21 = new Neuron(4, random);
        Neuron neuron22 = new Neuron(4, random);
        neuron21.setActivationFunction(new TanhFunction());
        neuron22.setActivationFunction(new TanhFunction());
        hiddenLayerNeurons2.add(neuron21);
        hiddenLayerNeurons2.add(neuron22);
        HiddenLayer hiddenLayer2 = new HiddenLayer(hiddenLayerNeurons2, random);

        List<Neuron> hiddenLayerNeurons3 = new ArrayList<>(2);
        Neuron neuron31 = new Neuron(2, random);
        Neuron neuron32 = new Neuron(2, random);
        neuron31.setActivationFunction(new TanhFunction());
        neuron32.setActivationFunction(new TanhFunction());
        hiddenLayerNeurons3.add(neuron31);
        hiddenLayerNeurons3.add(neuron32);
        HiddenLayer hiddenLayer3 = new HiddenLayer(hiddenLayerNeurons3, random);

        List<HiddenLayer> hiddenLayers = new ArrayList<>(3);
        hiddenLayers.add(hiddenLayer);
        hiddenLayers.add(hiddenLayer2);
        hiddenLayers.add(hiddenLayer3);

        Neuron outputNeuron = new Neuron(2, random);
        outputNeuron.setActivationFunction(new TanhFunction());
        List<Neuron> outputLayerNeurons = new ArrayList<>();
        outputLayerNeurons.add(outputNeuron);
        OutputLayer outputLayer = new OutputLayer(outputLayerNeurons, random);

        return new NeuralNetwork(inputLayer, hiddenLayers, outputLayer, random);
    }

    @Override
    public boolean isSpam(String message) {
        Matrix input = getInputVectorFromMessage(message);
        double result = neuralNetwork.forwardPropagation(input).get(0, 0);
        return result >= 0.5;
    }

    /**
     * @param message сообщение.
     * @return вектор размера (1, n_l0)
     */
    private Matrix getInputVectorFromMessage(String message) {
        String[] wordsFromMessage = SpamFiltersService.getWordsFromMessage(message);

        return getInputVectorFromMessage(wordsFromMessage);
    }

    private Matrix getInputVectorFromMessage(String[] wordsFromMessage) {
        Matrix input = new Matrix(1, wordsDict.size());

        Arrays.stream(wordsFromMessage).forEach(word -> {
            if (wordsDict.containsKey(word)) {
                input.set(0, wordsDict.get(word), input.get(0, wordsDict.get(word)) + 1d);
            }
        });

        return input;
    }

    private Pair<Matrix, Matrix> getInputsAndAnswersMatrices(int from, int to) {
//        int n = messages.size();
        int numberOfInputs = wordsDict.size();
        int numberOfOutputs = neuralNetwork.getOutputLayer().getNeuronsQuantity();
        Matrix inputs = new Matrix(to - from, numberOfInputs);
        Matrix answers = new Matrix(to - from, numberOfOutputs);

        for (int i = from; i < to; i++) {
            inputs.setMatrix(i - from, i - from, 0, numberOfInputs - 1, getInputVectorFromMessage(messages.get(i).getKey()));
            answers.set(i - from, 0, messages.get(i).getValue() ? 1d : 0d);
        }

        return new Pair<>(inputs, answers);
    }

    public void learn(int maxSteps) {
        int circlesNumber = 100;
        int n = messages.size();
        Collections.shuffle(messages, random);
        for (int i = 0; i < circlesNumber / 2 - 1; i++) {
            Pair<Matrix, Matrix> inputsAnsAnswers = getInputsAndAnswersMatrices(i * n / circlesNumber, (i + 1) * n / circlesNumber);
            neuralNetwork.backPropagation(inputsAnsAnswers.getKey(), inputsAnsAnswers.getValue(), maxSteps);
            System.out.println(i);
        }
    }

    public void learn(int maxSteps, int batchSize) {

    }

    public void learn(long millis) {
        //обучение определённое время
    }

    public void periodicallyLearn(long millis) {
        // обучение с периодическим сохранением
    }

    public void periodicallyLearn(int maxSteps) {

    }

    @Override
    public void clearInfo() {
        messages.clear();
//        inputs.clear();
//        neuralNetwork = createNeuralNetwork();
    }

    @Override
    public void addMessage(String message, boolean isSpam) {
        messages.add(new Pair<>(SpamFiltersService.getWordsFromMessage(message), isSpam));
    }

    @Override
    public void addMessages(Map<String, Boolean> messages) {
        messages.entrySet().forEach(x -> addMessage(x.getKey(), x.getValue()));
    }

    @Override
    public void saveData(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            //записываем seed
            writer.write(((Long) randomSeed).toString());
            writer.newLine();

            //записываем словарь слов (слово индекс)
            wordsDict.forEach((word, index) -> {
                try {
                    writer.write(word);
                    writer.write(' ');
                    writer.write(index.toString());
                    writer.write(' ');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.newLine();

            //записываем внутренние данные нейронной сети
            //входной слой записывать нет необходимости: количество слов в словаре и есть количество нейронов входного слоя

            //скрытые слои: количество слоёв; для каждого слоя с новой строки
            writer.write(((Integer) neuralNetwork.getHiddenLayers().size()).toString());
            writer.newLine();
            neuralNetwork.getHiddenLayers().forEach(hiddenLayer -> {
                try {
                    saveLayer(writer, hiddenLayer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            //выходной слой
            saveLayer(writer, neuralNetwork.getOutputLayer());
        }
    }

    //TODO finish this
    private void saveLayer(BufferedWriter writer, Layer layer) throws IOException {
        //количество нейронов
        writer.write(((Integer) layer.getNeuronsQuantity()).toString());
        //для каждого нейрона: learning rate, bias и веса
        for (Neuron neuron : layer.getNeurons()) {
            writer.write(((Double) neuron.getLearningRate()).toString());
            writer.write(' ');
            writer.write(((Double) neuron.getBias()).toString());
            writer.write(' ');
            Matrix weights = neuron.getWeights();
            int weightsQuantity = weights.size()[0];
            for (int i = 0; i < weightsQuantity; i++) {
                writer.write(((Double) weights.get(i, 0)).toString());
                writer.write(' ');
            }
            writer.newLine();
        }
    }

    //TODO implement
    @Override
    public void takeDataFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

        }
    }
}
