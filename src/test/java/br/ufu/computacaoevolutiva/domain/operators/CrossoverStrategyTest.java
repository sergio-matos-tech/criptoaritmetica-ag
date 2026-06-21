package br.ufu.computacaoevolutiva.domain.operators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import br.ufu.computacaoevolutiva.domain.model.Individual;

class CrossoverStrategyTest {

    @Test
    void testCyclicCrossoverProducesValidPermutations() {
        // Arrange
        Individual parent1 = new Individual(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        Individual parent2 = new Individual(new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0});
        
        CyclicCrossover cyclic = new CyclicCrossover();
        Random random = new Random(); // O Cíclico não usa random, mas a interface exige

        // Act
        Individual[] children = cyclic.crossover(parent1, parent2, random);

        // Assert
        assertEquals(2, children.length, "Deve gerar exatamente dois filhos.");
        assertValidPermutation(children[0].getGenes());
        assertValidPermutation(children[1].getGenes());
    }

    @Test
    void testPmxCrossoverProducesValidPermutations() {
        // Arrange
        Individual parent1 = new Individual(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        Individual parent2 = new Individual(new int[]{4, 5, 2, 1, 8, 7, 6, 9, 3, 0});
        
        PmxCrossover pmx = new PmxCrossover();
        
        // Vamos forçar os pontos de corte em 3 e 6
        Random riggedRandom = new Random() {
            private int count = 0;
            @Override
            public int nextInt(int bound) {
                return (count++ == 0) ? 3 : 6;
            }
        };

        // Act
        Individual[] children = pmx.crossover(parent1, parent2, riggedRandom);

        // Assert
        assertEquals(2, children.length, "Deve gerar exatamente dois filhos.");
        assertValidPermutation(children[0].getGenes());
        assertValidPermutation(children[1].getGenes());
        
        // Validação extra: O miolo (índices 3 a 6) do Filho 1 DEVE ser exatamente o miolo do Pai 1
        int[] child1Genes = children[0].getGenes();
        assertEquals(3, child1Genes[3]);
        assertEquals(4, child1Genes[4]);
        assertEquals(5, child1Genes[5]);
        assertEquals(6, child1Genes[6]);
    }

    // --- Métodos Auxiliares Defensivos ---

    /**
     * Garante que o genótipo tem tamanho 10 e possui todos os números de 0 a 9 sem repetição.
     */
    private void assertValidPermutation(int[] genes) {
        assertEquals(10, genes.length, "O cromossomo deve ter tamanho 10.");
        
        Set<Integer> uniqueGenes = new HashSet<>();
        for (int gene : genes) {
            assertTrue(gene >= 0 && gene <= 9, "O gene deve estar no intervalo [0, 9]. Valor encontrado: " + gene);
            uniqueGenes.add(gene);
        }
        
        assertEquals(10, uniqueGenes.size(), "O cromossomo não pode conter genes repetidos.");
    }
}