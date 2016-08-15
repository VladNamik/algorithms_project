package com.vladnamik.developer.concurrency.dif.equation;

import com.vladnamik.developer.concurrency.linear.equation.LinearEquationSolveMethod;
import com.vladnamik.developer.concurrency.linear.equation.TridiagonalMatrixParallelAlgorithm;
import com.vladnamik.developer.datastructures.Matrix;
import mpi.MPI;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Решение Дифференциального уравнения явным и неявным методами используя MPJ
 */
@SuppressWarnings("unused")
public class DifParallelSolving {
    public static final double A = 1e15d;
    public static final double a = 0.15d;
    public static final double Lambda = -1e-3d;

    public double x_0 = 0;
    public double x_n = 1;
    public double t_0 = 0;
    public double t_n = 1;
    public int n = 7;
    public int k = 7;
    public int s = 10;
    public double h = (x_n - x_0) / n;
    public double tau = (t_n - t_0) / k;
    int root;
    int currentProcessId;
    int processQuantity;

    //x[0] — x; x[1] — t.
    public final
    Function<double[], Double> checkFunction =
            (x) -> Math.pow(A * Math.exp(-2 * a * Lambda * x[1]) + Lambda * Math.pow(x[0], 2), 0.5);

    //w_i[0] — w_(i-1, j+1); w_i[1] — w_(i, j+1); w_i[2] — w_(i+1, j+1); w_i[3] — w_(i, j); w_i[4] — x; w_i[5] — t.
    public final Function<double[], Double> f_i =
            (w_i) -> a * (
                    2 * w_i[1] * Math.pow((w_i[2] - w_i[0]) / (2 * h), 2) -
                            (2 * Math.pow(w_i[1], 2) / w_i[4]) * (w_i[2] - w_i[0]) / (2 * h) +
                            Math.pow(w_i[1], 2) * (w_i[0] - 2 * w_i[1] + w_i[2]) / Math.pow(h, 2)
            ) - (w_i[1] - w_i[3]) / tau;

    //w_i[0] — w_(i-1, j+1); w_i[1] — w_(i, j+1); w_i[2] — w_(i+1, j+1); w_i[3] — w_(i, j); w_i[4] — x; w_i[5] — t.
    public final Function<double[], Double> f_i_DerivativeOn_w_i0 =
            (w_i) -> a * w_i[1] * (w_i[2] - w_i[0]) / Math.pow(h, 2) -
                    a * Math.pow(w_i[1], 2) * (1 / w_i[4] + 1 / h) / h;

    //w_i[0] — w_(i-1, j+1); w_i[1] — w_(i, j+1); w_i[2] — w_(i+1, j+1); w_i[3] — w_(i, j); w_i[4] — x; w_i[5] — t.
    public final Function<double[], Double> f_i_DerivativeOn_w_i1 =
            (w_i) -> 1 / tau + 2 * a * w_i[1] * (w_i[2] - w_i[0]) / (w_i[4] * h) -
                    a * Math.pow(w_i[2] - w_i[0], 2) / (2 * Math.pow(h, 2)) -
                    a * (2 * w_i[2] * w_i[1] - 6 * Math.pow(w_i[1], 2) + 2 * w_i[1] * w_i[0])
                            / Math.pow(h, 2);

    //    //w_i[0] — w_(i-1, j+1); w_i[1] — w_(i, j+1); w_i[2] — w_(i+1, j+1); w_i[3] — w_(i, j); w_i[4] — x; w_i[5] — t.
    public final Function<double[], Double> f_i_DerivativeOn_w_i2 =
            (w_i) -> a * w_i[1] * (w_i[0] - w_i[2]) / Math.pow(h, 2) +
                    a * Math.pow(w_i[1], 2) * (1 / w_i[4] - 1 / h) / h;

    //Явный метод. Нахождение w_(i, j+1)
    //w_i[0] — w_(i-1, j+1); w_i[1] — w_(i, j+1); w_i[2] — w_(i+1, j+1); w_i[3] — w_(i, j); w_i[4] — x; w_i[5] — t.
    public final Function<double[], Double> simpleFunction_w_i =
            (w_i) -> tau * a * (
                    2 * w_i[1] * Math.pow((w_i[2] - w_i[0]) / (2 * h), 2) -
                            (2 * Math.pow(w_i[1], 2) / w_i[4]) * (w_i[2] - w_i[0]) / (2 * h) +
                            Math.pow(w_i[1], 2) * (w_i[0] - 2 * w_i[1] + w_i[2]) / Math.pow(h, 2)
            ) + w_i[3];


    LinearEquationSolveMethod linearEquationSolveMethod;

    public DifParallelSolving(int root, int currentProcessId, int processQuantity) {
        this.root = root;
        this.currentProcessId = currentProcessId;
        this.processQuantity = processQuantity;

        linearEquationSolveMethod = new TridiagonalMatrixParallelAlgorithm(root, currentProcessId, processQuantity);
    }

    public Matrix getResultMatrix() {
        Matrix resultMatrix = new Matrix(k + 1, n + 1);

        resultMatrix.setMatrix(0, 0, 0, n, new Matrix(getFirstRow(), 1));
        for (int j = 1; j <= k; j++) {
            resultMatrix.setMatrix(j, j, 0, n, new Matrix(getRow(resultMatrix.getArray()[j - 1], j), 1));
        }

        return resultMatrix;
    }

    public Matrix getRealResultMatrix() {
        Matrix realResultMatrix = new Matrix(k + 1, n + 1);
        for (int j = 0; j <= k; j++) {
            for (int i = 0; i <= n; i++) {
                realResultMatrix.set(j, i, checkFunction.apply(new double[]{x_0 + i * h, t_0 + j * tau}));
            }
        }
        return realResultMatrix;
    }

