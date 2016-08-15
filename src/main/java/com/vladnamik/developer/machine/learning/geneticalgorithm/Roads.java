package com.vladnamik.developer.machine.learning.geneticalgorithm;


import com.vladnamik.developer.machine.learning.geneticalgorithm.function.WeightFunction;

/**
 * Матрица, хранящая города и стоимость перехода от одного к другому
 */
public class Roads {
    private City[] cities;
    private double[][] weights;

    public Roads(City[] cities, double[][] weights) {
        this.cities = cities;
        this.weights = weights;
        if (weights.length != cities.length)
            throw new IllegalArgumentException();
    }

    public Roads(City[] cities, WeightFunction weightFunction) {
        this.cities = cities;
        //заполняем веса
        weights = new double[cities.length][cities.length];
        for (int i = 0; i < weights.length; i++)
            for (int j = 0; j < weights.length; j++)
                if (i == j)
                    weights[i][j] = 0;
                else
                    weights[i][j] = weightFunction.getWeight(cities[i], cities[j]);

        //заполняем id
        for (int i = 0; i < cities.length; i++)
            cities[i].setId(i);
    }

    public double getWeight(City city1, City city2) {
        return weights[city1.getId()][city2.getId()];
    }

    public City[] getCities() {
        return cities;
    }

    public City getCity(int id) {
        return cities[id];

    }

    public int getQuantity() {
        return cities.length;
    }
}
