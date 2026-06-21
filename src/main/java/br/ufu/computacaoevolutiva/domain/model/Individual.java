package br.ufu.computacaoevolutiva.domain.model;

import java.util.Random;

public class Individual {
    private final int[] genes;
    private int fitness;
    private boolean isEvaluated;

    // Construtor 1: População Inicial (Aleatório sem repetições via Fisher-Yates)
    public Individual(Random random) {
        this.genes = new int[10];
        
        for (int i = 0; i < 10; i++) 
            this.genes[i] = i;
        
        // Fisher-Yates Shuffle para embaralhar em O(N) sem alocação de memória extra
        for (int i = genes.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = genes[index];
            genes[index] = genes[i];
            genes[i] = temp;
        }
        this.isEvaluated = false;
    }

    // Construtor 2: Filhos (Criados a partir de Crossover ou Mutação)
    public Individual(int[] genes) {
        if (genes.length != 10) 
            throw new IllegalArgumentException("O cromossomo deve ter exatamente tamanho 10.");
        
        // Clona o array para evitar referência mutável indesejada (Defensive Copying)
        this.genes = genes.clone();
        this.isEvaluated = false;
    }

    public int[] getGenes() {
        return genes;
    }

    public int getFitness() {
        if (!isEvaluated) {
            throw new IllegalStateException("O fitness ainda não foi avaliado para este indivíduo.");
        }
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
        this.isEvaluated = true;
    }

    public boolean isEvaluated() {
        return isEvaluated;
    }
}