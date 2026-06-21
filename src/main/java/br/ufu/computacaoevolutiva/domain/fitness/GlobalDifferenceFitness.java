package br.ufu.computacaoevolutiva.domain.fitness;

import br.ufu.computacaoevolutiva.domain.model.CryptoProblem;
import br.ufu.computacaoevolutiva.domain.model.Individual;

public class GlobalDifferenceFitness implements FitnessEvaluator {

    @Override
    public void evaluate(Individual individual, CryptoProblem problem) {
        int[] genes = individual.getGenes();
        int[] weights = problem.getWeights();
        
        int error = 0;

        for (int i = 0; i < weights.length; i++) 
            error += weights[i] * genes[i];
        
        // A função de avaliação (Fitness) exige a diferença absoluta: |(SEND+MORE)-MONEY|
        individual.setFitness(Math.abs(error));
    }
}