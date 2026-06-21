package br.ufu.computacaoevolutiva.domain.operators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import br.ufu.computacaoevolutiva.domain.model.Individual;
import br.ufu.computacaoevolutiva.domain.model.Population;

class ReplacementStrategyTest {

    @Test
    void testOrderedReplacementSelectsBestOverall() {
        // Arrange
        // População com erros altos (ruins)
        Population parents = createPopulation(100, 50, 60); 
        // População com erros baixos (bons)
        Population offspring = createPopulation(5, 15, 20); 
        
        OrderedReplacement ordered = new OrderedReplacement();
        
        // Act: Queremos formar a próxima geração com tamanho 3
        Population nextGen = ordered.replace(parents, offspring, 3);
        
        // Assert: Os 3 escolhidos devem ser os de fitness 5, 15 e 20 (todos da prole)
        assertEquals(3, nextGen.size());
        assertEquals(5, nextGen.getBest().getFitness(), "O melhor indivíduo absoluto deve ser o primeiro.");
        assertEquals(20, nextGen.getIndividuals().get(2).getFitness(), "O último selecionado deve ser o pior dos três melhores.");
    }

    @Test
    void testElitismReplacementMaintainsRatio() {
        // Arrange
        Population parents = createPopulation(10, 20, 30, 40, 50); // 5 pais
        Population offspring = createPopulation(15, 25, 35, 45, 55); // 5 filhos
        
        // 20% de elitismo em uma população de tamanho 5 = 1 pai de elite e 4 filhos
        ElitismReplacement elitism = new ElitismReplacement(0.2);
        
        // Act
        Population nextGen = elitism.replace(parents, offspring, 5);
        
        // Assert
        assertEquals(5, nextGen.size());
        
        // Verifica a composição: O 1º deve ser o pai de elite (fitness 10). O resto são os filhos.
        assertEquals(10, nextGen.getIndividuals().get(0).getFitness(), "A primeira posição deve pertencer ao pai de elite.");
        assertEquals(15, nextGen.getIndividuals().get(1).getFitness(), "A segunda posição deve ser do melhor filho.");
        assertEquals(45, nextGen.getIndividuals().get(4).getFitness(), "A última posição deve ser do 4º melhor filho.");
    }

    // --- Método Auxiliar ---
    private Population createPopulation(int... fitnesses) {
        Population pop = new Population();
        for (int fit : fitnesses) {
            Individual ind = new Individual(new int[10]);
            ind.setFitness(fit);
            pop.addIndividual(ind);
        }
        pop.sort();
        return pop;
    }
}