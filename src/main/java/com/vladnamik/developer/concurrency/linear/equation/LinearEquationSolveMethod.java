package com.vladnamik.developer.concurrency.linear.equation;


import com.vladnamik.developer.datastructures.Matrix;

public interface LinearEquationSolveMethod {

    /**
     * Решение СЛАУ вида a * x = b.
     *
     * @param a матрица уравнений размера (m, n).
     * @param b вектор результата размера (m, 1).
     * @return вектор x размера (n, 1).
     */
    Matrix solve(Matrix a, Matrix b);
}
