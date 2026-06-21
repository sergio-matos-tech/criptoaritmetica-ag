package br.ufu.computacaoevolutiva.domain.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Population {
    private final List<Individual> individuals;

    public Population() {
        this.individuals = new ArrayList<>();
    }

    public void addIndividual(Individual ind) {
        this.individuals.add(ind);
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public int size() {
        return individuals.size();
    }

    /**
     * Ordena a população pelo fitness.
     * Como a função objetivo é minimizar a diferença absoluta do problema criptoaritmético,
     * o melhor indivíduo é o que tem o fitness mais próximo de 0.
     */
    public void sort() {
        this.individuals.sort(Comparator.comparingInt(Individual::getFitness));
    }
    
    // pressupõe que a população já está ordenada
    public Individual getBest() {
        if (individuals.isEmpty()) 
            throw new IllegalStateException("População vazia.");
        
        return individuals.get(0);
    }
}