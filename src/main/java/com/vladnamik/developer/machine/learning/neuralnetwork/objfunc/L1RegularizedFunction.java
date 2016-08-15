package com.vladnamik.developer.machine.learning.neuralnetwork.objfunc;


import com.vladnamik.developer.datastructures.Matrix;
import com.vladnamik.developer.machine.learning.neuralnetwork.Neuron;

import java.util.Arrays;

@SuppressWarnings("unused")
public class L1RegularizedFunction extends ObjectiveFunction {

    private ObjectiveFunction objectiveFunction;

    /**
     * Гиперпараметр функции.
     */
    private double lambda = 1e-3;

    public L1RegularizedFunction(ObjectiveFunction objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public L1RegularizedFunction(ObjectiveFunction objectiveFunction, double lambda) {
        this.objectiveFunction = objectiveFunction;
        this.lambda = lambda;
    }

    @Override
    public double function(Neuron neuron, Matrix x, Matrix y) {
        double objectiveFuncResult = super.function(neuron, x, y);
        return objectiveFuncResult + lambda *
                Arrays.stream(neuron.getWeights().transpose().apply(Math::abs)
                        .getArray()[0]).sum();
    }

    @Override
    public double functionDerivativeOnWi(Neuron neuron, Matrix x, Matrix y, int wNumber) {
        double objectiveFuncDerivativeResult = super.functionDerivativeOnWi(neuron, x, y, wNumber);
        if (wNumber == -1) {
            return objectiveFuncDerivativeResult + lambda * Math.signum(neuron.getBias());
        }
        return objectiveFuncDerivativeResult + lambda * Math.signum(neuron.getWeights().get(wNumber, 0));
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
