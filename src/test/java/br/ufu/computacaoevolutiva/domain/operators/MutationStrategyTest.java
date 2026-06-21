package br.ufu.computacaoevolutiva.domain.operators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;

import br.ufu.computacaoevolutiva.domain.model.Individual;

class MutationStrategyTest {

    @Test
    void testSwapMutationAltersOrderButPreservesElements() {
        // Arrange
        int[] originalGenes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Individual ind = new Individual(originalGenes.clone());
        SwapMutation mutation = new SwapMutation();

        // Força o Random a escolher os índices 2 e 7 para a troca
        Random riggedRandom = new Random() {
            private int count = 0;
            @Override
            public int nextInt(int bound) {
                return (count++ == 0) ? 2 : 7;
            }
        };

        // Act
        mutation.mutate(ind, riggedRandom);
        int[] mutatedGenes = ind.getGenes();

        // Assert
        assertEquals(7, mutatedGenes[2], "O valor no índice 2 deve ser o antigo valor do índice 7.");
        assertEquals(2, mutatedGenes[7], "O valor no índice 7 deve ser o antigo valor do índice 2.");
        assertEquals(0, mutatedGenes[0], "Os outros índices não devem ser alterados.");

        // Validação de integridade: a soma de 0 a 9 deve continuar sendo 45
        assertEquals(10, mutatedGenes.length);
        assertEquals(45, Arrays.stream(mutatedGenes).sum(), "A mutação não pode inserir ou remover valores, apenas trocar.");
    }
}