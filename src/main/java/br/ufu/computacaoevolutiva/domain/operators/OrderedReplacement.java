package br.ufu.computacaoevolutiva.domain.operators;

import br.ufu.computacaoevolutiva.domain.model.Individual;
import br.ufu.computacaoevolutiva.domain.model.Population;

public class OrderedReplacement implements ReplacementStrategy {

    @Override
    public Population replace(Population parents, Population offspring, int targetSize) {
        Population combined = new Population();

        for (Individual ind : parents.getIndividuals()) 
            combined.addIndividual(ind);
        
        for (Individual ind : offspring.getIndividuals()) 
            combined.addIndividual(ind);

        combined.sort();

        // Extrai o top N
        Population nextGeneration = new Population();
        for (int i = 0; i < targetSize; i++) 
            nextGeneration.addIndividual(combined.getIndividuals().get(i));
        
        return nextGeneration;
    }
}