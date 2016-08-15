package com.vladnamik.developer.machine.learning.neuralnetwork.objfunc;

import com.vladnamik.developer.datastructures.Matrix;
import com.vladnamik.developer.machine.learning.neuralnetwork.Neuron;

import java.util.Arrays;

@SuppressWarnings("unused")
public class L2RegularizedFunction extends ObjectiveFunction {

    private ObjectiveFunction objectiveFunction;

    /**
     * Гиперпараметр функции.
     */
    private double lambda = 1e-3;

    public L2RegularizedFunction(ObjectiveFunction objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public L2RegularizedFunction(ObjectiveFunction objectiveFunction, double lambda) {
        this.objectiveFunction = objectiveFunction;
        this.lambda = lambda;
    }

    @Override
    public double function(Neuron neuron, Matrix x, Matrix y) {
        double objectiveFuncResult = super.function(neuron, x, y);
        return objectiveFuncResult + lambda / 2
                * Arrays.stream(neuron.getWeights().transpose().apply(element -> Math.pow(element, 2))
                .getArray()[0]).sum();
    }

    @Override
    public double functionDerivativeOnWi(Neuron neuron, Matrix x, Matrix y, int wNumber) {
        double objectiveFuncDerivativeResult = super.functionDerivativeOnWi(neuron, x, y, wNumber);
        if (wNumber == -1) {
            return objectiveFuncDerivativeResult + lambda * neuron.getBias();
        }
        return objectiveFuncDerivativeResult + lambda * neuron.getWeights().get(wNumber, 0);
    }

    @Override
    public double costFunction(double receivedAnswer, double correctAnswer) {
        return objectiveFunction.costFunction(receivedAnswer, correctAnswer);
    }

    @Override
    public double costFunctionDerivative(double receivedAnswer, double correctAnswer) {
        return objectiveFunction.costFunctionDerivative(receivedAnswer, correctAnswer);
    }

    @Override
    public double costFunctionSum(Matrix costFuncResults) {
        return objectiveFunction.costFunctionSum(costFuncResults);
    }


}
