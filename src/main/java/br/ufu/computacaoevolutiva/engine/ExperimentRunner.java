package br.ufu.computacaoevolutiva.engine;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import br.ufu.computacaoevolutiva.domain.fitness.FitnessEvaluator;
import br.ufu.computacaoevolutiva.domain.fitness.GlobalDifferenceFitness;
import br.ufu.computacaoevolutiva.domain.model.CryptoProblem;
import br.ufu.computacaoevolutiva.domain.operators.*;

public class ExperimentRunner {

    public static void main(String[] args) {
        System.out.println("=====================================================================================");
        System.out.println("              Iniciando Etapa 1: Algoritmo Genético (16.000 runs)  ");
        System.out.println("=====================================================================================");

        // 1. Definição do Problema Base e Avaliador [cite: 9, 12, 33]
        CryptoProblem problem = new CryptoProblem(Arrays.asList("SEND", "MORE"), "MONEY");
        FitnessEvaluator evaluator = new GlobalDifferenceFitness();

        // 2. Parâmetros Fixos [cite: 13, 14, 21]
        int popSize = 100;
        int maxGen = 50;
        int numExecutions = 1000;

        // 3. Pipeline de Dados: Preparando o CSV para o Power BI 
        try (PrintWriter writer = new PrintWriter(new FileWriter("resultados_etapa1.csv"))) {
            
            // Cabeçalho do CSV
            writer.println("ID,TaxaMutacao,Selecao,Crossover,Reinsercao,Convergencias,TempoMedio_ms,TaxaConvergencia");

            int configId = 1;
            double[] mutationRates = {0.1, 0.2}; // TM1 e TM2 [cite: 17]
            
            // 4. Matriz de Experimentos (16 combinações)
            for (double tm : mutationRates) {
                for (int s = 1; s <= 2; s++) { // 1 = Torneio, 2 = Roleta [cite: 18]
                    for (int c = 1; c <= 2; c++) { // 1 = Cíclico, 2 = PMX [cite: 19]
                        for (int r = 1; r <= 2; r++) { // 1 = Ordenada, 2 = Elitismo 

                            // Regra de Negócio: Se for Elitismo, Crossover é 80%. Senão, 60%. [cite: 15, 20]
                            double cr = (r == 2) ? 0.8 : 0.6; 

                            // Instanciando as estratégias via Factory In-line
                            SelectionStrategy selection = (s == 1) ? new TournamentSelection(3) : new RouletteSelection();
                            CrossoverStrategy crossover = (c == 1) ? new CyclicCrossover() : new PmxCrossover();
                            ReplacementStrategy replacement = (r == 1) ? new OrderedReplacement() : new ElitismReplacement(0.2);

                            // Labels para o log visual e CSV
                            String sLabel = (s == 1) ? "Torneio(3)" : "Roleta";
                            String cLabel = (c == 1) ? "Ciclico" : "PMX";
                            String rLabel = (r == 1) ? "Ordenada" : "Elitismo(20%)";

                            System.out.printf("Config %02d/16 | TM: %.1f | S: %-10s | C: %-7s | R: %-13s -> ", 
                                configId, tm, sLabel, cLabel, rLabel);

                            int convergences = 0;
                            long totalTimeNs = 0;

                            Random masterRng = new Random(2026); // seed para que o resultado da executação seja sempre o mesmo
                            // Loop de 1.000 execuções para a configuração atual 
                            for (int i = 0; i < numExecutions; i++) {
                                GeneticAlgorithm ga = new GeneticAlgorithm(
                                    problem, popSize, maxGen, cr, tm, 
                                    evaluator, selection, crossover, new SwapMutation(), replacement, masterRng
                                );

                                EvolutionResult result = ga.evolve();
                                
                                if (result.isConverged()) {
                                    convergences++;
                                }
                                totalTimeNs += result.getExecutionTimeNs();
                            }

                            // 5. Cálculo de Métricas [cite: 22, 23]
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
            System.out.println("                Experimentos concluídos com sucesso!                 ");
            System.out.println("                Relatório exportado para: resultados_etapa1.csv      ");
            System.out.println("=====================================================================================");
            
        } catch (Exception e) {
            System.err.println("Erro crítico de I/O ao gravar o arquivo CSV: " + e.getMessage());
        }
    }
}