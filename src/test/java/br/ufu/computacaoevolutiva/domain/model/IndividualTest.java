package br.ufu.computacaoevolutiva.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class IndividualTest {

    @Test
    void testRandomInitializationHasNoRepetitions() {
        Random random = new Random(42); // Seed fixa para previsibilidade em testes
        Individual individual = new Individual(random);

        int[] genes = individual.getGenes();
        Set<Integer> uniqueGenes = new HashSet<>();

        for (int gene : genes) {
            uniqueGenes.add(gene);
        }

        assertEquals(10, genes.length, "O vetor deve ter tamanho 10.");
        assertEquals(10, uniqueGenes.size(), "Não pode haver repetição de genes.");
        
        for (int i = 0; i < 10; i++) {
            assertTrue(uniqueGenes.contains(i), "O genótipo deve conter o número " + i);
        }
    }

    @Test
    void testFitnessState() {
        Random random = new Random();
        Individual individual = new Individual(random);

        assertFalse(individual.isEvaluated(), "O indivíduo recém-criado não deve estar avaliado.");

        assertThrows(IllegalStateException.class, individual::getFitness, 
            "Deve lançar exceção se tentar pegar o fitness antes de avaliá-lo.");

        individual.setFitness(50);
        assertTrue(individual.isEvaluated(), "Após o set, o estado de avaliação deve ser true.");
        assertEquals(50, individual.getFitness(), "O fitness deve corresponder ao valor atribuído.");
    }
}