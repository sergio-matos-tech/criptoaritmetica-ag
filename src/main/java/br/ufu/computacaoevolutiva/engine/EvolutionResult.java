package br.ufu.computacaoevolutiva.engine;

import br.ufu.computacaoevolutiva.domain.model.Individual;

public class EvolutionResult {
    private final boolean converged;
    private final Individual bestSolution;
    private final int generationsRan;
    private final long executionTimeNs; 

    public EvolutionResult(boolean converged, Individual bestSolution, int generationsRan, long executionTimeNs) {
        this.converged = converged;
        this.bestSolution = bestSolution;
        this.generationsRan = generationsRan;
        this.executionTimeNs = executionTimeNs;
    }

    public boolean isConverged() { return converged; }
    public Individual getBestSolution() { return bestSolution; }
    public int getGenerationsRan() { return generationsRan; }
    public long getExecutionTimeNs() { return executionTimeNs; }
    public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
}