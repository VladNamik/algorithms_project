package com.vladnamik.developer.machine.learning.neuralnetwork.objfunc;


import com.vladnamik.developer.datastructures.Matrix;

import java.util.Arrays;

@SuppressWarnings("unused")
public class CrossEntopyForBinaryFunction extends ObjectiveFunction {

    /**
     * Предполагается, что {@code receivedAnswer} находится в промежутке от 0 до 1
     * и {@code correctAnswer} равен 1 либо 0.
     *
     * @param receivedAnswer полученный ответ.
     * @param correctAnswer  предполагаемый ответ.
     * @return значение функции потерь.
     */
    @Override
    public double costFunction(double receivedAnswer, double correctAnswer) {
        if (receivedAnswer == 0.0) {
            receivedAnswer = 1e-100;
        }
        if (receivedAnswer == 1.0) {
            receivedAnswer = 1 - 1e100;
        }
        return -(correctAnswer * Math.log(receivedAnswer) + (1 - correctAnswer) * Math.log(1 - receivedAnswer));
    }

    @Override
    public double costFunctionDerivative(double receivedAnswer, double correctAnswer) {
        if (receivedAnswer == 0.0) {
            receivedAnswer = 1e-100;
        }
        if (receivedAnswer == 1.0) {
            receivedAnswer = 1 - 1e100;
        }
        return -(correctAnswer / receivedAnswer - (1 - correctAnswer) / (1 - receivedAnswer));
    }

    @Override
    public double costFunctionSum(Matrix costFuncResults) {
        return Arrays.stream(costFuncResults.transpose().getArray()[0]).sum()
                / (costFuncResults.size()[0]);
    }
}