    public double[] getFirstRow() {
        double[] firstRow = new double[n + 1];
        int rowPartBegin = currentProcessId * (n + 1) / processQuantity;
        int rowPartEnd = (currentProcessId + 1) * (n + 1) / processQuantity;
        int rowPartLength = rowPartEnd - rowPartBegin;
        double[] firstRowPart = new double[rowPartLength];
        for (int i = rowPartBegin; i < rowPartEnd; i++) {
            firstRowPart[i - rowPartBegin] = checkFunction.apply(new double[]{x_0 + i * h, t_0});
        }
        MPI.COMM_WORLD.Gather(firstRowPart, 0, rowPartLength, MPI.DOUBLE, firstRow, 0, rowPartLength, MPI.DOUBLE, root);
        MPI.COMM_WORLD.Bcast(firstRow, 0, n + 1, MPI.DOUBLE, root);
        return firstRow;
    }

    public double getFirstW(double t) {
        return checkFunction.apply(new double[]{x_0, t});
    }

    public double getLastW(double t) {
        return checkFunction.apply(new double[]{x_n, t});
    }

    public double[] getRow(double[] prevRow, int rowNum) {
        Matrix bufRow = new Matrix(prevRow, 1).transpose();

        for (int i = 0; i < s; i++) {
            bufRow = oneNewtonMethodIteration(bufRow, rowNum, prevRow);
        }

        return bufRow.transpose().getArray()[0];
    }

    public Matrix oneNewtonMethodIteration(Matrix prevResult, int rowNum, double[] prevRow) {

        Matrix aMatrix = new Matrix(n + 1, n + 1);
        Matrix fMatrix = new Matrix(n + 1, 1);

        aMatrix.set(0, 0, 1);
        fMatrix.set(0, 0, -prevResult.get(0, 0) + getFirstW(t_0 + tau * rowNum));
        aMatrix.set(n, n, 1);
        fMatrix.set(n, 0, -prevResult.get(n, 0) + getLastW(t_0 + tau * rowNum));
        for (int i = 1; i < n; i++) {
            aMatrix.set(i, i - 1, f_i_DerivativeOn_w_i0.apply(new double[]{prevResult.get(i - 1, 0),
                    prevResult.get(i, 0), prevResult.get(i + 1, 0), prevRow[i], x_0 + i * h, t_0 + (rowNum - 1) * tau}));
            aMatrix.set(i, i, f_i_DerivativeOn_w_i1.apply(new double[]{prevResult.get(i - 1, 0),
                    prevResult.get(i, 0), prevResult.get(i + 1, 0), prevRow[i], x_0 + i * h, t_0 + (rowNum - 1) * tau}));
            aMatrix.set(i, i + 1, f_i_DerivativeOn_w_i2.apply(new double[]{prevResult.get(i - 1, 0),
                    prevResult.get(i, 0), prevResult.get(i + 1, 0), prevRow[i], x_0 + i * h, t_0 + (rowNum - 1) * tau}));
            fMatrix.set(i, 0, -f_i.apply(new double[]{prevResult.get(i - 1, 0), prevResult.get(i, 0),
                    prevResult.get(i + 1, 0), prevRow[i], x_0 + i * h, t_0 + (rowNum - 1) * tau}));
        }


        Matrix wDelta = linearEquationSolveMethod.solve(aMatrix, fMatrix);
        Matrix result = new Matrix(n + 1, 1);

        for (int i = 0; i <= n; i++) {
            result.set(i, 0, prevResult.get(i, 0) + wDelta.get(i, 0));
        }

        return result;
    }

    public Matrix getSimpleResultMatrix() {
        Matrix simpleResultMatrix = new Matrix(k + 1, n + 1);
        double[] prevRow = getFirstRow();
        double[] bufRow = new double[prevRow.length];
        simpleResultMatrix.setMatrix(0, 0, 0, n, new Matrix(Arrays.copyOf(prevRow, prevRow.length), 1));
        for (int j = 1; j <= k; j++) {
            bufRow[0] = getFirstW(t_0 + j * tau);
            bufRow[n] = getLastW(t_0 + j * tau);
            for (int i = 1; i < n; i++) {
                bufRow[i] = simpleFunction_w_i.apply(new double[]{prevRow[i - 1],
                        prevRow[i], prevRow[i + 1], prevRow[i], x_0 + i * h, t_0 + j * tau});
            }
            prevRow = bufRow;
            simpleResultMatrix.setMatrix(j, j, 0, n, new Matrix(Arrays.copyOf(prevRow, prevRow.length), 1));
        }
        return simpleResultMatrix;
    }

    public void saveInFileAsListPlot(List<Matrix> matrices, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)))) {
            int[] size;
            int matricesSize = matrices.size();
            for (int i = 0; i < matricesSize; i++) {
                Matrix currentMatrix = matrices.get(i);
                size = matrices.get(i).size();
                StringBuilder stringMatrix = new StringBuilder();
                stringMatrix.append('{');
                for (int j = 0; j < size[0]; j++) {
                    for (int k = 0; k < size[1]; k++) {
                        stringMatrix.append('{');
                        stringMatrix.append(t_0 + j * tau);
                        stringMatrix.append(", ");
                        stringMatrix.append(x_0 + k * h);
                        stringMatrix.append(", ");
                        stringMatrix.append(currentMatrix.get(j, k));
                        stringMatrix.append("}, ");
                    }
                }
                stringMatrix.delete(stringMatrix.length() - 2, stringMatrix.length() - 1);
                stringMatrix.append('}');
                writer.write(stringMatrix.toString());
                writer.newLine();
            }
            writer.flush();
        }
    }


}
