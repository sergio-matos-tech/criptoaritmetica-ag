package br.ufu.computacaoevolutiva.domain.fitness;

import br.ufu.computacaoevolutiva.domain.model.CryptoProblem;
import br.ufu.computacaoevolutiva.domain.model.Individual;

public class GlobalDifferenceFitness implements FitnessEvaluator {

    private static final long LEADING_ZERO_PENALTY = 100_000L;

    @Override
    public void evaluate(Individual individual, CryptoProblem problem) {
        int[] genes = individual.getGenes();

        int[] weights = problem.getWeights();
        long error = 0;

        for (int i = 0; i < weights.length; i++) {
            error += (long) weights[i] * genes[i];
        }

        long fitness = Math.abs(error);

        // Penalidade híbrida:
        // a solução continua sendo avaliada pelo erro global, mas cada zero inicial
        // adiciona uma penalidade forte em vez de descartar o indivíduo por completo.
        int invalidLeadingZeros = problem.countInvalidLeadingZeros(genes);
        if (invalidLeadingZeros > 0) {
            long penalty = LEADING_ZERO_PENALTY * invalidLeadingZeros * invalidLeadingZeros;
            fitness += penalty;
        }

        if (fitness > Integer.MAX_VALUE) {
            fitness = Integer.MAX_VALUE;
        }

        individual.setFitness((int) fitness);
    }
}
