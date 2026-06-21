package br.ufu.computacaoevolutiva.domain.operators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import br.ufu.computacaoevolutiva.domain.model.Individual;
import br.ufu.computacaoevolutiva.domain.model.Population;

class SelectionStrategyTest {

    @Test
    void testTournamentSelectionPicksBestFromSample() {
        // Arrange
        Population pop = createDummyPopulation();
        TournamentSelection tournament = new TournamentSelection(3);

        // Criamos um Random "viciado" que sempre retornará os índices 1, 3 e 4.
        // Os fitnesses nesses índices são: 20, 40 e 50. O esperado é que ele escolha o de fitness 20.
        Random riggedRandom = new Random() {
            private int[] sequence = {1, 3, 4};
            private int callCount = 0;

            @Override
            public int nextInt(int bound) {
                return sequence[callCount++];
            }
        };

        // Act
        Individual winner = tournament.select(pop, riggedRandom);

        // Assert
        assertEquals(20, winner.getFitness(), "O torneio deve selecionar o indivíduo com o menor erro (fitness 20) dentre os sorteados.");
    }

    @Test
    void testRouletteSelectionFavorsLowerFitness() {
        // Arrange
        Population pop = new Population();
        
        // Indivíduo A: fitness 0 (solução perfeita, erro zero) -> aptidão invertida = 1.0
        Individual indA = new Individual(new int[10]);
        indA.setFitness(0);
        pop.addIndividual(indA);

        // Indivíduo B: fitness 9 (erro alto) -> aptidão invertida = 0.1
        Individual indB = new Individual(new int[10]);
        indB.setFitness(9);
        pop.addIndividual(indB);
        
        // Total da roleta = 1.1. 
        // Indivíduo A tem a fatia de 0.0 até 1.0
        // Indivíduo B tem a fatia de 1.0 até 1.1

        RouletteSelection roulette = new RouletteSelection();

        // Criamos um Random "viciado" para cair no começo da roleta (0.5)
        Random spinForA = new Random() {
            @Override
            public double nextDouble() {
                return 0.5 / 1.1; // Cai no meio da fatia do Indivíduo A
            }
        };

        // Criamos um Random "viciado" para cair no final da roleta (1.05)
        Random spinForB = new Random() {
            @Override
            public double nextDouble() {
                return 1.05 / 1.1; // Cai na fatia do Indivíduo B
            }
        };

        // Act
        Individual winnerA = roulette.select(pop, spinForA);
        Individual winnerB = roulette.select(pop, spinForB);

        // Assert
        assertEquals(0, winnerA.getFitness(), "A roleta deve selecionar o indivíduo A quando o giro cair na sua proporção.");
        assertEquals(9, winnerB.getFitness(), "A roleta deve selecionar o indivíduo B quando o giro cair na sua proporção.");
    }

    @Test
    void testEmptyPopulationThrowsException() {
        Population emptyPop = new Population();
        TournamentSelection tournament = new TournamentSelection(3);
        RouletteSelection roulette = new RouletteSelection();
        Random random = new Random();

        assertThrows(IllegalStateException.class, () -> tournament.select(emptyPop, random));
        assertThrows(IllegalStateException.class, () -> roulette.select(emptyPop, random));
    }

    // --- Métodos Auxiliares ---

    /**
     * Cria uma população de 5 indivíduos com fitness de 10 a 50 para os testes.
     */
    private Population createDummyPopulation() {
        Population pop = new Population();
        for (int i = 1; i <= 5; i++) {
            Individual ind = new Individual(new int[10]); // Genótipo vazio não importa para este teste
            ind.setFitness(i * 10); // Fitness: 10, 20, 30, 40, 50
            pop.addIndividual(ind);
        }
        return pop;
    }
}