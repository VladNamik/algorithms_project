package com.vladnamik.developer.machine.learning.geneticalgorithm.function;


import com.vladnamik.developer.machine.learning.geneticalgorithm.City;

/**
 * Функция определения веса
 */
public interface WeightFunction {
    double getWeight(City city1, City city2);
}
