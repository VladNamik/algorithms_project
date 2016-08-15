package com.vladnamik.developer.machine.learning.geneticalgorithm.crossing;


import com.vladnamik.developer.machine.learning.geneticalgorithm.City;
import com.vladnamik.developer.machine.learning.geneticalgorithm.Phenotype;
import com.vladnamik.developer.machine.learning.geneticalgorithm.RandomNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Одноточечный кроссовер
 * "Частично отображаемый кроссовер"
 */

public class SinglePointCrossover implements Crossing {
    @Override
    public Phenotype crossing(Phenotype phenotype1, Phenotype phenotype2) {
        List<City> genes1 = phenotype1.getList();
        List<City> genes2 = phenotype2.getList();

        City[] comparisonArray = new City[genes1.size()];//создаём массив отображения
        for (int i = 0; i < comparisonArray.length; i++)
            comparisonArray[i] = null;

        int crossoverPoint = Math.abs(RandomNumber.random.nextInt() % (genes1.size() - 1)) + 1;//выбираем точку кроссовера

        for (int i = 0; i < crossoverPoint; i++)//заполняем массив отображения
            comparisonArray[genes1.get(i).getId()] = genes2.get(i);


        List<City> childGenes = new ArrayList<>(genes1.size());
        for (int i = 0; i < crossoverPoint; i++)//первую часть берём у первого родителя
            childGenes.add(genes1.get(i));

        for (int i = crossoverPoint; i < genes1.size(); i++)// вторую часть получаем либо у второго родителя, либо из массива отображения
        {
            City currentGen = genes2.get(i);
            while (comparisonArray[currentGen.getId()] != null) {
                currentGen = comparisonArray[currentGen.getId()];
            }
            childGenes.add(currentGen);
        }
        return new Phenotype(phenotype1.getRoads(), childGenes);
    }
}
