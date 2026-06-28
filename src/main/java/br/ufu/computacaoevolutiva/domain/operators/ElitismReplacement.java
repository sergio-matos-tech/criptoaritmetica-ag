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
        
        parents.sort();
        int eliteCount = (int) (targetSize * elitismRate);
        
        for (int i = 0; i < eliteCount; i++) 
            nextGeneration.addIndividual(parents.getIndividuals().get(i));
        
        int offspringCount = targetSize - eliteCount;
        for (int i = 0; i < Math.min(offspringCount, offspring.size()); i++) 
            nextGeneration.addIndividual(offspring.getIndividuals().get(i));
        
        return nextGeneration;
    }
}