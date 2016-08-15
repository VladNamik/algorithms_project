package com.vladnamik.developer.machine.learning.geneticalgorithm.mutation;


import com.vladnamik.developer.machine.learning.geneticalgorithm.City;
import com.vladnamik.developer.machine.learning.geneticalgorithm.Phenotype;
import com.vladnamik.developer.machine.learning.geneticalgorithm.RandomNumber;

import java.util.List;

/**
 * Меняются местами два гена
 */
public class SinglePointMutation implements Mutation {
    @Override
    public Phenotype mutation(Phenotype phenotype) {
        List<City> genes = phenotype.getList();
        swap(genes, Math.abs(RandomNumber.random.nextInt() % genes.size()), Math.abs(RandomNumber.random.nextInt() % genes.size()));
        return phenotype;
    }

    public void swap(List<City> list, int i, int j) {
        City city = list.get(i);
        City city2 = list.get(j);

        list.remove(i);
        list.add(i, city2);

        list.remove(j);
        list.add(j, city);
    }
}
