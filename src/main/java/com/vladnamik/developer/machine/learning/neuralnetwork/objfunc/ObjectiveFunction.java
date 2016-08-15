package com.vladnamik.developer.machine.learning.neuralnetwork.objfunc;

import com.vladnamik.developer.datastructures.Matrix;
import com.vladnamik.developer.machine.learning.neuralnetwork.Neuron;
import javafx.util.Pair;

public abstract class ObjectiveFunction {
    /**
     * @param x - матрица входных активаций (n, m), n — кол-во примеров, m — кол-во входов
     * @param y - вектор правильных ответов (n, 1).
     */
    public double function(Neuron neuron, Matrix x, Matrix y) {
        int n = x.size()[0];
        Matrix costFuncResults = new Matrix(n, 1);
        double receivedAnswer;
        double correctAnswer;
        for (int i = 0; i < n; i++) {
            receivedAnswer = neuron.forwardPass(new Matrix(x.getArray()[i], 1));
            correctAnswer = y.get(i, 0);
            costFuncResults.set(i, 0, costFunction(receivedAnswer, correctAnswer));
        }

        return costFunctionSum(costFuncResults);
    }

    public abstract double costFunction(double receivedAnswer, double correctAnswer);

    public abstract double costFunctionDerivative(double receivedAnswer, double correctAnswer);

    /**
     * @param costFuncResults вектор значений функции потерь или её произодной
     *                        ({@code costFunction} или {@code costFunctionDerivative})
     *                        (n, 1),
     *                        усреднённое по количеству примеров (n).
     * @return усреднённую по всем примерам сумму.
     */
    public abstract double costFunctionSum(Matrix costFuncResults);

    /**
     * @param singleInput   вектор входных значений (1, m), где m — кол-во весов.
     * @param correctAnswer правильный (ожидаемый) отклик нейрона.
     * @return производная функции потерь по активационной функции.
     */
    public double costFunctionDerivativeOnActivation(Neuron neuron, Matrix singleInput, double correctAnswer) {
        return costFunctionDerivative(neuron.forwardPass(singleInput), correctAnswer);
    }

    /**
     * @return производная функции потерь по сумматорной функции.
     */
    public double costFunctionDerivativeOnSummatory(Neuron neuron, Matrix singleInput, double correctAnswer) {
        double dfOnDz = neuron.getActivationFunction().functionDerivative(neuron.summatoryFunction(singleInput));
        return costFunctionDerivativeOnActivation(neuron, singleInput, correctAnswer) * dfOnDz;
    }

    /**
     * @param wNumber вес, по которому ищем производную.
     *                Если {@code wNumber} = -1, ищем по bias.
     * @return производная функции потерь по весу с номером {@code wNumber}.
     */
    public double costFunctionDerivativeOnWi(Neuron neuron, Matrix singleInput, double correctAnswer, int wNumber) {
        double dzOnDw = neuron.summatoryDerivativeOnWFunction(singleInput, wNumber);
        return costFunctionDerivativeOnSummatory(neuron, singleInput, correctAnswer) * dzOnDw;
    }

    /**
     * @param x       - матрица входных активаций (n, m), n — кол-во примеров, m — кол-во входов.
     * @param y       - вектор правильных ответов (n, 1).
     * @param wNumber — номер веса, по которому находится производная.
     *                Если {@code wNumber} = -1, ищем по bias.
     * @return производная целевой функции по весу с номером {@code wNumber}.
     */
    public double functionDerivativeOnWi(Neuron neuron, Matrix x, Matrix y, int wNumber) {
        int n = x.size()[0];
        Matrix costDerivativeFuncResult = new Matrix(n, 1);

        Matrix singleInput;
        double correctAnswer;
        for (int i = 0; i < n; i++) {
            singleInput = new Matrix(x.getArray()[i], 1);
            correctAnswer = y.get(i, 0);
            costDerivativeFuncResult.set(i, 0, costFunctionDerivativeOnWi(neuron, singleInput, correctAnswer, wNumber));
        }

        return costFunctionSum(costDerivativeFuncResult);
    }

    /**
     * @return вектор значений (m, 1) — градиент целевой функции по весам,
     * где m — кол-во весов нейрона
     */
    public Pair<Matrix, Double> gradientOnWeights(Neuron neuron, Matrix x, Matrix y) {
        int m = neuron.getWeights().size()[0];
        Matrix gradient = new Matrix(m, 1);
        for (int i = 0; i < m; i++) {
            gradient.set(i, 0, functionDerivativeOnWi(neuron, x, y, i));
        }

        Double gradientOnBias = functionDerivativeOnWi(neuron, x, y, -1);

        return new Pair<>(gradient, gradientOnBias);
    }

}
