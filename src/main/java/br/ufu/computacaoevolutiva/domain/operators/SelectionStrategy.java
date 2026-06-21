package br.ufu.computacaoevolutiva.domain.operators;

import java.util.Random;
import br.ufu.computacaoevolutiva.domain.model.Individual;
import br.ufu.computacaoevolutiva.domain.model.Population;

public interface SelectionStrategy {
    /**
     * Seleciona um indivíduo da população para ser pai/mãe da próxima geração.
     */
    Individual select(Population population, Random random);
}