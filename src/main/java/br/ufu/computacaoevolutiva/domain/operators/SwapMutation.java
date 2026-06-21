package br.ufu.computacaoevolutiva.domain.operators;

import java.util.Random;
import br.ufu.computacaoevolutiva.domain.model.Individual;

public class SwapMutation implements MutationStrategy {

    @Override
    public void mutate(Individual individual, Random random) {
        int[] genes = individual.getGenes();
        int length = genes.length;

        int pos1 = random.nextInt(length);
        int pos2 = random.nextInt(length);

        // Garante que as posições são diferentes para que a mutação realmente ocorra
        while (pos1 == pos2) 
            pos2 = random.nextInt(length);
        
        // Realiza o swap (troca) in-place em O(1)
        int temp = genes[pos1];
        genes[pos1] = genes[pos2];
        genes[pos2] = temp;
        
        // Como o genótipo mudou, o indivíduo deve ser reavaliado na próxima geração
        // Para uma arquitetura purista, o ideal seria resetar a flag isEvaluated, 
        // mas como a engine vai recalcular todos os mutantes, o swap in-place é suficiente.
    }
}