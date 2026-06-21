package br.ufu.computacaoevolutiva.domain.operators;

import java.util.List;
import java.util.Random;

import br.ufu.computacaoevolutiva.domain.model.Individual;
import br.ufu.computacaoevolutiva.domain.model.Population;

public class TournamentSelection implements SelectionStrategy {
    
    private final int tourSize;

    public TournamentSelection(int tourSize) {
        this.tourSize = tourSize;
    }

    @Override
    public Individual select(Population population, Random random) {
        List<Individual> individuals = population.getIndividuals();
        if (individuals.isEmpty()) 
            throw new IllegalStateException("A população não pode estar vazia.");
        
        Individual best = null;

        // Sorteia 'tourSize' indivíduos e fica com o que tem o menor erro (menor fitness)
        for (int i = 0; i < tourSize; i++) {
            Individual candidate = individuals.get(random.nextInt(individuals.size()));
            
            if (best == null || candidate.getFitness() < best.getFitness()) 
                best = candidate;
        }

        return best;
    }
}