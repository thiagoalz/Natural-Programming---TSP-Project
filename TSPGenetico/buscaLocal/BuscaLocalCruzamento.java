package buscaLocal;

import java.util.List;

import org.jgap.BaseChromosome;
import org.jgap.Gene;
import org.jgap.GeneticOperator;
import org.jgap.IChromosome;
import org.jgap.Population;

import controle.GA.GeneTSP;

/**
 * Busca local que tenta detectar e remover cruzamento de linhas
 * @author thiago
 *
 */
public class BuscaLocalCruzamento implements GeneticOperator{

	public BuscaLocalCruzamento(){
	}

	public void operate(Population a_population, List a_candidateChromosomes,boolean stopOnRight) {
//		Loop executando a busca em todos os indivíduos da população
		int len = a_population.size();
		for ( int i = 0; i < len; i++ )
		{
			IChromosome original=a_population.getChromosome(i);
			IChromosome clone=(IChromosome)original.clone();
			IChromosome best= executaBusca(clone,stopOnRight);
			a_candidateChromosomes.add( best );
		}

	}
	
	public void operate(Population a_population, List a_candidateChromosomes) {
		operate(a_population,a_candidateChromosomes,true);
	}


	private IChromosome executaBusca(IChromosome chromosome,boolean stopOnRight) {							
		boolean acertou=false;
		
		Gene[] genes= chromosome.getGenes();
		//Compara todas as arestas com todas
		//Exclui os vizinhos diretos, já que esses não podem se cruzar
		for(int i=0;i<chromosome.getGenes().length-3;i++){//Vai até a ante-penultima aresta
			
			if(stopOnRight && acertou){
				break;
			}
			//Extremos aresta A
			GeneTSP a1=(GeneTSP)genes[i];
			GeneTSP a2=(GeneTSP)genes[i+1];
			
			for(int j=i+2;j<chromosome.getGenes().length-1;j++){//Pula o vizinho direto e vai até o fim
				if(stopOnRight && acertou){
					break;
				}
				
				//Extremos aresta B
				GeneTSP b1=(GeneTSP)genes[j];
				GeneTSP b2=(GeneTSP)genes[j+1];
				
				//verifica se as arestas se cruzam
				if( CruzamentoDeLinhas.INTERSECTION==CruzamentoDeLinhas.cruzamento(a1.getPos_x(), a1.getPos_y(), a2.getPos_x(), a2.getPos_y(), b1.getPos_x(), b1.getPos_y(), b2.getPos_x(), b2.getPos_y())){
					//Inverte a posicao das arestas (sem modificar o resto)
					//i+1=a2
					//j=b1
					this.inverter(chromosome,i+1,j);	
					acertou=true;
				}
			}
			
		}
		
		return chromosome;
	}

	private void inverter(IChromosome chromosome, int primeiro, int segundo) {
		Gene[] genes= chromosome.getGenes();
		
		//Inverte os numeros
		int tamanho=(segundo-primeiro)+1;
		for(int i=0;i<(tamanho/2)-1;i++){//Percorre até a metade trocando com o valor da posição simétrica.
			//troca
			Gene aux=chromosome.getGene(primeiro+i);
			genes[primeiro+i]=genes[segundo-i];
			genes[segundo-i]=aux;
			
		}
		
	}

	public void operate(IChromosome bestSolutionSoFar,boolean stopOnRight) {
		this.executaBusca(bestSolutionSoFar,stopOnRight);		
	}

}
