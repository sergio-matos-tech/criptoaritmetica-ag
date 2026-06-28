package br.ufu.computacaoevolutiva.engine;

import java.util.Random;

import br.ufu.computacaoevolutiva.domain.fitness.FitnessEvaluator;
import br.ufu.computacaoevolutiva.domain.model.CryptoProblem;
import br.ufu.computacaoevolutiva.domain.model.Individual;
import br.ufu.computacaoevolutiva.domain.model.Population;
import br.ufu.computacaoevolutiva.domain.operators.CrossoverStrategy;
import br.ufu.computacaoevolutiva.domain.operators.MutationStrategy;
import br.ufu.computacaoevolutiva.domain.operators.ReplacementStrategy;
import br.ufu.computacaoevolutiva.domain.operators.SelectionStrategy;

public class GeneticAlgorithm {

    private final CryptoProblem problem;
    private final int populationSize;
    private final int maxGenerations;
    private final double crossoverRate;
    private final double mutationRate;

    private final FitnessEvaluator fitnessEvaluator;
    private final SelectionStrategy selection;
    private final CrossoverStrategy crossover;
    private final MutationStrategy mutation;
    private final ReplacementStrategy replacement;
    private final Random random;

    public GeneticAlgorithm(CryptoProblem problem, int populationSize, int maxGenerations,
                            double crossoverRate, double mutationRate,
                            FitnessEvaluator fitnessEvaluator, SelectionStrategy selection,
                            CrossoverStrategy crossover, MutationStrategy mutation,
                            ReplacementStrategy replacement, Random random) {
        this.problem = problem;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.fitnessEvaluator = fitnessEvaluator;
        this.selection = selection;
        this.crossover = crossover;
        this.mutation = mutation;
        this.replacement = replacement;
        this.random = random;
    }

    public EvolutionResult evolve() {
        long startTime = System.nanoTime();

        // 1. Inicialização limpa
        Population currentPopulation = new Population();
        for (int i = 0; i < populationSize; i++) 
            currentPopulation.addIndividual(new Individual(random));
        
        // 2. Avaliação Inicial
        evaluatePopulation(currentPopulation);

        int currentGeneration = 0;
        Individual globalBest = currentPopulation.getBest();

        // 3. Loop Evolutivo
        while (currentGeneration < maxGenerations && globalBest.getFitness() != 0) {
            Population offspring = new Population();

            while (offspring.size() < populationSize) {
                Individual parent1 = selection.select(currentPopulation, random);
                Individual parent2 = selection.select(currentPopulation, random);

                Individual child1;
                Individual child2;

                if (random.nextDouble() < crossoverRate) {
                    Individual[] children = crossover.crossover(parent1, parent2, random);
                    child1 = children[0];
                    child2 = children[1];
                } else {
                    child1 = new Individual(parent1.getGenes());
                    child2 = new Individual(parent2.getGenes());
                }

                if (random.nextDouble() < mutationRate) mutation.mutate(child1, random);
                if (random.nextDouble() < mutationRate) mutation.mutate(child2, random);

                offspring.addIndividual(child1);
                if (offspring.size() < populationSize) 
                    offspring.addIndividual(child2);
            }

            evaluatePopulation(offspring);

            currentPopulation = replacement.replace(currentPopulation, offspring, populationSize);
            
            Individual generationBest = currentPopulation.getBest();
            if (generationBest.getFitness() < globalBest.getFitness()) 
                globalBest = generationBest;
            
            currentGeneration++;
        }

        long endTime = System.nanoTime();
        boolean converged = (globalBest.getFitness() == 0);

        return new EvolutionResult(converged, globalBest, currentGeneration, endTime - startTime);
    }

    private void evaluatePopulation(Population population) {
        for (Individual ind : population.getIndividuals()) 
            if (!ind.isEvaluated()) fitnessEvaluator.evaluate(ind, problem);
        
        population.sort(); // Garante que a população sempre esteja ordenada do melhor para o pior
    }
}