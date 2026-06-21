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

        double totalInvertedFitness = 0.0;
        double[] invertedFitnesses = new double[individuals.size()];

        // 1. Calcula a aptidão invertida para lidar com problema de minimização
        for (int i = 0; i < individuals.size(); i++) {
            // Soma 1 no denominador para evitar divisão por zero caso o fitness seja 0 (solução perfeita)
            invertedFitnesses[i] = 1.0 / (1.0 + individuals.get(i).getFitness());
            totalInvertedFitness += invertedFitnesses[i];
        }

        // 2. Gira a roleta (sorteia um valor entre 0 e a soma total)
        double spin = random.nextDouble() * totalInvertedFitness;
        double accumulated = 0.0;

        // 3. Verifica em qual fatia a "bolinha" da roleta caiu
        for (int i = 0; i < individuals.size(); i++) {
            accumulated += invertedFitnesses[i];
            if (accumulated >= spin) 
                return individuals.get(i);
        }

        // Fallback de segurança por erro de precisão de ponto flutuante
        return individuals.get(individuals.size() - 1);
    }
}