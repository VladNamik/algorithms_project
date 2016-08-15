package com.vladnamik.developer.machine.learning.neuralnetwork;

import com.vladnamik.developer.datastructures.Matrix;
import com.vladnamik.developer.exceptions.WrongMatrixSizeException;
import com.vladnamik.developer.machine.learning.neuralnetwork.activation.ActivationFunction;
import com.vladnamik.developer.machine.learning.neuralnetwork.activation.SigmoidFunction;
import com.vladnamik.developer.machine.learning.neuralnetwork.objfunc.ObjectiveFunction;
import com.vladnamik.developer.machine.learning.neuralnetwork.objfunc.QuadraticObjectiveFunction;
import javafx.util.Pair;

import java.util.Queue;
import java.util.Random;

@SuppressWarnings("unused")
public class Neuron {

    public static final double DEFAULT_BEGIN_WEIGHT_MIN = -1.0;

    public static final double DEFAULT_BEGIN_WEIGHT_MAX = 1.0;

    private static final int DEFAULT_MAX_STEPS_SGD = 1000;

    private static final double EPS = 1e-10;

    private static final int DEFAULT_BATCH_SIZE_SGD = 2;

    /**
     * Рандомизатор.
     */
    private Random random;

    /**
     * Смещение.
     */
    private double bias = 0.0;

    /**
     * Вертикальный вектор весов нейрона формы (m, 1)
     * m — кол-во входов ({numberOfInputs}).
     */
    private Matrix weights;

    /**
     * Кол-во входов.
     */
    private int numberOfInputs;

    /**
     * Константа скорости обучения.
     */
    double learningRate = 1.0;

    /**
     * Активационная функция нейрона, сигмоидальная по умолчанию.
     */
    private ActivationFunction activationFunction = new SigmoidFunction();

    /**
     * Целевая функция, квадратичная по умолчанию.
     */
    private ObjectiveFunction objectiveFunction = new QuadraticObjectiveFunction();


    public Neuron(double bias, Matrix weights, ActivationFunction activationFunction,
                  ObjectiveFunction objectiveFunction) {
        this.bias = bias;
        if (weights.size()[1] != 1) {
            throw new WrongMatrixSizeException(weights.toString() + "size needs to be (m, 1)");
        }
        this.weights = weights;
        numberOfInputs = weights.size()[0];
        this.activationFunction = activationFunction;
        this.objectiveFunction = objectiveFunction;
    }

    public Neuron(double bias, Matrix weights, ActivationFunction activationFunction) {
        this.bias = bias;
        if (weights.size()[1] != 1) {
            throw new WrongMatrixSizeException(weights.toString() + "size needs to be (m, 1)");
        }
        this.weights = weights;
        numberOfInputs = weights.size()[0];
        this.activationFunction = activationFunction;
    }

    public Neuron(double bias, Matrix weights) {
        this.bias = bias;
        if (weights.size()[1] != 1) {
            throw new WrongMatrixSizeException(weights.toString() + "size needs to be (m, 1)");
        }
        this.weights = weights;
        this.numberOfInputs = weights.size()[0];
    }

    public Neuron(int numberOfInputs, long seed, ActivationFunction activationFunction) {
        this.random = new Random(seed);
        this.numberOfInputs = numberOfInputs;
        this.activationFunction = activationFunction;
        randomFill(DEFAULT_BEGIN_WEIGHT_MIN, DEFAULT_BEGIN_WEIGHT_MAX);
    }

    public Neuron(int numberOfInputs, long seed) {
        this.random = new Random(seed);
        this.numberOfInputs = numberOfInputs;
        randomFill(DEFAULT_BEGIN_WEIGHT_MIN, DEFAULT_BEGIN_WEIGHT_MAX);
    }

    public Neuron(int numberOfInputs, Random random) {
        this.random = random;
        this.numberOfInputs = numberOfInputs;
        randomFill(DEFAULT_BEGIN_WEIGHT_MIN, DEFAULT_BEGIN_WEIGHT_MAX);
    }

    private void randomFill(double from, double to) {
        Matrix weights = new Matrix(numberOfInputs, 1);
        for (int i = 0; i < numberOfInputs; i++) {
            weights.set(i, 0, randomDouble(from, to));
        }

        this.weights = weights;
    }

    private double randomDouble(double from, double to) {
        return Math.min(from, to) + (random.nextDouble()) * Math.abs(to - from);
    }

    /**
     * Сумматорная функция.
     * sum(x*w, m) + b
     * singleInput - вектор входов формы (1, m),
     * m — {@code numberOfInputs}
     */
    public double summatoryFunction(Matrix singleInput) {
        return singleInput.times(weights).get(0, 0) + bias;
    }

    /**
     * @param singleInput вектор входов формы (1, m), m — {@code numberOfInputs}.
     * @return вектор значений сумматорной функции ответа (формы (m, 1)).
     */
    public Matrix summatoryDerivativeOnWFunction(Matrix singleInput) {
        if (singleInput.size()[1] != numberOfInputs) {
            throw new WrongMatrixSizeException(singleInput.toString() +
                    "need to be (1, " + singleInput.toString() + ")");
        }
        return singleInput.transpose();
    }

