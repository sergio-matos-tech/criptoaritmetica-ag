package br.ufu.computacaoevolutiva.engine;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import br.ufu.computacaoevolutiva.domain.fitness.FitnessEvaluator;
import br.ufu.computacaoevolutiva.domain.fitness.GlobalDifferenceFitness;
import br.ufu.computacaoevolutiva.domain.fitness.PositionalErrorFitness;
import br.ufu.computacaoevolutiva.domain.model.CryptoProblem;
import br.ufu.computacaoevolutiva.domain.operators.CrossoverStrategy;
import br.ufu.computacaoevolutiva.domain.operators.CyclicCrossover;
import br.ufu.computacaoevolutiva.domain.operators.ElitismReplacement;
import br.ufu.computacaoevolutiva.domain.operators.MutationStrategy;
import br.ufu.computacaoevolutiva.domain.operators.PmxCrossover;
import br.ufu.computacaoevolutiva.domain.operators.RouletteSelectionAggressive;
import br.ufu.computacaoevolutiva.domain.operators.SelectionStrategy;
import br.ufu.computacaoevolutiva.domain.operators.SwapMutation;
import br.ufu.computacaoevolutiva.domain.operators.TournamentSelection;

public class ExperimentRunnerEtapa3 {

    private static final long BASE_SEED = 2026L;
    private static final int NUM_EXECUTIONS = 1000;
    private static final int NUM_PROBLEMS = 5;

    private static final List<CryptoProblem> PROBLEMS = List.of(
            new CryptoProblem(Arrays.asList("SEND", "MORE"), "MONEY"),
            new CryptoProblem(Arrays.asList("EAT", "THAT"), "APPLE"),
            new CryptoProblem(Arrays.asList("CROSS", "ROADS"), "DANGER"),
            new CryptoProblem(Arrays.asList("COCA", "COLA"), "OASIS"),
            new CryptoProblem(Arrays.asList("DONALD", "GERALD"), "ROBERT")
    );

    private static final String[] PROBLEM_NAMES = {
            "SEND+MORE=MONEY", "EAT+THAT=APPLE", "CROSS+ROADS=DANGER",
            "COCA+COLA=OASIS", "DONALD+GERALD=ROBERT"
    };

    private static final class Variacao {
        final String id;
        final String descricao;
        final int popSize;
        final int maxGen;
        final double mutationRate;
        final double crossoverRate;
        final double elitismRate;
        final FitnessEvaluator evaluator;
        final SelectionStrategy selection;
        final CrossoverStrategy crossover;
        final MutationStrategy mutation;

        Variacao(String id, String descricao, int popSize, int maxGen, double mutationRate,
                 double crossoverRate, double elitismRate, FitnessEvaluator evaluator,
                 SelectionStrategy selection, CrossoverStrategy crossover, MutationStrategy mutation) {
            this.id = id;
            this.descricao = descricao;
            this.popSize = popSize;
            this.maxGen = maxGen;
            this.mutationRate = mutationRate;
            this.crossoverRate = crossoverRate;
            this.elitismRate = elitismRate;
            this.evaluator = evaluator;
            this.selection = selection;
            this.crossover = crossover;
            this.mutation = mutation;
        }
    }

