package br.ufu.computacaoevolutiva.domain.operators;

import java.util.List;
import java.util.Random;

import br.ufu.computacaoevolutiva.domain.model.Individual;
import br.ufu.computacaoevolutiva.domain.model.Population;

public class RouletteSelection implements SelectionStrategy {

    @Override
    public Individual select(Population population, Random random) {
        List<Individual> individuals = population.getIndividuals();
        if (individuals.isEmpty()) 
            throw new IllegalStateException("A população não pode estar vazia.");
        
        double totalFitness = 0.0;
        double[] selectionWeights = new double[individuals.size()];

        // Roleta proporcional para minimização:
        // converte fitness em aptidão invertida, favorecendo quem tem menor erro.
        for (int i = 0; i < individuals.size(); i++) {
            selectionWeights[i] = 1.0 / (1.0 + individuals.get(i).getFitness());
            totalFitness += selectionWeights[i];
        }

        double spin = random.nextDouble() * totalFitness;
        double accumulated = 0.0;

        for (int i = 0; i < individuals.size(); i++) {
            accumulated += selectionWeights[i];
            if (accumulated >= spin) 
                return individuals.get(i);
        }

        return individuals.get(individuals.size() - 1);
    }
}
