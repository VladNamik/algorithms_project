package com.vladnamik.developer.machine.learning.geneticalgorithm;


import com.vladnamik.developer.machine.learning.geneticalgorithm.crossing.Crossing;
import com.vladnamik.developer.machine.learning.geneticalgorithm.crossing.DoublePointCrossover;
import com.vladnamik.developer.machine.learning.geneticalgorithm.mutation.ModifiedGreedyMutation;
import com.vladnamik.developer.machine.learning.geneticalgorithm.mutation.Mutation;
import com.vladnamik.developer.machine.learning.geneticalgorithm.refresh.ProportionalRefresh;
import com.vladnamik.developer.machine.learning.geneticalgorithm.refresh.Refresh;
import com.vladnamik.developer.machine.learning.geneticalgorithm.selection.ProportionalSelection;
import com.vladnamik.developer.machine.learning.geneticalgorithm.selection.Selection;

/**
 * Параметры запуска алгоритма
 */
public class AlgorithmStartParameters {
    //функции
    public Crossing crossing;
    public Mutation mutation;
    public Selection selection;
    public Refresh refresh;

    public int populationSize = 20; //начальный размер популяции
    public int mutationChance = 20; //шанс на мутацию в процентах
    public int crossingCoefficient = 50; // процент участвующих в передаче информации наследникам

    public int iterationQuantity = 10000;

    public boolean deleteTheSame = true;

    public AlgorithmStartParameters() {
//        refresh = new RefreshKeepBest(populationSize);
        refresh = new ProportionalRefresh(populationSize);

//        selection = new TruncationSelection(crossingCoefficient, false, false);
        selection = new ProportionalSelection(crossingCoefficient * 2);

//        crossing = new SinglePointCrossover();
        crossing = new DoublePointCrossover();

//        mutation = new SinglePointMutation();
//        mutation = new CombinedMutation(0.07);
        mutation = new ModifiedGreedyMutation(0.04);
//        mutation = new GreedyMutation();
    }
}
