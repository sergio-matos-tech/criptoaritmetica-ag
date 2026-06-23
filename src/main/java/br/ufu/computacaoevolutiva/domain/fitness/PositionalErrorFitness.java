package br.ufu.computacaoevolutiva.domain.fitness;

import java.util.List;

import br.ufu.computacaoevolutiva.domain.model.CryptoProblem;
import br.ufu.computacaoevolutiva.domain.model.Individual;

/**
 * Fitness baseado em erro posicional por coluna, da direita para a esquerda.
 * Mantem a mesma semantica de minimizacao: quanto menor o erro acumulado, melhor.
 */
public class PositionalErrorFitness implements FitnessEvaluator {

    // REDUZIDO: Como o erro posicional base varia de 0 a ~50, 
    // uma penalidade de 100,000 esmagava as fatias de probabilidade da Roleta Agressiva.
    // 100L e suficiente para punir sem destruir a pressão seletiva.
    private static final long LEADING_ZERO_PENALTY = 100L;

    @Override
    public void evaluate(Individual individual, CryptoProblem problem) {
        int[] genes = individual.getGenes();
        List<String> addends = problem.getAddends();
        String result = problem.getResult();
        char[] uniqueLetters = problem.getUniqueLetters();

        long error = 0L;
        int carry = 0;

        int maxAddendLength = 0;
        for (String word : addends) {
            if (word.length() > maxAddendLength) {
                maxAddendLength = word.length();
            }
        }

        int maxColumns = Math.max(maxAddendLength, result.length());
        
        for (int column = 0; column < maxColumns; column++) {
            int columnSum = carry;
            for (String word : addends) {
                columnSum += digitAtFromRight(word, column, genes, uniqueLetters);
            }

            int expectedDigit = digitAtFromRight(result, column, genes, uniqueLetters);
            int unitDigit = columnSum % 10;
            
            // O erro e a distancia modular entre os digitos calculados na coluna
            error += Math.abs(unitDigit - expectedDigit);
            carry = columnSum / 10;
        }

        // Se sobrou carry ao final de todas as colunas
        while (carry > 0) {
            error += carry % 10;
            carry /= 10;
        }

        int invalidLeadingZeros = problem.countInvalidLeadingZeros(genes);
        if (invalidLeadingZeros > 0) {
            error += LEADING_ZERO_PENALTY * invalidLeadingZeros;
        }

        individual.setFitness((int) error);
    }

    // OTIMIZADO: Removido o uso de 'indexOf' para evitar complexidade O(N) desnecessaria
    // na pilha de chamadas da CPU por gene avaliado, garantindo tempos de ms baixos.
    private int digitAtFromRight(String word, int column, int[] genes, char[] uniqueLetters) {
        int charIndex = word.length() - 1 - column;
        if (charIndex < 0) {
            return 0;
        }
        char letter = word.charAt(charIndex);
        
        for (int i = 0; i < uniqueLetters.length; i++) {
            if (uniqueLetters[i] == letter) {
                return genes[i];
            }
        }
        return 0;
    }
}