package br.ufu.computacaoevolutiva.domain.operators;

import java.util.Arrays;
import java.util.Random;
import br.ufu.computacaoevolutiva.domain.model.Individual;

public class PmxCrossover implements CrossoverStrategy {

    @Override
    public Individual[] crossover(Individual parent1, Individual parent2, Random random) {
        int length = parent1.getGenes().length;
        
        // 1. Sorteia os dois pontos de corte
        int point1 = random.nextInt(length);
        int point2 = random.nextInt(length);
        
        if (point1 > point2) {
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }

        return new Individual[]{
            generateChild(parent1.getGenes(), parent2.getGenes(), point1, point2, length),
            generateChild(parent2.getGenes(), parent1.getGenes(), point1, point2, length)
        };
    }

    private Individual generateChild(int[] p1, int[] p2, int start, int end, int length) {
        int[] childGenes = new int[length];
        Arrays.fill(childGenes, -1);
        
        // Array de mapeamento rápido O(1). O índice é o valor antigo, o conteúdo é o novo valor.
        int[] mapping = new int[10];
        Arrays.fill(mapping, -1);

        // 2. Copia a seção interna do Pai 1 para o Filho
        for (int i = start; i <= end; i++) {
            childGenes[i] = p1[i];
            mapping[p1[i]] = p2[i]; // Registra quem substituiu quem
        }

        // 3. Preenche o restante com o Pai 2, resolvendo conflitos pelo mapa
        for (int i = 0; i < length; i++) {
            if (i >= start && i <= end) continue; // Pula a seção central que já foi copiada

            int candidate = p2[i];
            
            // Se o candidato já está no filho (conflito), navegamos pelo mapa até achar um livre
            while (contains(childGenes, start, end, candidate)) 
                candidate = mapping[candidate];
            childGenes[i] = candidate;
        }

        return new Individual(childGenes);
    }

    // Verifica se o valor existe apenas na seção copiada (swath)
    private boolean contains(int[] array, int start, int end, int value) {
        for (int i = start; i <= end; i++) 
            if (array[i] == value) return true;
        
        return false;
    }
}