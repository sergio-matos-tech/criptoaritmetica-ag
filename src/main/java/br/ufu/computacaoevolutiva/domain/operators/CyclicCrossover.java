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
        boolean[] visited = new boolean[length];

        boolean copyFromP1ToC1 = true;

        for (int i = 0; i < length; i++) {
            if (!visited[i]) {
                int currentIndex = i;
                
                while (!visited[currentIndex]) {
                    visited[currentIndex] = true;
                    
                    if (copyFromP1ToC1) {
                        c1[currentIndex] = p1[currentIndex];
                        c2[currentIndex] = p2[currentIndex];
                    } else {
                        c1[currentIndex] = p2[currentIndex];
                        c2[currentIndex] = p1[currentIndex];
                    }

                    int valueToFind = p2[currentIndex];
                    currentIndex = findIndex(p1, valueToFind);
                }
                
                copyFromP1ToC1 = !copyFromP1ToC1;
            }
        }

        return new Individual[]{new Individual(c1), new Individual(c2)};
    }

    // Busca linear O(N). Como N é 10, é mais rápido que usar Map/Hashings.
    private int findIndex(int[] array, int value) {
        for (int i = 0; i < array.length; i++) 
            if (array[i] == value) return i;
        
        throw new IllegalStateException("Valor não encontrado. Permutação corrompida.");
    }
}