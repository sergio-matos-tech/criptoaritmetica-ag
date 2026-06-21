package br.ufu.computacaoevolutiva.domain.operators;

import java.util.Random;
import br.ufu.computacaoevolutiva.domain.model.Individual;

public interface MutationStrategy {
    /**
     * Aplica a mutação diretamente no genótipo do indivíduo.
     */
    void mutate(Individual individual, Random random);
}