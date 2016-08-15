package com.vladnamik.developer.concurrency.linear.equation;

import com.vladnamik.developer.datastructures.Matrix;
import javafx.util.Pair;
import mpi.MPI;

/**
 * Параллельный метод прогонки.
 */
public class TridiagonalMatrixParallelAlgorithm implements LinearEquationSolveMethod {
    private int root;
    private int currentProcessId;
    private int processQuantity;

    public TridiagonalMatrixParallelAlgorithm(int root, int currentProcessId, int processQuantity) {
        this.root = root;
        this.currentProcessId = currentProcessId;
        this.processQuantity = processQuantity;
    }

    @Override
    public Matrix solve(Matrix aMatrix, Matrix bMatrix) {
        int n = aMatrix.size()[0];
        int fromRow = currentProcessId * n / processQuantity;// включительно
        int toRow = (currentProcessId + 1) * n / processQuantity;// не включительно
        forwardSweep(aMatrix, bMatrix, fromRow, toRow);

        Pair<double[], double[]> newMatrices = unionFromAllProcesses(aMatrix, bMatrix, fromRow, toRow);
        aMatrix = new Matrix(Matrix.toTwoDimensionArray(newMatrices.getKey(), n));
        bMatrix = new Matrix(newMatrices.getValue(), 1).transpose();

        backwardSweep(aMatrix, bMatrix, fromRow, toRow);

        if (fromRow == 0) {
            fromRow++;
            toRow++;
        }
        fromRow--;
        toRow--;
        newMatrices = unionFromAllProcesses(aMatrix, bMatrix, fromRow, toRow);

        //чиним матрицы
        Matrix bufMatrixA = new Matrix(Matrix.toTwoDimensionArray(newMatrices.getKey(), n));
        Matrix bufMatrixB = new Matrix(newMatrices.getValue(), 1).transpose();
        for (int i = n / processQuantity - 1; i < n - 1; i++) {
            bufMatrixA.setMatrix(i, i, 0, n - 1, bufMatrixA.getMatrix(i + 1, i + 1, 0, n - 1));
            bufMatrixB.set(i, 0, bufMatrixB.get(i + 1, 0));
        }
        bufMatrixA.setMatrix(n - 1, n - 1, 0, n - 1, aMatrix.getMatrix(n - 1, n - 1, 0, n - 1));
        bufMatrixB.set(n - 1, 0, bMatrix.get(n - 1, 0));
        aMatrix = bufMatrixA;
        bMatrix = bufMatrixB;

        return solveResultSystem(aMatrix, bMatrix);
    }

    public void forwardSweep(Matrix aMatrix, Matrix bMatrix, int fromRow, int toRow) {
        for (int i = fromRow + 1; i < toRow; i++) {
            double coef = aMatrix.get(i, i - 1) / aMatrix.get(i - 1, i - 1);
            aMatrix.set(i, i - 1, 0);
            aMatrix.set(i, i, aMatrix.get(i, i) - coef * aMatrix.get(i - 1, i));
            bMatrix.set(i, 0, bMatrix.get(i, 0) - coef * bMatrix.get(i - 1, 0));
            if (fromRow != 0) {
                aMatrix.set(i, fromRow - 1, -coef * aMatrix.get(i - 1, fromRow - 1));
            }
        }
    }

    public void backwardSweep(Matrix aMatrix, Matrix bMatrix, int fromRow, int toRow) {
        if (fromRow == 0) {
            for (int i = toRow - 3; i >= 0; i--) {
                double coef = aMatrix.get(i, i + 1) / aMatrix.get(i + 1, i + 1);
                aMatrix.set(i, i + 1, 0);
                aMatrix.set(i, toRow - 1, -coef * aMatrix.get(i + 1, toRow - 1));
                bMatrix.set(i, 0, bMatrix.get(i, 0) - coef * bMatrix.get(i + 1, 0));
            }
        } else {
            for (int i = toRow - 3; i >= fromRow - 1; i--) {
                double coef = aMatrix.get(i, i + 1) / aMatrix.get(i + 1, i + 1);
                aMatrix.set(i, i + 1, 0);
                aMatrix.set(i, toRow - 1, -coef * aMatrix.get(i + 1, toRow - 1));
                aMatrix.set(i, fromRow - 1, aMatrix.get(i, fromRow - 1) - coef * aMatrix.get(i + 1, fromRow - 1));
                bMatrix.set(i, 0, bMatrix.get(i, 0) - coef * bMatrix.get(i + 1, 0));
            }
        }
    }

