package br.ufu.computacaoevolutiva.engine;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import br.ufu.computacaoevolutiva.domain.fitness.FitnessEvaluator;
import br.ufu.computacaoevolutiva.domain.fitness.GlobalDifferenceFitness;
import br.ufu.computacaoevolutiva.domain.model.CryptoProblem;
import br.ufu.computacaoevolutiva.domain.operators.ElitismReplacement;
import br.ufu.computacaoevolutiva.domain.operators.PmxCrossover;
import br.ufu.computacaoevolutiva.domain.operators.RouletteSelectionAggressive;
import br.ufu.computacaoevolutiva.domain.operators.SwapMutation;

/**
 * Etapa 2 - Refinamento (tuning) da melhor configuracao da Etapa 1.
 *
 * Ponto de partida (Config 16 vencedora da Etapa 1):
 *   Selecao    = Roleta (agressiva / inversao linear)
 *   Crossover  = PMX
 *   Reinsercao = Elitismo 20%
 *   Mutacao    = SwapMutation, taxa 20%
 *   Crossover rate = 80%
 *   Populacao  = 100, Geracoes = 50
 *   -> ~51,2% de convergencia, ~2,12 ms
 *
 * Cada variacao altera UM eixo por vez (exceto as combinadas V9-V11), para
 * isolar o efeito de cada parametro.
 */

public class ExperimentRunnerEtapa2 {

    private static final long BASE_SEED = 2026L;

    // Parametros fixos herdados da Config 16
    private static final FitnessEvaluator EVALUATOR = new GlobalDifferenceFitness();
    private static final int NUM_EXECUTIONS = 1000;

    private static class Variacao {
        final String id;
        final String descricao;
        final int popSize;
        final int maxGen;
        final double mutationRate;
        final double crossoverRate;
        final double elitismRate;

        Variacao(String id, String descricao, int popSize, int maxGen,
                 double mutationRate, double crossoverRate, double elitismRate) {
            this.id = id;
            this.descricao = descricao;
            this.popSize = popSize;
            this.maxGen = maxGen;
            this.mutationRate = mutationRate;
            this.crossoverRate = crossoverRate;
            this.elitismRate = elitismRate;
        }
    }

