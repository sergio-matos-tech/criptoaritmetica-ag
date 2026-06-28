package br.ufu.computacaoevolutiva.domain.operators;

import java.util.Random;
import br.ufu.computacaoevolutiva.domain.model.Individual;

public class CyclicCrossover implements CrossoverStrategy {

    @Override
    public Individual[] crossover(Individual parent1, Individual parent2, Random random) {
        int[] p1 = parent1.getGenes();
        int[] p2 = parent2.getGenes();
        int length = p1.length;

        int[] c1 = new int[length];
        int[] c2 = new int[length];
        
        boolean[] inCycle = new boolean[length];

        int currentIndex = 0;
        while (!inCycle[currentIndex]) {
            inCycle[currentIndex] = true; 
            int valueToFind = p2[currentIndex]; 
            currentIndex = findIndex(p1, valueToFind);
        }

        for (int i = 0; i < length; i++) {
            if (inCycle[i]) {
                c1[i] = p2[i];
                c2[i] = p1[i];
            } else {
                c1[i] = p1[i];
                c2[i] = p2[i];
            }
        }

        return new Individual[]{new Individual(c1), new Individual(c2)};
    }

    // Busca linear O(N). Como N é no máximo 10 (0 a 9), é muito rápido.
    private int findIndex(int[] array, int value) {
        for (int i = 0; i < array.length; i++) 
            if (array[i] == value) return i;
        
        throw new IllegalStateException("Valor não encontrado. Permutação corrompida.");
    }
}