    private Pair<double[], double[]> unionFromAllProcesses(Matrix aMatrix, Matrix bMatrix, int fromRow, int toRow) {
        int n = aMatrix.size()[0];

        double[] oneProcessOneDimA = Matrix.toOneDimensionArray(aMatrix.getMatrix(fromRow, toRow - 1, 0, n - 1).getArray());
        double[] oneDimA = new double[n * n];
        MPI.COMM_WORLD.Gather(oneProcessOneDimA, 0, n * (toRow - fromRow),
                MPI.DOUBLE, oneDimA, 0, n * (toRow - fromRow), MPI.DOUBLE, root);
        MPI.COMM_WORLD.Bcast(oneDimA, 0, n * n, MPI.DOUBLE, root);

        double[] oneDimB = new double[n];
        double[] oneProcessOneDimB = bMatrix.transpose().getMatrix(0, 0, fromRow, toRow - 1).getArray()[0];
        MPI.COMM_WORLD.Gather(oneProcessOneDimB, 0, toRow - fromRow, MPI.DOUBLE, oneDimB,
                0, toRow - fromRow, MPI.DOUBLE, root);
        MPI.COMM_WORLD.Bcast(oneDimB, 0, n, MPI.DOUBLE, root);

        return new Pair<>(oneDimA, oneDimB);
    }

    private Matrix solveResultSystem(Matrix aMatrix, Matrix bMatrix) {
        int n = aMatrix.size()[0];
        Matrix resultVector = new Matrix(n, 1);

        //заполнаяем трёхдиагональную подматрицу
        int subTridiagonalMatrixSize = processQuantity;
        Matrix subTridiagonalMatrixA = new Matrix(subTridiagonalMatrixSize, subTridiagonalMatrixSize);
        Matrix subTridiagonalMatrixB = new Matrix(subTridiagonalMatrixSize, 1);
        int blockSize = n / processQuantity;


        int subRowNumber = blockSize - 1;
        double subAParameter;
        double subBParameter = aMatrix.get(subRowNumber, subRowNumber);
        double subCParameter = aMatrix.get(subRowNumber, subRowNumber + blockSize);
        double subDParameter = bMatrix.get(subRowNumber, 0);
        subTridiagonalMatrixA.set(0, 0, subBParameter);
        subTridiagonalMatrixA.set(0, 1, subCParameter);
        subTridiagonalMatrixB.set(0, 0, subDParameter);

        for (int i = 1; i < subTridiagonalMatrixSize - 1; i++) {
            subRowNumber += blockSize;
            subAParameter = aMatrix.get(subRowNumber, subRowNumber - blockSize);
            subBParameter = aMatrix.get(subRowNumber, subRowNumber);
            subCParameter = aMatrix.get(subRowNumber, subRowNumber + blockSize);
            subDParameter = bMatrix.get(subRowNumber, 0);

            subTridiagonalMatrixA.set(i, i - 1, subAParameter);
            subTridiagonalMatrixA.set(i, i, subBParameter);
            subTridiagonalMatrixA.set(i, i + 1, subCParameter);
            subTridiagonalMatrixB.set(i, 0, subDParameter);
        }

        subRowNumber += blockSize;
        subAParameter = aMatrix.get(subRowNumber, subRowNumber - blockSize);
        subBParameter = aMatrix.get(subRowNumber, subRowNumber);
        subDParameter = bMatrix.get(subRowNumber, 0);

        subTridiagonalMatrixA.set(subTridiagonalMatrixSize - 1, subTridiagonalMatrixSize - 2, subAParameter);
        subTridiagonalMatrixA.set(subTridiagonalMatrixSize - 1, subTridiagonalMatrixSize - 1, subBParameter);
        subTridiagonalMatrixB.set(subTridiagonalMatrixSize - 1, 0, subDParameter);

        Matrix subTridiagonalMatrixResult = new TridiagonalMatrixAlgorithm().solve(subTridiagonalMatrixA, subTridiagonalMatrixB);

        subRowNumber = blockSize - 1;
        resultVector.set(subRowNumber, 0, subTridiagonalMatrixResult.get(0, 0));
        for (int i = 0; i < subRowNumber; i++) {
            resultVector.set(i, 0, (bMatrix.get(i, 0) - aMatrix.get(i, subRowNumber)
                    * resultVector.get(subRowNumber, 0)) / aMatrix.get(i, i));
        }

        for (int i = 1; i < subTridiagonalMatrixSize; i++) {
            subRowNumber += blockSize;
            resultVector.set(subRowNumber, 0, subTridiagonalMatrixResult.get(i, 0));

            for (int j = subRowNumber - blockSize + 1; j < subRowNumber; j++) {
                resultVector.set(j, 0, (bMatrix.get(j, 0) - aMatrix.get(j, subRowNumber) * resultVector.get(subRowNumber, 0)
                        - aMatrix.get(j, subRowNumber - blockSize) * resultVector.get(subRowNumber - blockSize, 0))
                        / aMatrix.get(j, j));
            }

        }

        return resultVector;
    }


}