    public static void main(String[] args) {
        System.out.println("=====================================================================================");
        System.out.println("              Iniciando Etapa 3: Generalizacao e Erro Posicional (30.000 runs)");
        System.out.println("=====================================================================================");

        Variacao[] variacoes = {

                new Variacao("V0", "Baseline Etapa 3 (V15)", 100, 75, 0.50, 0.80, 0.20,
                        new GlobalDifferenceFitness(), new RouletteSelectionAggressive(),
                        new PmxCrossover(), new SwapMutation()),

                new Variacao("V1", "PositionalErrorFitness", 100, 75, 0.50, 0.80, 0.20,
                        new PositionalErrorFitness(), new RouletteSelectionAggressive(),
                        new PmxCrossover(), new SwapMutation()),

                new Variacao("V2", "PositionalError + Mut 60%", 100, 75, 0.60, 0.80, 0.20,
                        new PositionalErrorFitness(), new RouletteSelectionAggressive(),
                        new PmxCrossover(), new SwapMutation()),

                new Variacao("V3", "PositionalError + Tournament", 100, 75, 0.50, 0.80, 0.20,
                        new PositionalErrorFitness(), new TournamentSelection(3),
                        new PmxCrossover(), new SwapMutation()),

                new Variacao("V4", "PositionalError + CyclicCross", 100, 75, 0.50, 0.80, 0.20,
                        new PositionalErrorFitness(), new RouletteSelectionAggressive(),
                        new CyclicCrossover(), new SwapMutation()),
                
                new Variacao("V5", "Global + Geracoes 110", 100, 110, 0.50, 0.80, 0.20,
                        new GlobalDifferenceFitness(), new RouletteSelectionAggressive(),
                        new PmxCrossover(), new SwapMutation()),
 
                new Variacao("V6", "Global + Pop 150", 150, 75, 0.50, 0.80, 0.20,
                        new GlobalDifferenceFitness(), new RouletteSelectionAggressive(),
                        new PmxCrossover(), new SwapMutation()),
 
                new Variacao("V7", "Global + Pop 200 + Ger 100", 200, 100, 0.50, 0.80, 0.20,
                        new GlobalDifferenceFitness(), new RouletteSelectionAggressive(),
                        new PmxCrossover(), new SwapMutation())
        };

        double baselineTimeMs = -1.0;

        try (PrintWriter writerProblema = new PrintWriter(new FileWriter("resultados_etapa3_por_problema.csv"));
             PrintWriter writerMedia = new PrintWriter(new FileWriter("resultados_etapa3.csv"))) {

            writerProblema.println("ID,Descricao,Problema,Convergencias,TempoMedio_ms,TaxaConvergencia");
            writerMedia.println("ID,Descricao,PopSize,Geracoes,TaxaMutacao,TaxaCrossover,Elitismo,"
                    + "Convergencias,TempoMedio_ms,TaxaConvergencia,AcrescimoTempo_pct");

            for (int variationIndex = 0; variationIndex < variacoes.length; variationIndex++) {
                Variacao v = variacoes[variationIndex];
                System.out.printf("Variacao %-4s | %-30s |%n", v.id, v.descricao);

                long totalTimeNsGeral = 0L;
                int convergencesGeral = 0;

                for (int problemIndex = 0; problemIndex < NUM_PROBLEMS; problemIndex++) {
                    CryptoProblem problem = PROBLEMS.get(problemIndex);
                    long totalTimeNsProblema = 0L;
                    int convergencesProblema = 0;

                    for (int executionIndex = 0; executionIndex < NUM_EXECUTIONS; executionIndex++) {
                        Random runRng = new Random(seedFor(variationIndex, problemIndex, executionIndex));
                        GeneticAlgorithm ga = new GeneticAlgorithm(
                                problem, v.popSize, v.maxGen, v.crossoverRate, v.mutationRate,
                                v.evaluator, v.selection, v.crossover, v.mutation,
                                new ElitismReplacement(v.elitismRate), runRng);

                        EvolutionResult result = ga.evolve();
                        totalTimeNsProblema += result.getExecutionTimeNs();
                        
                        if (result.isConverged()) convergencesProblema++;
                    }

                    double avgTimeMsProblema = (totalTimeNsProblema / (double) NUM_EXECUTIONS) / 1_000_000.0;
                    double convRateProblema = (convergencesProblema / (double) NUM_EXECUTIONS) * 100.0;

                    System.out.printf("   %-22s -> Conv: %5.1f%% | Tempo: %5.2f ms%n",
                            PROBLEM_NAMES[problemIndex], convRateProblema, avgTimeMsProblema);

                    writerProblema.printf(Locale.US, "%s,%s,%s,%d,%.4f,%.1f%n",
                            v.id, v.descricao, PROBLEM_NAMES[problemIndex],
                            convergencesProblema, avgTimeMsProblema, convRateProblema);

                    totalTimeNsGeral += totalTimeNsProblema;
                    convergencesGeral += convergencesProblema;
                }

                int totalExecutions = NUM_PROBLEMS * NUM_EXECUTIONS;
                double avgTimeMs = (totalTimeNsGeral / (double) totalExecutions) / 1_000_000.0;
                double convergenceRate = (convergencesGeral / (double) totalExecutions) * 100.0;

                if ("V0".equals(v.id)) baselineTimeMs = avgTimeMs;
                double acrescimoPct = baselineTimeMs > 0.0
                        ? ((avgTimeMs - baselineTimeMs) / baselineTimeMs) * 100.0 : 0.0;

                String alerta = acrescimoPct > 50.0 ? "  [ESTOUROU +50%]" : "";
                System.out.printf("   >> MEDIA -> Conv: %5.1f%% | Tempo: %5.2f ms | dT: %+5.1f%%%s%n%n",
                        convergenceRate, avgTimeMs, acrescimoPct, alerta);

                writerMedia.printf(Locale.US, "%s,%s,%d,%d,%.2f,%.2f,%.2f,%d,%.4f,%.1f,%.1f%n",
                        v.id, v.descricao, v.popSize, v.maxGen, v.mutationRate,
                        v.crossoverRate, v.elitismRate, convergencesGeral, avgTimeMs,
                        convergenceRate, acrescimoPct);
            }

            System.out.println("=====================================================================================");
            System.out.println("   Etapa 3 concluida!");
            System.out.println("   - resultados_etapa3.csv             (media por variacao)");
            System.out.println("   - resultados_etapa3_por_problema.csv (detalhe por problema)");
            System.out.println("=====================================================================================");

        } catch (Exception e) {
            System.err.println("Erro critico de I/O ao gravar o CSV: " + e.getMessage());
        }
    }

    private static long seedFor(int variationIndex, int problemIndex, int executionIndex) {
        return BASE_SEED + (variationIndex * 100_000L) + (problemIndex * 10_000L) + executionIndex;
    }
}