    /**
     * @param singleInput вектор входов формы (1, m), m — {@code numberOfInputs}.
     * @param wNumber     номер веса, по которому необходимо найти производную.
     *                    Если wNumber = -1, находим по bias.
     * @return значение производной сумматорной функции по весу с номером {@code wNumber}.
     */
    public double summatoryDerivativeOnWFunction(Matrix singleInput, int wNumber) {
        if (wNumber == -1) {
            return 1d;
        }
        return singleInput.getArray()[0][wNumber];
    }

    /**
     * активационная функция логистического нейрона
     * singleInput - вектор входов формы (1, m),
     * m — numberOfInputs.
     *
     * @param singleInput вектор входов (1, m).
     * @return выход нейрона.
     */
    public double forwardPass(Matrix singleInput) {
        return activationFunction.function(summatoryFunction(singleInput));
    }

    /**
     * Векторизованная активационная функция логистического нейрона.
     * {@code inputMatrix} - матрица примеров размера (n, m), каждая строка - отдельный пример,
     * n - количество примеров, m - количество переменных.
     * Возвращает вертикальный вектор размера (n, 1) с выходными активациями нейрона
     *
     * @param inputMatrix матрица примеров размера (n, m)
     * @return вертикальный вектор размера (n, 1) с выходными активациями нейрона
     */
    public Matrix vectorizedForwardPass(Matrix inputMatrix) {
        Matrix resultVector = new Matrix(inputMatrix.size()[0], 1);
        int i = 0;
        for (double[] singleInput : inputMatrix.getArray()) {
            resultVector.set(i, 0, forwardPass(new Matrix(singleInput, 1)));
            i++;
        }
        return resultVector;
    }

    /**
     * Внешний цикл алгоритма градиентного спуска.
     * x - матрица входных активаций (n, m)
     * y - вектор правильных ответов (n, 1)
     * <p>
     * batchSize - размер батча, на основании которого
     * рассчитывается градиент и совершается один шаг алгоритма
     * <p>
     * eps - критерий остановки номер один: если разница между значением целевой функции
     * до и после обновления весов меньше eps - алгоритм останавливается.
     * Вторым вариантом была бы проверка размера градиента, а не изменение функции,
     * что будет работать лучше - неочевидно. В заданиях используйте первый подход.
     * <p>
     * maxSteps - критерий остановки номер два: если количество обновлений весов
     * достигло maxSteps, то алгоритм останавливается.
     * <p>
     * Метод возвращает {@code true}, если отработал первый критерий остановки (спуск сошёлся)
     * и {@code false}, если второй (спуск не достиг минимума за отведённое время).
     */
    public boolean sgd(Matrix x, Matrix y, int batchSize, double eps, int maxSteps) {
        boolean isStoppedByEps = false;

        Queue<Pair<Matrix, Matrix>> batches;
        mainCycle:
        for (int stepsCounter = 0; stepsCounter < maxSteps; stepsCounter++) {
            batches = getMiniBatches(x, y, batchSize);

            for (Pair<Matrix, Matrix> pair : batches) {
                if (updateMiniBatch(pair.getKey(), pair.getValue(), eps)) {
                    isStoppedByEps = true;
                    break mainCycle;
                }
            }
        }
        return isStoppedByEps;
    }

    /**
     * Мини-батчи, на основании которых рассчитывается градиент
     * и совершается один шаг алгоритма {@code sgd}.
     *
     * @return {@code List<Pair<Matrix, Matrix>}, где ключ в паре— матрица входных активаций (batchSize, m),
     * значение — вектор правильных ответов (batchSize, 1).
     */
    private Queue<Pair<Matrix, Matrix>> getMiniBatches(Matrix inputX, Matrix inputY, int batchSize) {
        return Matrix.getMiniBatches(inputX, inputY, batchSize, random);
    }

    /**
     * x - матрица размера (batchSize, m)
     * y - вектор правильных ответов размера (batchSize, 1)
     * eps - критерий остановки номер один: если разница между значением целевой функции
     * до и после обновления весов меньше eps - алгоритм останавливается.
     * <p>
     * Рассчитывает градиент и обновляет веса нейрона. Если ошибка изменилась меньше,
     * чем на eps - возвращает true, иначе возвращает false.
     */
    private boolean updateMiniBatch(Matrix x, Matrix y, double eps) {
        double beginValueOfObjFunc = objectiveFunction.function(this, x, y);

        //обновляем веса
        Pair<Matrix, Double> gradient = objectiveFunction.gradientOnWeights(this, x, y);
        weights = weights.minus(gradient.getKey().times(learningRate));
        bias -= gradient.getValue() * learningRate;

        double endValueOfObjFunc = objectiveFunction.function(this, x, y);
        return Math.abs(beginValueOfObjFunc - endValueOfObjFunc) < eps;
    }

    public boolean sgd(Matrix x, Matrix y, int maxSteps) {
        return sgd(x, y, DEFAULT_BATCH_SIZE_SGD, EPS, DEFAULT_MAX_STEPS_SGD);
    }

    public boolean sgd(Matrix x, Matrix y) {
        return sgd(x, y, DEFAULT_MAX_STEPS_SGD);
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public Matrix getWeights() {
        return weights;
    }

    public void setWeights(Matrix weights) {
        this.weights = weights;
        this.numberOfInputs = weights.size()[0];
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

    public ObjectiveFunction getObjectiveFunction() {
        return objectiveFunction;
    }

    public void setObjectiveFunction(ObjectiveFunction objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public Random getRandom() {
        return random;
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }


}
