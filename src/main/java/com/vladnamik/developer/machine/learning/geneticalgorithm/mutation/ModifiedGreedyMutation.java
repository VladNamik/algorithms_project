package com.vladnamik.developer.machine.learning.geneticalgorithm.mutation;


import com.vladnamik.developer.machine.learning.geneticalgorithm.Phenotype;
import com.vladnamik.developer.machine.learning.geneticalgorithm.RandomNumber;

/**
 * Модифицированная жадная мутация
 * С заданной вероятностью меняет местами первый/последний с тем, что в середине
 */
public class ModifiedGreedyMutation implements Mutation {
    private SinglePointMutation singlePointMutation;
    private GreedyMutation greedyMutation;
    private double swapProbability;

    public ModifiedGreedyMutation(double swapProbability) {
        singlePointMutation = new SinglePointMutation();
        greedyMutation = new GreedyMutation();
        this.swapProbability = swapProbability;
    }

    @Override
    public Phenotype mutation(Phenotype phenotype) {
        if (RandomNumber.random.nextDouble() < swapProbability) {
            if (RandomNumber.random.nextDouble() < 0.5)
                singlePointMutation.swap(phenotype.getList(), 0, 1 + Math.abs(RandomNumber.random.nextInt() % (phenotype.getList().size() - 1)));
            else
                singlePointMutation.swap(phenotype.getList(), phenotype.getList().size() - 1, Math.abs(RandomNumber.random.nextInt() % (phenotype.getList().size() - 1)));
        }

        return greedyMutation.mutation(phenotype);
    }
}
