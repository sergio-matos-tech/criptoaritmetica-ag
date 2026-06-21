package br.ufu.computacaoevolutiva.domain.operators;

import br.ufu.computacaoevolutiva.domain.model.Population;

public class ElitismReplacement implements ReplacementStrategy {
    
    private final double elitismRate;

    public ElitismReplacement(double elitismRate) {
        this.elitismRate = elitismRate;
    }

    @Override
    public Population replace(Population parents, Population offspring, int targetSize) {
        Population nextGeneration = new Population();
        
        // Garante que os pais estejam ordenados para extrair a elite
        parents.sort();
        
        int eliteCount = (int) (targetSize * elitismRate);
        
        // 1. Salva a elite dos pais
        for (int i = 0; i < eliteCount; i++) {
            nextGeneration.addIndividual(parents.getIndividuals().get(i));
        }
        
        // 2. Preenche o resto com os filhos gerados
        int offspringCount = targetSize - eliteCount;
        for (int i = 0; i < Math.min(offspringCount, offspring.size()); i++) {
            nextGeneration.addIndividual(offspring.getIndividuals().get(i));
        }
        
        return nextGeneration;
    }
}