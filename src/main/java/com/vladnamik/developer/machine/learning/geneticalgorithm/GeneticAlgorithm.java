package com.vladnamik.developer.machine.learning.geneticalgorithm;


import com.vladnamik.developer.machine.learning.geneticalgorithm.crossing.Crossing;
import com.vladnamik.developer.machine.learning.geneticalgorithm.mutation.Mutation;
import com.vladnamik.developer.machine.learning.geneticalgorithm.refresh.Refresh;
import com.vladnamik.developer.machine.learning.geneticalgorithm.selection.Selection;

import java.util.List;

/**
 * Реализация генетического алгоритма
 */
public class GeneticAlgorithm {

    //данные
    private Roads roads;
    private Population currentPopulation;
    double maxWeight = 0;
    int iterationQuantity = 200;
    int mutationChance = 20;
    boolean deleteTheSame;


    //реализации конкретных функций алгоритма
    private Crossing crossing;
    private Mutation mutation;
    private Selection selection;
    private Refresh refresh;


    //процеcc выполнения
    private double resultStatus = 0;//процент выполнения задачи
    private boolean isStopped = false;


    public GeneticAlgorithm(Roads roads, AlgorithmStartParameters startParameters) {
        this.roads = roads;
        this.crossing = startParameters.crossing;
        this.mutation = startParameters.mutation;
        this.iterationQuantity = startParameters.iterationQuantity;
        this.selection = startParameters.selection;
        this.mutationChance = startParameters.mutationChance;
        this.refresh = startParameters.refresh;
        this.deleteTheSame = startParameters.deleteTheSame;


        currentPopulation = new Population();
        for (int i = 0; i < startParameters.populationSize; i++)
            currentPopulation.add(new Phenotype(roads));
    }

    public void run() {
        int i = 0;//номер поколения

        while (currentPopulation.getBest().getFitness() > maxWeight && !isStopped && i < iterationQuantity) {


            //производим выборку и скрещевание
            this.currentPopulation = selection.selection(currentPopulation, crossing);


            //мутация
            if (getStartMutation(i)) {
                List<Phenotype> phenotypeList = currentPopulation.getAll();
                for (int j = 1; j < phenotypeList.size(); j++)
                    if (RandomNumber.random.nextDouble() <= (double) mutationChance / 100)
                        mutation.mutation(phenotypeList.get(j));
                currentPopulation.sort();
            }


            //помечаем одинаковые, если они есть
            if (deleteTheSame) {
                double[] fitness = new double[1];
                fitness[0] = 0.0;
                currentPopulation.getAll().forEach((x) ->
                {
                    if (x.getFitness() == fitness[0])
                        x.setAlive(false);
                    fitness[0] = x.getFitness();
                });
            }

            //очищение популяции от лишних особей
            this.currentPopulation = refresh.refresh(currentPopulation);


            i++;
            resultStatus = i / iterationQuantity;
            System.out.println(currentPopulation.getBest().getFitness());
        }
    }

    public double getResultStatus() {
        return resultStatus;
    }

    public Phenotype getResult() {
        return currentPopulation.getBest();
    }

    public void stop() {
        isStopped = true;
    }

    public boolean getStartMutation(int x) {
//        if(x < iterationQuantity / 10)
//            return true;
//
//        if (x % (int)Math.sqrt(iterationQuantity) == 0)
//            return true;
//        return false;
        return true;
    }
}
