package br.ufu.computacaoevolutiva.domain.operators;

import java.util.Random;
import br.ufu.computacaoevolutiva.domain.model.Individual;

public interface CrossoverStrategy {
    /**
     * Recombina dois pais para gerar dois filhos.
     * Retorna um array contendo [Filho1, Filho2].
     */
    Individual[] crossover(Individual parent1, Individual parent2, Random random);
}