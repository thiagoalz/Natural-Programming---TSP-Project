package buscaLocal;

import java.util.List;

import org.jgap.Gene;
import org.jgap.GeneticOperator;
import org.jgap.IChromosome;
import org.jgap.Population;

import controle.GA.GeneTSP;

public class BuscaLocalTroca implements GeneticOperator{
	//private int tamanho;

	public BuscaLocalTroca(){
		//this.tamanho=tamanho;
	}

	public void operate(Population a_population, List a_candidateChromosomes) {
		//Loop executando a busca em todos os indivíduos da população
//		-----------------------------------------------------------------
		int len = a_population.size();
		for ( int i = 0; i < len; i++ )
		{
			IChromosome original=a_population.getChromosome(i);
			IChromosome clone=(IChromosome)original.clone();
			IChromosome best= executaBusca(clone);
			a_candidateChromosomes.add( best );
		}

	}

	private IChromosome executaBusca(IChromosome chromosome) {				
		IChromosome best=chromosome;		
		
		for(int i=1;i<chromosome.getGenes().length-1;i++){//Não mexe no primeiro!
			IChromosome novo=(IChromosome)best.clone();
			Gene[] genes= novo.getGenes();
			
			
			GeneTSP atual=(GeneTSP)genes[i];
			GeneTSP prox=(GeneTSP)genes[i+1];
			GeneTSP aux=null;
			
			//troca			
			aux=atual;
			genes[i]=prox;
			genes[i+1]=aux;
									
			if(novo.getFitnessValue()>best.getFitnessValue()){
				best=novo;
				break;
			}	
		}
		
		return best;
	}
	
	/*
	 * private IChromosome executaBusca(IChromosome chromosome) {				
		IChromosome best=chromosome;		
		
		for(int i=0;i<chromosome.getGenes().length;i++){
			IChromosome novo=(IChromosome)best.clone();
			Gene[] genes= novo.getGenes();
			
			
			Alelo atual=(Alelo)genes[i].getAllele();
			Alelo prox=(Alelo)genes[(i+1)%genes.length].getAllele();
			Alelo aux=null;
			
			//troca			
			aux=atual;
			genes[i].setAllele(prox);
			genes[(i+1)%genes.length].setAllele(aux);
									
			if(novo.getFitnessValue()>best.getFitnessValue()){
				best=novo;
				System.out.println("MELHOROU");
			}else if(novo.getFitnessValue()==best.getFitnessValue()){
				System.out.println("IGUAL");
			}else{
				System.out.println("PIOROU");
			}
		}
		
		return best;
	}
	 * 
	 */

	//public int getTamanho() {
		//return tamanho;
	//}

}
