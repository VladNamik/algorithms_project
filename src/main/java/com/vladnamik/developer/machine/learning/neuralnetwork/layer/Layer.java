package com.vladnamik.developer.machine.learning.neuralnetwork.layer;


import com.vladnamik.developer.datastructures.Matrix;
import com.vladnamik.developer.machine.learning.neuralnetwork.Neuron;

import java.util.List;

@SuppressWarnings("unused")
public abstract class Layer {
    /**
     * Матрица (n, n_l), где n — количество входных векторов (количество примеров),
     * n_l — размер одного вектора — количество нейронов на данном слое.
     */
    private Matrix lastResults;

    /**
     * Вектор ошибок слоя.
     * Заполняется после вызова метода {@code getErrors(Matrix inputs, Matrix answers, Layer nextLayer)}.
     * Размер (n, n_l), где n — количество примеров, n_l — количество нейронов слоя.
     */
    protected Matrix errors;

    /**
     * Совершает один проход через каждый нейрон в слое.
     *
     * @param singleInput вектор (1, n_(l-1)) результатов предыдущего слоя,
     *                    где n_(l-1) — количество нейронов на предыдущем слое.
     * @return вектор (1, n_l), где n_l — количество нейронов на данном слое.
     */
    public abstract Matrix getResult(Matrix singleInput);

    /**
     * Совершает по одному проходу через каждый нейрон в слое.
     *
     * @param inputs матрица (n, n_(l-1)) результатов предыдущего слоя,
     *               где n — количество входных векторов (количество примеров),
     *               n_(l-1) — количество нейронов на предыдущем слое.
     * @return матрицу (n, n_l), где n — количество входных векторов (количество примеров),
     * n_l — размер одного вектора — количество нейронов на данном слое.
     */
    public Matrix getResults(Matrix inputs) {
        int n = inputs.size()[0];
        int neuronsQuantity = getNeuronsQuantity();
        int neuronsQuantityFromPreviousLayer = inputs.size()[1];
        Matrix resultMatrix = new Matrix(n, neuronsQuantity);

        Matrix oneResultMatrix;
        for (int i = 0; i < n; i++) {
            oneResultMatrix = getResult(inputs.getMatrix(i, i, 0, neuronsQuantityFromPreviousLayer - 1));
            resultMatrix.setMatrix(i, i, 0, neuronsQuantity - 1, oneResultMatrix);
        }

        lastResults = resultMatrix;
        return resultMatrix;
    }

    /**
     * @return количество нейронов в слое.
     */
    public abstract int getNeuronsQuantity();

    /**
     * Рассчитывает ошибку слоя.
     *
     * @param inputs    матрица (n, n_(l-1)), где n — количество примеров,
     *                  n_(l-1) — количество нейронов на предыдущем слое
     *                  (количество входов для каждого нейрона).
     * @param answers   вектор правильных ответов (n, 1).
     * @param nextLayer следующий слой; {@code null}, если нет.
     * @return матрица ошибок слоя размером (n, n_l),
     * где n_l — количество нейронов в слое.
     */
    public abstract Matrix getErrors(Matrix inputs, Matrix answers, Layer nextLayer);

    /**
     * Предполагается, что до этого был вызван метод {@code getErrors(Matrix inputs, Matrix answers, Layer nextLayer)}.
     *
     * @return матрица ошибок слоя.
     */
    public Matrix getErrors() {
        return errors;
    }

    /**
     * @return матрицу весов размера (n_l, m), где n_l — количество нейронов на данном слое,
     * m — количество входов в одном нейроне (количество весов).
     */
    public abstract Matrix getWeights();

    /**
     * Расчитывает производную целевой функции по смещению для слоя.
     * Предполагается, что до этого был вызван метод {@code getErrors(Matrix inputs, Matrix answers, Layer nextLayer)}.
     *
     * @return вектор (n_l, 1) производных, где n_l — количество нейронов в слое.
     */
    public Matrix objectiveFunctionDerivationOnBias() {
        return errors.mean().transpose();
    }


    /**
     * Расчитывает производную целевой функции по весу для слоя.
     * Предполагается, что до этого был вызван метод {@code getErrors(Matrix inputs, Matrix answers, Layer nextLayer)}.
     *
     * @param inputs матрица (n, n_(l-1)), где n — количество примеров,
     *               n_(l-1) — количество нейронов на предыдущем слое
     *               (количество входов для каждого нейрона).
     * @return матрица (n_l, m) производных, где n_l — количество нейронов в слое,
     * m (=n_(l-1))— количество входов в одном нейроне.
     */
    public Matrix objectiveFunctionDerivationOnWeight(Matrix inputs) {
        // a_(l-1)*errors_l
        // ((n, n_(l-1)).t * (n, n_l)).t = (n_l, n_(l-1))

        return inputs.transpose().times(getErrors()).transpose();
    }

    public Matrix getLastResults() {
        return lastResults;
    }

    public abstract List<Neuron> getNeurons();
}
