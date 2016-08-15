package com.vladnamik.developer.machine.learning.neuralnetwork.objfunc;


import com.vladnamik.developer.datastructures.Matrix;

import java.util.Arrays;

public class QuadraticObjectiveFunction extends ObjectiveFunction {

    public double costFunction(double receivedAnswer, double correctAnswer) {
        return Math.pow(receivedAnswer - correctAnswer, 2);
    }

    public double costFunctionDerivative(double receivedAnswer, double correctAnswer) {
        return 2 * (receivedAnswer - correctAnswer);
    }

    /**
     * @param costFuncResults вектор значений функции потерь или её произодной
     *                        ({@code costFunction} или {@code costFunctionDerivative})
     *                        (n, 1),
     *                        усреднённое по количеству примеров (n).
     * @return усреднённую по всем примерам сумму
     */
    public double costFunctionSum(Matrix costFuncResults) {
        return Arrays.stream(costFuncResults.transpose().getArray()[0]).sum()
                / (2 * costFuncResults.size()[0]);
    }


}
