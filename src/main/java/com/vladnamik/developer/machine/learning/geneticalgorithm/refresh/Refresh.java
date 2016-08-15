package com.vladnamik.developer.machine.learning.geneticalgorithm.refresh;


import com.vladnamik.developer.machine.learning.geneticalgorithm.Population;

/**
 * Обновление популяции; удаление лишних особей
 */
public interface Refresh {
    Population refresh(Population population);
}
