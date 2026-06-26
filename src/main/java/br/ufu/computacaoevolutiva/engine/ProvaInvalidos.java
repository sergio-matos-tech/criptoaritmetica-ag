package br.ufu.computacaoevolutiva.engine;

import br.ufu.computacaoevolutiva.domain.fitness.*;
import br.ufu.computacaoevolutiva.domain.model.*;
import br.ufu.computacaoevolutiva.domain.operators.*;
import java.util.*;

public class ProvaInvalidos {
    // Fitness SEM penalidade: so o erro global, sem checar zero a esquerda.
    static class FitnessSemPenalidade implements FitnessEvaluator {
        public void evaluate(Individual ind, CryptoProblem p) {
            int[] genes = ind.getGenes();
            int[] w = p.getWeights();
            long error = 0;
            for (int i = 0; i < w.length; i++) error += (long) w[i] * genes[i];
            long fit = Math.abs(error);
            if (fit > Integer.MAX_VALUE) fit = Integer.MAX_VALUE;
            ind.setFitness((int) fit);
        }
    }

    public static void main(String[] a) {
        CryptoProblem p = new CryptoProblem(Arrays.asList("SEND","MORE"),"MONEY");
        char[] letras = p.getUniqueLetters();
        FitnessEvaluator semPen = new FitnessSemPenalidade();

        int N = 1000;
        int convergiu = 0, validas = 0, invalidas = 0;
        Map<String,Integer> solucoesInvalidas = new HashMap<>();
        String solValida = null;

        for (int i = 0; i < N; i++) {
            Random r = new Random(2026L + 500000L + 15*10000L + i);
            GeneticAlgorithm ga = new GeneticAlgorithm(p, 100, 50, 0.80, 0.20, semPen,
                new RouletteSelectionAggressive(), new PmxCrossover(), new SwapMutation(),
                new ElitismReplacement(0.20), r);
            EvolutionResult res = ga.evolve();
            if (!res.isConverged()) continue;
            convergiu++;

            int[] g = res.getBestSolution().getGenes();
            boolean temZeroEsquerda = p.countInvalidLeadingZeros(g) > 0;

            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < letras.length; k++)
                sb.append(letras[k]).append("=").append(g[k]).append(" ");
            String sol = sb.toString().trim();

            if (temZeroEsquerda) {
                invalidas++;
                solucoesInvalidas.merge(sol, 1, Integer::sum);
            } else {
                validas++;
                solValida = sol;
            }
        }

        System.out.println("=== Config 16 SEM penalidade, " + N + " execucoes ===");
        System.out.printf("Convergencias (fitness 0): %d (%.1f%%)%n", convergiu, 100.0*convergiu/N);
        System.out.printf("  VALIDAS (sem zero a esquerda): %d (%.1f%%)%n", validas, 100.0*validas/N);
        System.out.printf("  INVALIDAS (com zero a esquerda): %d (%.1f%%)%n", invalidas, 100.0*invalidas/N);
        System.out.println();
        System.out.println("Solucao valida: " + solValida);
        System.out.println();
        System.out.println("Top solucoes INVALIDAS contadas como 'convergencia':");
        solucoesInvalidas.entrySet().stream()
            .sorted((x,y) -> y.getValue() - x.getValue())
            .limit(8)
            .forEach(e -> System.out.printf("   %-45s (%d vezes)%n", e.getKey(), e.getValue()));
        System.out.println();
        System.out.printf(">>> Das %d 'convergencias', %.1f%% sao INVALIDAS%n",
            convergiu, 100.0 * invalidas / convergiu);
    }
}