package com.vladnamik.developer.machine.learning.geneticalgorithm.refresh;


import com.vladnamik.developer.machine.learning.geneticalgorithm.Population;

/**
 * Удаляет только по достижению верхней гранцы, потом проводится refresh по оставшимся
 */
public class RefreshWithoutDelete implements Refresh {
    private int upperBound;
    private Refresh refresh;

    public RefreshWithoutDelete(int upperBound, Refresh refresh) {
        this.upperBound = upperBound;
        this.refresh = refresh;
    }

    @Override
    public Population refresh(Population population) {
        if (population.getAll().size() < upperBound)
            return population;
        else
            refresh.refresh(population);
        return population;
    }
}
