package com.vladnamik.developer.machine.learning.geneticalgorithm.selection;

import com.vladnamik.developer.machine.learning.geneticalgorithm.Phenotype;
import com.vladnamik.developer.machine.learning.geneticalgorithm.Population;
import com.vladnamik.developer.machine.learning.geneticalgorithm.RandomNumber;
import com.vladnamik.developer.machine.learning.geneticalgorithm.crossing.Crossing;

import java.util.ArrayList;
import java.util.List;

/**
 * Пропорциональная селекция
 */
public class ProportionalSelection implements Selection {
    private int childrenCoefficient;//процентное количество детей, которые появятся, от количества родителей
    private double[] probabilityArray;

    public ProportionalSelection(int childrenCoefficient) {
        this.childrenCoefficient = childrenCoefficient;
    }


    @Override
    public Population selection(Population population, Crossing crossing) {
        probabilityArray = getProbabiltyArray(population.getAll());
        List<Phenotype> parentsList = population.getAll();
        List<Phenotype> childrenList = new ArrayList<>((int) (parentsList.size() * ((double) childrenCoefficient / 100)));

        for (int i = 0; i < childrenCoefficient; i++) {
            double buf = RandomNumber.random.nextDouble();
            Phenotype firstParent = parentsList.get(0);
            double sum = 0;
            for (int j = 0; j < parentsList.size(); j++)  //выбираем первого родителя
            {
                sum += probabilityArray[j];
                if (buf < sum) {
                    firstParent = parentsList.get(j);
                    break;
                }
            }

            Phenotype secondParent = parentsList.get(1);
            sum = 0;
            for (int j = 0; j < parentsList.size(); j++) //выбираем второго родителя
            {
                sum += probabilityArray[j];
                if (buf < sum) {
                    secondParent = parentsList.get(j);
                    break;
                }
            }
            if (secondParent == firstParent)
                secondParent = parentsList.get(1);
            if (secondParent == firstParent)
                secondParent = parentsList.get(0);


            Phenotype child = crossing.crossing(firstParent, secondParent);

            int k = 0;
            while (firstParent.getList().equals(child.getList()) || secondParent.getList().equals(child.getList())) //исключаем случай одинаковых фенотипов
            {
                if (k == 6) {
                    child.setAlive(false);
                    break;
                }
                k++;
                child = crossing.crossing(firstParent, secondParent);
            }

            childrenList.add(child);


        }
        population.addAll(childrenList);
        return population;
    }


    private double[] getProbabiltyArray(List<Phenotype> list) //массив вероятности выбора объекта
    {
        double[] probabilityArray = new double[list.size()];
        double sum = 0;
        for (Phenotype phenotype : list)
            sum += phenotype.getFitness();

        for (int i = 0; i < probabilityArray.length; i++)
            probabilityArray[i] = sum / list.get(i).getFitness();

        sum = 0;
        for (int i = 0; i < probabilityArray.length; i++)
            sum += probabilityArray[i];

        for (int i = 0; i < probabilityArray.length; i++)
            probabilityArray[i] = probabilityArray[i] / sum;

        return probabilityArray;
    }

}
