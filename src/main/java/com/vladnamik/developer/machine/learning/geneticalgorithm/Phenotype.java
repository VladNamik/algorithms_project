package com.vladnamik.developer.machine.learning.geneticalgorithm;

import java.util.*;

/**
 * Один организм — одно возможное решение задачи
 * Совокупность всех точек-городов в определённой последовательности
 */
public class Phenotype {
    private List<City> phenotype;
    private Roads roads;
    private boolean isAlive = true;
    private double fitness;

    public static final Comparator<Phenotype> comparator = (x, y) -> Double.compare(x.fitness, y.fitness);

    public Phenotype(Roads roads) {
        this.roads = roads;
        phenotype = new ArrayList<>(roads.getQuantity());

        Random random = RandomNumber.random;
        List<City> cities = new LinkedList<>(Arrays.asList(roads.getCities()));
        while (!cities.isEmpty()) {
            phenotype.add(Math.abs(phenotype.size() == 0 ? 0 : random.nextInt() % phenotype.size()), cities.remove(Math.abs(random.nextInt() % cities.size())));
        }
        recountFitness();
    }

    public Phenotype(Phenotype phenotype) {
        Collections.copy(phenotype.getList(), this.phenotype);
        this.roads = phenotype.getRoads();
        recountFitness();
    }

    public Phenotype(Roads roads, List<City> phenotype) {
        this.roads = roads;
        this.phenotype = phenotype;
        recountFitness();
    }

    private void recountFitness() {
        double fitness = 0;

        if (phenotype.size() > 0)
            fitness += roads.getWeight(phenotype.get(0), phenotype.get(phenotype.size() - 1));
        for (int i = 1; i < phenotype.size(); i++)
            fitness += roads.getWeight(phenotype.get(i - 1), phenotype.get(i));
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public List<City> getList() {
        return phenotype;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public Roads getRoads() {
        return roads;
    }


}
