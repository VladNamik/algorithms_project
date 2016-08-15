package com.vladnamik.developer.machine.learning.geneticalgorithm.selection;


import com.vladnamik.developer.machine.learning.geneticalgorithm.Phenotype;
import com.vladnamik.developer.machine.learning.geneticalgorithm.Population;
import com.vladnamik.developer.machine.learning.geneticalgorithm.RandomNumber;
import com.vladnamik.developer.machine.learning.geneticalgorithm.crossing.Crossing;

import java.util.ArrayList;
import java.util.List;

/**
 * Отбор усечением
 * Выбираются только лучшие
 */
public class TruncationSelection implements Selection {
    private int crossingCoefficient;//процент участвующих в селекции родителей
    private boolean deleteParent;//удалять или не удалять родителя / родителей
    private boolean twoChild;//два ребёнка с разными генотипами от двух родителей

    public TruncationSelection(int crossingCoefficient, boolean deleteParent, boolean twoChild) {
        this.crossingCoefficient = crossingCoefficient;
        this.deleteParent = deleteParent;
        this.twoChild = twoChild;
    }

    @Override
    public Population selection(Population population, Crossing crossing) {
        int populationSize = population.getAll().size();
        int pairQuantity = (int) (populationSize * (double) crossingCoefficient / 100 / 2);

        List<Phenotype> childrenList = new ArrayList<>(pairQuantity);
        List<Phenotype> parentsList = new ArrayList<>(pairQuantity * 2);

        for (int i = 0; i < pairQuantity * 2; i++)
            parentsList.add(population.getAll().get(i));


        for (int i = 0; i < pairQuantity; i++) {
            int firstParentNumber = (int) (parentsList.size() * RandomNumber.random.nextDouble());
            int secondParentNumber = (int) (parentsList.size() * RandomNumber.random.nextDouble());

            if (parentsList.size() >= 2) // исключаем случай одинаковых фенотипов
            {
                while (secondParentNumber == firstParentNumber)
                    secondParentNumber = (int) (parentsList.size() * RandomNumber.random.nextDouble());
            }

            Phenotype firstParent = parentsList.get(firstParentNumber);
            Phenotype secondParent = parentsList.get(secondParentNumber);
            Phenotype child = crossing.crossing(firstParent, secondParent);

            int k = 0;
            while (firstParent.getList().equals(child.getList()) || secondParent.getList().equals(child.getList())) //исключаем случай одинаковых фенотипов
            {
                if (k == 5) {
                    child.setAlive(false);
                    break;
                }
                k++;
                child = crossing.crossing(firstParent, secondParent);
            }

            childrenList.add(child);


            if (twoChild) //ещё один, если необходимо
            {
                Phenotype secondChild = crossing.crossing(secondParent, firstParent);

                k = 0;
                while (firstParent.getList().equals(secondChild.getList()) || secondParent.getList().equals(secondChild.getList())) //исключаем случай одинаковых фенотипов
                {
                    if (k == 5) {
                        secondChild.setAlive(false);
                        break;
                    }
                    secondChild = crossing.crossing(secondParent, firstParent);
                    k++;
                }

                childrenList.add(secondChild);
            }


            if (deleteParent)//удаляем родителя если задано условие
            {
                firstParent.setAlive(false);
                if (firstParent.getFitness() == population.getBest().getFitness()) // сохраняем жизнь, если он лучший
                {
                    firstParent.setAlive(true);
                    //childrenList.forEach((x) -> {if (x.getFitness() < firstParent.getFitness()) firstParent.setAlive(false);});
                }
            }
            if (deleteParent && twoChild) {
                secondParent.setAlive(false);
                if (secondParent.getFitness() == population.getBest().getFitness()) // сохраняем жизнь, если он лучший
                {
                    secondParent.setAlive(true);
                    //childrenList.forEach((x) -> {if (x.getFitness() <= secondParent.getFitness()) secondParent.setAlive(false);});
                }
            }
        }


        population.addAll(childrenList);
        return population;
    }
}
