package com.vladnamik.developer.machine.learning.geneticalgorithm.mutation;

import com.vladnamik.developer.machine.learning.geneticalgorithm.City;
import com.vladnamik.developer.machine.learning.geneticalgorithm.Phenotype;
import com.vladnamik.developer.machine.learning.geneticalgorithm.RandomNumber;
import com.vladnamik.developer.machine.learning.geneticalgorithm.Roads;

import java.util.ArrayList;
import java.util.List;

/**
 * "Жадная мутация"
 */
public class GreedyMutation implements Mutation {
    @Override
    public Phenotype mutation(Phenotype phenotype) {
        List<City> phenotypeList = phenotype.getList();
        if (phenotypeList.size() < 4)
            return phenotype;

        int firstPoint = 1 + Math.abs(RandomNumber.random.nextInt() % (phenotypeList.size() - 4));
        int secondPoint = firstPoint + 2 + Math.abs(RandomNumber.random.nextInt() % (phenotypeList.size() - firstPoint - 3));

        List<City> intermediateList = new ArrayList<>(secondPoint - firstPoint);
        for (int i = firstPoint; i < secondPoint; i++)
            intermediateList.add(phenotypeList.get(i));
        int k = secondPoint - 1;
        while (k >= firstPoint) {
            phenotypeList.remove(k);
            k--;
        }


        final Roads roads = phenotype.getRoads();
        City lastCity = phenotypeList.get(firstPoint - 1);
        int currentAddToListPoint = firstPoint;
        while (intermediateList.size() > 0) {
            k = 0;
            for (int i = 1; i < intermediateList.size(); i++)
                if (roads.getWeight(lastCity, intermediateList.get(k)) > roads.getWeight(lastCity, intermediateList.get(i)))
                    k = i;

            phenotypeList.add(currentAddToListPoint, intermediateList.get(k));
            lastCity = intermediateList.get(k);
            intermediateList.remove(k);

            currentAddToListPoint++;
        }

        return phenotype;
    }
}
