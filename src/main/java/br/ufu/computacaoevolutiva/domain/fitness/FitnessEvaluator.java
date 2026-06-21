package br.ufu.computacaoevolutiva.domain.fitness;

import br.ufu.computacaoevolutiva.domain.model.CryptoProblem;
import br.ufu.computacaoevolutiva.domain.model.Individual;

public interface FitnessEvaluator {
    /**
     * Avalia o indivíduo e injeta o valor de fitness calculado nele.
     */
    void evaluate(Individual individual, CryptoProblem problem);
}