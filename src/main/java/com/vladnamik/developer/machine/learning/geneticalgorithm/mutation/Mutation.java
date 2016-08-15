package com.vladnamik.developer.machine.learning.geneticalgorithm.mutation;


import com.vladnamik.developer.machine.learning.geneticalgorithm.Phenotype;

/**
 * Способ мутации
 * Выбирается один из фенотипа и
 */
public interface Mutation {
    Phenotype mutation(Phenotype phenotype);
}
