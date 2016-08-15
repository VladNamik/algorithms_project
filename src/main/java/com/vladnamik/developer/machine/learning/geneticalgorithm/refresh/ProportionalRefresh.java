package com.vladnamik.developer.machine.learning.geneticalgorithm.refresh;


import com.vladnamik.developer.machine.learning.geneticalgorithm.Phenotype;
import com.vladnamik.developer.machine.learning.geneticalgorithm.Population;
import com.vladnamik.developer.machine.learning.geneticalgorithm.RandomNumber;

import java.util.List;

/**
 * Удаляет сначала те, что помечены, потом с определённой вероятностью удаляет те, что остались
 */
public class ProportionalRefresh implements Refresh {
    private int populationSize;

    public ProportionalRefresh(int populationSize) {
        this.populationSize = populationSize;
    }

    @Override
    public Population refresh(Population population) {
        List<Phenotype> list = population.getAll();

        int i = 0;
        while (list.size() > populationSize && i < list.size())//удаляем невыживших
        {
            if (!list.get(i).isAlive())
                list.remove(i);
            else
                i++;
        }

        while (list.size() > populationSize) {
            double[] probabilityArray = getProbabiltyArray(list);
            int k = list.size() - 1;
            while (k >= 1 && list.size() > populationSize) {
                if (RandomNumber.random.nextDouble() < probabilityArray[k])
                    list.remove(k);
                k--;
            }
        }

        return population;
    }

    private double[] getProbabiltyArray(List<Phenotype> list) //массив вероятности удаления объекта
    {
        double[] probabilityArray = new double[list.size()];
        double sum = 0;
        for (Phenotype phenotype : list)
            sum += phenotype.getFitness();
        sum -= list.get(0).getFitness();

        for (int i = 1; i < probabilityArray.length; i++)
            probabilityArray[i] = list.get(i).getFitness() / sum;
        probabilityArray[0] = 0;

        for (int i = 0; i < probabilityArray.length; i++)
            probabilityArray[i] *= (list.size() - populationSize);
        return probabilityArray;
    }
}