    public static void main(String[] args) {
        System.out.println("=====================================================================================");
        System.out.println("              Iniciando Etapa 2: Tuning sobre a Config 16 (12 variacoes)  ");
        System.out.println("=====================================================================================");

        CryptoProblem problem = new CryptoProblem(Arrays.asList("SEND", "MORE"), "MONEY");

        // V0 = baseline (Config 16). Demais variam um eixo por vez; V9-V11 combinam eixos.
        Variacao[] variacoes = {
            new Variacao("V0",  "Baseline (Config 16)",   100,  50, 0.20, 0.80, 0.20),
            new Variacao("V1",  "Geracoes 75",            100,  75, 0.20, 0.80, 0.20),
            new Variacao("V2",  "Geracoes 100",           100, 100, 0.20, 0.80, 0.20),
            new Variacao("V3",  "Populacao 150",          150,  50, 0.20, 0.80, 0.20),
            new Variacao("V4",  "Populacao 200",          200,  50, 0.20, 0.80, 0.20),
            new Variacao("V5",  "Mutacao 30%",            100,  50, 0.30, 0.80, 0.20),
            new Variacao("V6",  "Mutacao 40%",            100,  50, 0.40, 0.80, 0.20),
            new Variacao("V7",  "Elitismo 10%",           100,  50, 0.20, 0.80, 0.10),
            new Variacao("V8",  "Elitismo 30%",           100,  50, 0.20, 0.80, 0.30),
            new Variacao("V9",  "Pop150 + Gen75",         150,  75, 0.20, 0.80, 0.20),
            new Variacao("V10", "Pop150 + Mut30%",        150,  50, 0.30, 0.80, 0.20),
            new Variacao("V11", "Gen75 + Mut30%",         100,  75, 0.30, 0.80, 0.20),
            new Variacao("V12", "Mutacao 50%",            100,  50, 0.50, 0.80, 0.20),
            new Variacao("V13", "Mut40% + Elit10%",       100,  50, 0.40, 0.80, 0.10),
            new Variacao("V14", "Mut40% + Gen75",         100,  75, 0.40, 0.80, 0.20),
            new Variacao("V15", "Mut50% + Gen75",         100,  75, 0.50, 0.80, 0.20),
        };

        double baselineTimeMs = -1;

        try (PrintWriter writer = new PrintWriter(new FileWriter("resultados_etapa2.csv"))) {
            writer.println("ID,Descricao,PopSize,Geracoes,TaxaMutacao,TaxaCrossover,Elitismo,"
                    + "Convergencias,TempoMedio_ms,TaxaConvergencia,AcrescimoTempo_pct");

            int idx = 0;
            for (Variacao v : variacoes) {
                System.out.printf("Variacao %-4s | %-22s | pop=%3d gen=%3d mut=%.2f elit=%.2f -> ",
                        v.id, v.descricao, v.popSize, v.maxGen, v.mutationRate, v.elitismRate);

                int convergences = 0;
                long totalTimeNs = 0;

                for (int i = 0; i < NUM_EXECUTIONS; i++) {
                    Random runRng = new Random(seedFor(idx, i));
                    GeneticAlgorithm ga = new GeneticAlgorithm(
                            problem, v.popSize, v.maxGen, v.crossoverRate, v.mutationRate,
                            EVALUATOR,
                            new RouletteSelectionAggressive(),
                            new PmxCrossover(),
                            new SwapMutation(),
                            new ElitismReplacement(v.elitismRate),
                            runRng
                    );
                    EvolutionResult result = ga.evolve();
                    if (result.isConverged()) 
                        convergences++;
                    
                    totalTimeNs += result.getExecutionTimeNs();
                }

                double avgTimeMs = (totalTimeNs / (double) NUM_EXECUTIONS) / 1_000_000.0;
                double convergenceRate = (convergences / (double) NUM_EXECUTIONS) * 100.0;

                if (v.id.equals("V0")) 
                    baselineTimeMs = avgTimeMs;
                
                double acrescimoPct = baselineTimeMs > 0
                        ? ((avgTimeMs - baselineTimeMs) / baselineTimeMs) * 100.0
                        : 0.0;

                String alerta = acrescimoPct > 50.0 ? "  [ESTOUROU +50%]" : "";
                System.out.printf("Conv: %5.1f%% | Tempo: %5.2f ms | dT: %+5.1f%%%s%n",
                        convergenceRate, avgTimeMs, acrescimoPct, alerta);

                writer.printf(Locale.US, "%s,%s,%d,%d,%.2f,%.2f,%.2f,%d,%.4f,%.1f,%.1f%n",
                        v.id, v.descricao, v.popSize, v.maxGen, v.mutationRate,
                        v.crossoverRate, v.elitismRate, convergences, avgTimeMs,
                        convergenceRate, acrescimoPct);

                idx++;
            }

            System.out.println("=====================================================================================");
            System.out.println("                Etapa 2 concluida! Relatorio: resultados_etapa2.csv  ");
            System.out.println("                Teto de tempo: +50% sobre o baseline (V0)            ");
            System.out.println("=====================================================================================");

        } catch (Exception e) {
            System.err.println("Erro critico de I/O ao gravar o CSV: " + e.getMessage());
        }
    }

    /**
     * Seeds isoladas por variacao. Usa offset alto para nao colidir com as
     * seeds da Etapa 1 (que usava configId * 10_000).
     */
    private static long seedFor(int variationIndex, int executionIndex) {
        return BASE_SEED + 500_000L + (variationIndex * 10_000L) + executionIndex;
    }
}