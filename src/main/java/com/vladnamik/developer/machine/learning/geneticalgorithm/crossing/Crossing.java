package com.vladnamik.developer.machine.learning.geneticalgorithm.crossing;


import com.vladnamik.developer.machine.learning.geneticalgorithm.Phenotype;

/**
 * Способ передачи информации потомку
 */
public interface Crossing {
    Phenotype crossing(Phenotype phenotype1, Phenotype phenotype2);
}
