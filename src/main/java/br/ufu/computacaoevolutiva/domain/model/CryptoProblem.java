package br.ufu.computacaoevolutiva.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa a equação criptoaritmética.
 * Pré-calcula os pesos base-10 de cada letra para otimizar o cálculo do fitness em O(1).
 */
public class CryptoProblem {
    private final char[] uniqueLetters;
    private final int[] weights;

    public CryptoProblem(List<String> addends, String result) {
        Map<Character, Integer> weightMap = new HashMap<>();

        // Processa as parcelas da soma (pesos positivos)
        for (String word : addends) 
            processWord(word, weightMap, 1);
        
        // Processa o resultado (pesos negativos)
        processWord(result, weightMap, -1);

        // Um problema em base 10 só comporta de 0 a 9.
        if (weightMap.size() > 10) 
            throw new IllegalArgumentException("Problema inválido: Contém mais de 10 letras únicas.");
        
        this.uniqueLetters = new char[weightMap.size()];
        this.weights = new int[weightMap.size()];

        int index = 0;
        for (Map.Entry<Character, Integer> entry : weightMap.entrySet()) {
            this.uniqueLetters[index] = entry.getKey();
            this.weights[index] = entry.getValue();
            index++;
        }
    }

    private void processWord(String word, Map<Character, Integer> weightMap, int sign) {
        int length = word.length();
        for (int i = 0; i < length; i++) {
            char c = word.charAt(i);
            // Calcula a grandeza da letra. Ex: na palavra SEND, o S vale 1000 (10^3).
            int positionWeight = (int) Math.pow(10, length - 1 - i);
            weightMap.put(c, weightMap.getOrDefault(c, 0) + (positionWeight * sign));
        }
    }

    public char[] getUniqueLetters() {
        return uniqueLetters;
    }

    public int[] getWeights() {
        return weights;
    }
}