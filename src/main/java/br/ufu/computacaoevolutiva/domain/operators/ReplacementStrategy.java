package br.ufu.computacaoevolutiva.domain.operators;

import br.ufu.computacaoevolutiva.domain.model.Population;

public interface ReplacementStrategy {
    /**
     * Define a nova geração combinando pais e filhos.
     */
    Population replace(Population parents, Population offspring, int targetSize);
}