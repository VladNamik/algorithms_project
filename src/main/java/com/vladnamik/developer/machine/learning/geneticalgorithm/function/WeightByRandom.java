package com.vladnamik.developer.machine.learning.geneticalgorithm.function;


import com.vladnamik.developer.machine.learning.geneticalgorithm.City;
import com.vladnamik.developer.machine.learning.geneticalgorithm.RandomNumber;

/**
 * Определяем вес рандомно
 */
public class WeightByRandom implements WeightFunction {
    @Override
    public double getWeight(City city1, City city2) {
        return RandomNumber.random.nextDouble() * Integer.MAX_VALUE;
    }
}
