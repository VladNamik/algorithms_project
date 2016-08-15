package com.vladnamik.developer.machine.learning.geneticalgorithm.function;


import com.vladnamik.developer.machine.learning.geneticalgorithm.City;

/**
 * Определяем вес по расстоянию
 */
public class WeightByDistance implements WeightFunction {
    @Override
    public double getWeight(City city1, City city2) {
        return Math.sqrt(Math.pow(city1.getX() - city2.getX(), 2.0) + Math.pow(city1.getY() - city2.getY(), 2.0));
    }
}
