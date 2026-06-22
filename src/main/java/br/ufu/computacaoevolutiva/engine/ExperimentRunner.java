package br.ufu.computacaoevolutiva.engine;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import br.ufu.computacaoevolutiva.domain.fitness.FitnessEvaluator;
import br.ufu.computacaoevolutiva.domain.fitness.GlobalDifferenceFitness;
import br.ufu.computacaoevolutiva.domain.model.CryptoProblem;
import br.ufu.computacaoevolutiva.domain.operators.CrossoverStrategy;
import br.ufu.computacaoevolutiva.domain.operators.ElitismReplacement;
import br.ufu.computacaoevolutiva.domain.operators.OrderedReplacement;
import br.ufu.computacaoevolutiva.domain.operators.PmxCrossover;
import br.ufu.computacaoevolutiva.domain.operators.ReplacementStrategy;
import br.ufu.computacaoevolutiva.domain.operators.RouletteSelectionAggressive;
import br.ufu.computacaoevolutiva.domain.operators.RouletteSelection;
import br.ufu.computacaoevolutiva.domain.operators.SelectionStrategy;
import br.ufu.computacaoevolutiva.domain.operators.SwapMutation;
import br.ufu.computacaoevolutiva.domain.operators.TournamentSelection;
import br.ufu.computacaoevolutiva.domain.operators.CyclicCrossover;

public class ExperimentRunner {

    private static final long BASE_SEED = 2026L;

    public static void main(String[] args) {
        System.out.println("=====================================================================================");
        System.out.println("              Iniciando Etapa 1: Algoritmo Genetico (16.000 runs)  ");
        System.out.println("=====================================================================================");

        CryptoProblem problem = new CryptoProblem(Arrays.asList("SEND", "MORE"), "MONEY");
        FitnessEvaluator evaluator = new GlobalDifferenceFitness();

        int popSize = 100;
        int maxGen = 50;
        int numExecutions = 1000;

        try (PrintWriter writer = new PrintWriter(new FileWriter("resultados_etapa1.csv"))) {
            writer.println("ID,TaxaMutacao,Selecao,Crossover,Reinsercao,Convergencias,TempoMedio_ms,TaxaConvergencia");

            int configId = 1;
            double[] mutationRates = {0.1, 0.2};

            for (double tm : mutationRates) {
                for (int s = 1; s <= 2; s++) {
                    for (int c = 1; c <= 2; c++) {
                        for (int r = 1; r <= 2; r++) {
                            double cr = (r == 2) ? 0.8 : 0.6;

                            SelectionStrategy selection = (s == 1)
                                    ? new TournamentSelection(3)
                                    : new RouletteSelectionAggressive();
                            CrossoverStrategy crossover = (c == 1)
                                    ? new CyclicCrossover()
                                    : new PmxCrossover();
                            ReplacementStrategy replacement = (r == 1)
                                    ? new OrderedReplacement()
                                    : new ElitismReplacement(0.2);

                            String sLabel = (s == 1) ? "Torneio(3)" : "Roleta";
                            String cLabel = (c == 1) ? "Ciclico" : "PMX";
                            String rLabel = (r == 1) ? "Ordenada" : "Elitismo(20%)";

                            System.out.printf(
                                    "Config %02d/16 | TM: %.1f | S: %-10s | C: %-7s | R: %-13s -> ",
                                    configId, tm, sLabel, cLabel, rLabel);

                            int convergences = 0;
                            long totalTimeNs = 0;

                            for (int i = 0; i < numExecutions; i++) {
                                Random runRng = new Random(seedFor(configId, i));
                                GeneticAlgorithm ga = new GeneticAlgorithm(
                                        problem, popSize, maxGen, cr, tm,
                                        evaluator, selection, crossover, new SwapMutation(), replacement, runRng
                                );

                                EvolutionResult result = ga.evolve();

                                if (result.isConverged()) {
                                    convergences++;
                                }
                                totalTimeNs += result.getExecutionTimeNs();
                            }

                            double avgTimeMs = (totalTimeNs / (double) numExecutions) / 1_000_000.0;
                            double convergenceRate = (convergences / (double) numExecutions) * 100.0;

                            System.out.printf("Conv: %5.1f%% | Tempo: %5.2f ms%n", convergenceRate, avgTimeMs);

                            writer.printf(Locale.US, "%d,%.1f,%s,%s,%s,%d,%.4f,%.1f%n",
                                    configId, tm, sLabel, cLabel, rLabel, convergences, avgTimeMs, convergenceRate);

                            configId++;
                        }
                    }
                }
            }

            System.out.println("=====================================================================================");
            System.out.println("                Experimentos concluidos com sucesso!                 ");
            System.out.println("                Relatorio exportado para: resultados_etapa1.csv      ");
            System.out.println("=====================================================================================");

        } catch (Exception e) {
            System.err.println("Erro critico de I/O ao gravar o arquivo CSV: " + e.getMessage());
        }
    }

    private static long seedFor(int configId, int executionIndex) {
        return BASE_SEED + (configId * 10_000L) + executionIndex;
    }
}
