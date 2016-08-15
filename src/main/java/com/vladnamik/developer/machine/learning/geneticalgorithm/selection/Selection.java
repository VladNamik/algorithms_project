package com.vladnamik.developer.machine.learning.geneticalgorithm.selection;


import com.vladnamik.developer.machine.learning.geneticalgorithm.Population;
import com.vladnamik.developer.machine.learning.geneticalgorithm.crossing.Crossing;

/**
 * Способ выбора из популяции для последующего скрещевания
 */
public interface Selection {
    Population selection(Population population, Crossing crossing);
}
