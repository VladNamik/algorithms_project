package com.vladnamik.developer.datastructures;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Matrix {
    double[][] array;

    public Matrix(double[][] array) {
        this.array = array;
    }

    public Matrix(double[] oneDimArray, int otherDim) {
        array = new double[otherDim][oneDimArray.length];
        for (int i = 0; i < otherDim; i++) {
            array[i] = Arrays.copyOf(oneDimArray, oneDimArray.length);
        }
    }

    public Matrix(int rowQuantity, int columnQuantity) {
        array = new double[rowQuantity][columnQuantity];
    }

    public void set(int i, int j, double number) {
        array[i][j] = number;
    }

    public double get(int i, int j) {
        return array[i][j];
    }

    public Matrix getMatrix(int i1, int i2, int j1, int j2) {
        double[][] newArray = new double[i2 - i1 + 1][j2 - j1 + 1];
        for (int i = i1; i <= i2; i++) {
            for (int j = j1; j <= j2; j++) {
                newArray[i][j] = array[i][j];
            }
        }
        return new Matrix(newArray);
    }

    public void setMatrix(int i1, int i2, int j1, int j2, Matrix newMatrix) {
        for (int i = i1; i <= i2; i++) {
            for (int j = j1; j <= j2; j++) {
                set(i, j, newMatrix.get(i, j));
            }
        }
    }

    public double[][] getArray() {
        return array;
    }

    public Matrix copy() {
        return new Matrix(Arrays.copyOf(array, array.length));
    }

    public Matrix transpose() {
        double[][] newArray = new double[array[0].length][array.length];
        for (int i = 0; i < array[0].length; i++) {
            for (int j = 0; j < array.length; j++) {
                newArray[i][j] = array[j][i];
            }
        }
        return new Matrix(newArray);
    }

    /**
     * Применяет функцию к каждому элементу матрицы.
     *
     * @param function функция double -> double.
     * @return матрица, полученная после применения функции.
     */
    public Matrix apply(Function<Double, Double> function) {
        int[] size = this.size();
        double funcResult;
        Matrix resultMatrix = new Matrix(size[0], size[1]);
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                funcResult = function.apply(this.get(i, j));
                resultMatrix.set(i, j, funcResult);
            }
        }
        return resultMatrix;
    }

    public int[] size() {
        return new int[]{this.getArray().length, this.getArray()[0].length};
    }

    /**
     * Multiply a matrix by a scalar, C = s*A.
     *
     * @param s scalar
     * @return s*A
     */
    public Matrix times(double s) {
        return this.apply((x) -> x * s);
    }

    /**
     * Linear algebraic matrix multiplication, A * B.
     *
     * @param bMatrix another matrix
     * @return Matrix product, A * B
     */
    public Matrix times(Matrix bMatrix) {
        Matrix resultMatrix = new Matrix(this.size()[0], bMatrix.size()[1]);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < bMatrix.array[0].length; j++) {
                double newElement = 0d;
                for (int k = 0; k < array[0].length; k++) {
                    newElement += array[i][k] * bMatrix.array[k][j];
                }
                resultMatrix.set(i, j, newElement);
            }
        }
        return resultMatrix;
    }

    /**
     * Element-by-element multiplication, C = A.*B
     *
     * @param bMatrix another matrix
     * @return A.*B
     */
    public Matrix arrayTimes(Matrix bMatrix) {
        Matrix resultMatrix = new Matrix(this.size()[0], this.size()[1]);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                resultMatrix.set(i, j, this.get(i, j) * bMatrix.get(i, j));
            }
        }
        return resultMatrix;
    }

    /**
     * C = A - B.
     *
     * @param bMatrix another matrix
     * @return A - B
     */
    public Matrix minus(Matrix bMatrix) {
        Matrix resultMatrix = new Matrix(this.size()[0], this.size()[1]);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                resultMatrix.set(i, j, this.get(i, j) - bMatrix.get(i, j));
            }
        }
        return resultMatrix;
    }

    // Получаем матрицу (n, k), возвращаем (1, k)
    public Matrix mean() {
        int n = size()[0];
        int k = size()[1];
        Matrix meanVector = new Matrix(1, k);
        meanVector.inZeros();

        double element;
        for (int i = 0; i < k; i++) {
            element = 0d;
            for (int j = 0; j < n; j++) {
                element += this.get(j, i);
            }
            element /= n;
            meanVector.set(0, i, element);
        }

        return meanVector;
    }

    /**
     * Обнуляет матрицу.
     */
    public void inZeros() {
        int[] size = size();
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                this.set(i, j, 0d);
            }
        }
    }

    @Override
    public String toString() {
        return toString(2);
    }

    public String toString(int accuracy) {
        StringBuilder stringBuilder = new StringBuilder();

        for (double[] array : this.getArray()) {
            stringBuilder.append('|');
            for (double element : array) {
                stringBuilder.append(String.format("\t%6." + accuracy + "f", element));
            }
            stringBuilder.append('|');
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    /**
     * Разбивает две связанные матрицы на несколько батчей пар матриц.
     */
    public static Queue<javafx.util.Pair<Matrix, Matrix>> getMiniBatches(Matrix inputX, Matrix inputY, int batchSize, Random random) {
        Matrix x = inputX.copy();
        Matrix y = inputY.copy();
        Matrix.twoMatrixRowsShuffle(x, y, random);

        int samplesQuantity = x.size()[0];
        int weightQuantity = x.size()[1];
        int batchesQuantity = samplesQuantity / batchSize;
        Queue<javafx.util.Pair<Matrix, Matrix>> batches = new LinkedList<>();

        Matrix bufferMatrixX;
        Matrix bufferMatrixY;
        for (int i = 0; i < batchesQuantity; i++) {
            bufferMatrixX = x.getMatrix(i * batchSize, (i + 1) * batchSize - 1, 0, weightQuantity - 1);
            bufferMatrixY = y.getMatrix(i * batchSize, (i + 1) * batchSize - 1, 0, 0);
            batches.add(new javafx.util.Pair<>(bufferMatrixX, bufferMatrixY));
        }

        int sizeOfLastBatch = samplesQuantity % batchSize;
        if (sizeOfLastBatch != 0) {
            bufferMatrixX = x.getMatrix(samplesQuantity - sizeOfLastBatch, samplesQuantity - 1, 0, weightQuantity - 1);
            bufferMatrixY = y.getMatrix(samplesQuantity - sizeOfLastBatch, samplesQuantity - 1, 0, 0);
            batches.add(new javafx.util.Pair<>(bufferMatrixX, bufferMatrixY));
        }

        return batches;
    }

    public static double[] toOneDimensionArray(double[][] array) {
        double[] newArray = new double[array.length * array[0].length];
        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < array[i].length; j++) {
                newArray[i * array[i].length + j] = array[i][j];
            }
        return newArray;
    }

    public static double[][] toTwoDimensionArray(double[] array, int m) {
        double[][] newArray = new double[array.length / m][m];
        for (int i = 0; i < array.length / m; i++)
            for (int j = 0; j < m; j++) {
                newArray[i][j] = array[i * m + j];
            }

        return newArray;
    }


    public static void twoMatrixRowsShuffle(Matrix firstMatrix, Matrix secondMatrix, Random random) {
        int currentRandom;
        for (int i = firstMatrix.size()[0] - 1; i >= 0; i--) {
            currentRandom = Math.abs(random.nextInt()) % firstMatrix.size()[0];
            firstMatrix.swapRows(i, currentRandom);
            secondMatrix.swapRows(i, currentRandom);
        }
    }

    public void swapRows(int firstRowNumber, int secondRowNumber) {
        Matrix firstRow = this.getMatrix(firstRowNumber, firstRowNumber, 0, this.size()[1] - 1);
        Matrix secondRow = this.getMatrix(secondRowNumber, secondRowNumber, 0, this.size()[1] - 1);
        this.setMatrix(firstRowNumber, firstRowNumber, 0, this.size()[1] - 1, secondRow);
        this.setMatrix(secondRowNumber, secondRowNumber, 0, this.size()[1] - 1, firstRow);
    }

}
