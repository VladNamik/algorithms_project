package com.vladnamik.developer.concurrency.linear.equation;


import com.vladnamik.developer.datastructures.Matrix;

/**
 * Метод прогонки.
 */
public class TridiagonalMatrixAlgorithm implements LinearEquationSolveMethod {

    @Override
    public Matrix solve(Matrix aMatrix, Matrix bMatrix) {
        Matrix coefficientsForwardSweep = forwardSweep(aMatrix, bMatrix);
        return backwardSweep(coefficientsForwardSweep);
    }

    /**
     * Прямой ход метода прогонки.
     *
     * @param aMatrix матрица (n, n)
     * @param bMatrix матрица (n, 1)
     * @return матрицу (n, 2) коэфициентов q, r метода прогонки
     * Уравнение после преобразования:
     * 1 -q_1 0 ... 0     r_1
     * 0 1 -q_2 0 ...     r_2
     * ... ... ... ..   = ...
     * ... ... ...  1     r_n
     */
    public Matrix forwardSweep(Matrix aMatrix, Matrix bMatrix) {
        int n = aMatrix.size()[0];
        Matrix resultCoef = new Matrix(n, 2);
        // a_i * x_(i-1) + b_i * x_i + c_i * x_(i + 1) = d_i;
        double a;
        double b = aMatrix.get(0, 0);
        double c = aMatrix.get(0, 1);
        double d = bMatrix.get(0, 0);

        // y_i = b_i + a_i * q_(i - 1)
        double y = b;
        // q_i = - c_i / y_i
        double q = -c / y;
        // r_i = (d_i - a_i * r_(i-1)) / y_i
        double r = d / y;

        resultCoef.set(0, 0, q);
        resultCoef.set(0, 1, r);

        for (int i = 1; i < n - 1; i++) {
            a = aMatrix.get(i, i - 1);
            b = aMatrix.get(i, i);
            c = aMatrix.get(i, i + 1);
            d = bMatrix.get(i, 0);

            y = b + a * q;
            q = -c / y;
            r = (d - a * r) / y;

            resultCoef.set(i, 0, q);
            resultCoef.set(i, 1, r);
        }

        a = aMatrix.get(n - 1, n - 2);
        b = aMatrix.get(n - 1, n - 1);
        c = 0;
        d = bMatrix.get(n - 1, 0);

        y = b + a * q;
        q = -c / y;
        r = (d - a * r) / y;

        resultCoef.set(n - 1, 0, q);
        resultCoef.set(n - 1, 1, r);

        return resultCoef;
    }

    /**
     * Обратный ход метода прогонки.
     *
     * @param coefMatrix матрица коэффициентов, полученная после
     *                   прямого хода, размером (n, 2).
     * @return матрица (n, 1)
     */
    public Matrix backwardSweep(Matrix coefMatrix) {
        int n = coefMatrix.size()[0];
        Matrix resultVector = new Matrix(n, 1);
        resultVector.set(n - 1, 0, coefMatrix.get(n - 1, 1));
        for (int i = n - 2; i >= 0; i--) {
            //x_i = q_i * x_(i+1) + r_i
            resultVector.set(i, 0, coefMatrix.get(i, 0) * resultVector.get(i + 1, 0) + coefMatrix.get(i, 1));
        }

        return resultVector;
    }

}
