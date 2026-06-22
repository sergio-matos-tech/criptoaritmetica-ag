package br.ufu.computacaoevolutiva.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representa a equação criptoaritmética.
 * Pré-calcula os pesos base-10 e os índices das letras iniciais para otimização O(1).
 */
public class CryptoProblem {
    private final char[] uniqueLetters;
    private final int[] weights;
    private final int[] leadingLetterIndices; 

    public CryptoProblem(List<String> addends, String result) {
        Map<Character, Integer> weightMap = new HashMap<>();
        Set<Character> leadingChars = new HashSet<>(); 

        // Identifica as letras iniciais das parcelas e processa os pesos
        for (String word : addends) {
            if (!word.isEmpty()) leadingChars.add(word.charAt(0));
            processWord(word, weightMap, 1);
        }
        
        // Identifica a letra inicial do resultado e processa
        if (!result.isEmpty()) leadingChars.add(result.charAt(0));
        processWord(result, weightMap, -1);

        if (weightMap.size() > 10) 
            throw new IllegalArgumentException("Problema inválido: Contém mais de 10 letras únicas.");
        
        this.uniqueLetters = new char[weightMap.size()];
        this.weights = new int[weightMap.size()];
        List<Integer> leadingIndicesList = new ArrayList<>();

        int index = 0;
        for (Map.Entry<Character, Integer> entry : weightMap.entrySet()) {
            this.uniqueLetters[index] = entry.getKey();
            this.weights[index] = entry.getValue();
            
            // Se esta letra for uma inicial, guardamos o índice dela
            if (leadingChars.contains(entry.getKey())) {
                leadingIndicesList.add(index);
            }
            index++;
        }
        
        // Converte a lista para um array primitivo para acesso O(1) rápido
        this.leadingLetterIndices = leadingIndicesList.stream().mapToInt(i -> i).toArray();
    }

    private void processWord(String word, Map<Character, Integer> weightMap, int sign) {
        int length = word.length();
        for (int i = 0; i < length; i++) {
            char c = word.charAt(i);
            int positionWeight = (int) Math.pow(10, length - 1 - i);
            weightMap.put(c, weightMap.getOrDefault(c, 0) + (positionWeight * sign));
        }
    }

    public char[] getUniqueLetters() { return uniqueLetters; }
    public int[] getWeights() { return weights; }

    // Método O(1) para checar a restrição do zero
    public boolean hasInvalidLeadingZero(int[] genes) {
        for (int idx : leadingLetterIndices) {
            if (genes[idx] == 0) return true;
        }
        return false;
    }

    public int countInvalidLeadingZeros(int[] genes) {
        int count = 0;
        for (int idx : leadingLetterIndices) {
            if (genes[idx] == 0) {
                count++;
            }
        }
        return count;
    }
}
