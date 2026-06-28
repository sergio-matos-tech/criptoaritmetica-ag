package br.ufu.computacaoevolutiva.domain.operators;

import java.util.List;
import java.util.Random;

import br.ufu.computacaoevolutiva.domain.model.Individual;
import br.ufu.computacaoevolutiva.domain.model.Population;

public class RouletteSelectionAggressive implements SelectionStrategy {

    @Override
    public Individual select(Population population, Random random) {
        
        List<Individual> individuals = population.getIndividuals();
        if (individuals.isEmpty()) 
            throw new IllegalStateException("A população não pode estar vazia.");
        
        int size = individuals.size();
        double[] weights = new double[size];
        long maxFit = Long.MIN_VALUE;

        for (Individual individual : individuals) {
            long fitness = individual.getFitness();
            if (fitness < Long.MAX_VALUE / 2 && fitness > maxFit) 
                maxFit = fitness;
        }

        if (maxFit == Long.MIN_VALUE) 
            maxFit = 0;

        double totalWeight = 0.0;
        for (int i = 0; i < size; i++) {
            long fitness = individuals.get(i).getFitness();
            if (fitness >= Long.MAX_VALUE / 2) {
                weights[i] = 1e-9;
            } else {
                weights[i] = (double) (maxFit + 1 - fitness);
            }
            totalWeight += weights[i];
        }

        double spin = random.nextDouble() * totalWeight;
        double accumulated = 0.0;
        for (int i = 0; i < size; i++) {
            accumulated += weights[i];
            if (accumulated >= spin) 
                return individuals.get(i);
        }

        return individuals.get(size - 1);
    }
}
