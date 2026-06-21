package br.ufu.computacaoevolutiva.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class CryptoProblemTest {

    @Test
    void testSendMoreMoneyWeights() {
        CryptoProblem problem = new CryptoProblem(
            Arrays.asList("SEND", "MORE"), 
            "MONEY"
        );

        char[] letters = problem.getUniqueLetters();
        int[] weights = problem.getWeights();

        // Act & Assert
        // Letras esperadas na equação SEND + MORE = MONEY (8 letras únicas)
        assertEquals(8, letters.length, "O problema SEND + MORE = MONEY deve ter exatamente 8 letras únicas.");

        // Verifica o peso consolidado de cada letra de acordo com nossa prova matemática
        assertEquals(1000, getWeightForLetter(letters, weights, 'S'));
        assertEquals(91, getWeightForLetter(letters, weights, 'E')); // 100 (SEND) + 1 (MORE) - 10 (MONEY)
        assertEquals(-90, getWeightForLetter(letters, weights, 'N')); // 10 (SEND) - 100 (MONEY)
        assertEquals(1, getWeightForLetter(letters, weights, 'D'));
        assertEquals(-9000, getWeightForLetter(letters, weights, 'M')); // 1000 (MORE) - 10000 (MONEY)
        assertEquals(-900, getWeightForLetter(letters, weights, 'O')); // 100 (MORE) - 1000 (MONEY)
        assertEquals(10, getWeightForLetter(letters, weights, 'R'));
        assertEquals(-1, getWeightForLetter(letters, weights, 'Y'));
    }

    @Test
    void testInvalidProblemThrowsException() {
        // Problema fictício com mais de 10 letras únicas
        assertThrows(IllegalArgumentException.class, () -> {
            new CryptoProblem(Arrays.asList("ABCDEF", "GHIJKL"), "MNOPQR");
        }, "Deve lançar exceção se houver mais de 10 letras únicas.");
    }

    // Método auxiliar para buscar o peso associado à letra nos arrays paralelos
    private int getWeightForLetter(char[] letters, int[] weights, char target) {
        for (int i = 0; i < letters.length; i++) {
            if (letters[i] == target) {
                return weights[i];
            }
        }
        fail("Letra " + target + " não encontrada no mapeamento.");
        return 0; 
    }
}
