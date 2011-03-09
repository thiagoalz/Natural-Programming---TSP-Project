/*
 * Algoritmo de TSP com GA desenvolvido por Thiago Alvarenga Lechuga para a matéria IA013
 */
package controle.GA;

import java.util.Random;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.NaturalSelector;
import org.jgap.Population;
import org.jgap.impl.GreedyCrossover;
import org.jgap.impl.StockRandomGenerator;
import org.jgap.impl.SwappingMutationOperator;
import org.jgap.impl.TournamentSelector;
import org.jgap.impl.WeightedRouletteSelector;
import org.jgap.impl.salesman.Salesman;

import buscaLocal.BuscaLocalCruzamento;
import buscaLocal.BuscaLocalTroca;


/**
 *
 * Classe Principal que contém toda a configuração do GA Para o TSP
 *
 * @author Thiago Alvarenga Lechuga
 * @since 2.0
 */
public class TSP
extends Salesman {

	public static final int NS_TOURNAMENT=0;
	public static final int NS_ROULETTE=1;

	/** The number of cities to visit*/
	private int cidades = 0;
	private Configuration config;
	private Genotype populacao;

	public TSP(int cidades,int ns,boolean buscaLocal){
		System.out.println("Iniciando Algoritmo....");
		System.out.println(cidades+ " cidades");
		//this.setStartOffset(0);
		this.setCidades(cidades);

		this.configuraGA(ns,buscaLocal);

		this.setDados(null);

	}

	public TSP(float[][] cidades,int ns,boolean buscaLocal){
		System.out.println("Iniciando Algoritmo....");
		System.out.println(cidades.length+ " cidades");
		//this.setStartOffset(0);
		this.setCidades(cidades.length);

		this.configuraGA(ns,buscaLocal);

		this.setDados(cidades);

	}

	private void configuraGA(int ns,boolean buscaLocal){
		try {
			System.out.println("Configurando Algoritmo....");		  
			//Configurando o GA
			this.setConfiguration(createConfiguration(null));
			FitnessFunction myFunc = createFitnessFunction(null);
			this.getConfiguration().setFitnessFunction(myFunc);
			this.getConfiguration().setAlwaysCaculateFitness(true);

			IChromosome sampleChromosome = createSampleChromosome(null);
			this.getConfiguration().setSampleChromosome(sampleChromosome);

			this.getConfiguration().setPopulationSize(getPopulationSize());


			//Seleção
			this.getConfiguration().removeNaturalSelectors(true);
			//this.getConfiguration().setRandomGenerator(new StockRandomGenerator());

			NaturalSelector nselector;
			if(ns==NS_ROULETTE){
				System.out.println("Seleção tipo: Roulette");
				nselector=new WeightedRouletteSelector(this.getConfiguration());
			}else{
				System.out.println("Seleção tipo: Tournament");
				nselector=new TournamentSelector(this.getConfiguration(),2,0.8);
			}

			this.getConfiguration().addNaturalSelector(nselector, false);

			//Operadores
			//config.addGeneticOperator(new GreedyCrossover(config));
			//config.addGeneticOperator(new SwappingMutationOperator(config, 20)); 20->1/20
			if(buscaLocal){
				System.out.println("COM Busca Local");
				this.getConfiguration().addGeneticOperator(new BuscaLocalCruzamento());
			}else{
				System.out.println("SEM Busca Local");
			}

		} catch (InvalidConfigurationException e) {	
			e.printStackTrace();
		}	  
	}

	public void setDados(Object entrada){
		try{
			IChromosome sampleChromosome = createSampleChromosome(entrada);
			IChromosome[] chromosomes =
				new IChromosome[this.getConfiguration().getPopulationSize()];
			Gene[] samplegenes = sampleChromosome.getGenes();
			for (int i = 0; i < chromosomes.length; i++) {
				Gene[] genes = new Gene[samplegenes.length];
				for (int k = 0; k < genes.length; k++) {
					genes[k] = samplegenes[k].newGene();
					genes[k].setAllele(samplegenes[k].getAllele());
				}
				shuffle(genes);
				chromosomes[i] = new Chromosome(this.getConfiguration(), genes);
			}
			Genotype population = new Genotype(this.getConfiguration(),
					new Population(this.getConfiguration(), chromosomes));
			this.setPopulacao(population);
		}catch(Exception e){ e.printStackTrace();}
	}

	/**
	 * Cria um vetor que representará o cromossomo. Seu tamanho é dado pela quantidade de cidades. O primeiro elemento é sempre zero, que é onde o caixeiro inicia a viagem.
	 *
	 * @param a_initial_data ignorado
	 * @return Cromossomo
	 *
	 * @author Thiago Lechuga
	 * @since 2.0
	 */
	public IChromosome createSampleChromosome(Object initial_data) {
		try {
			Random rand=new Random(System.currentTimeMillis());
			GeneTSP[] genes = new GeneTSP[this.getCidades()];
			for (int i = 0; i < genes.length; i++) {
				if(initial_data==null){//dados Randomicos  
					genes[i] = new GeneTSP(getConfiguration(), this.getCidades());                
					genes[i].setAllele(new Alelo(i,rand.nextInt(this.getCidades()),rand.nextInt(this.getCidades()))); // Seta o valor do alelo
				}else{//dados do vetor
					float[][] vetor_entrada=(float[][]) initial_data;
					this.setCidades(vetor_entrada.length);
					genes[i] = new GeneTSP(getConfiguration(), this.getCidades());                
					genes[i].setAllele(new Alelo(i,vetor_entrada[i][0],vetor_entrada[i][1])); // Seta o valor do alelo
				}
			}

			shuffle(genes);// Embaralha os genes
			IChromosome sample = new Chromosome(getConfiguration(), genes);
			//System.out.println("Inicio " + sample);
			//System.out.println("Score " +
			//(Integer.MAX_VALUE / 2 -
			//getConfiguration().getFitnessFunction()
			//.getFitnessValue(sample)));
			return sample;
		}
		catch (InvalidConfigurationException iex) {
			throw new IllegalStateException(iex.getMessage());
		}
	}

	/**
	 * Metodo que calcula a distancia entre dois Genes. A função é dada pela distancia Euclidiana.
	 *
	 * @param a_from primeiro gene, representando uma cidade
	 * @param a_to segundo gene, representando uma cidade
	 * @return a distancia entre duas cidades representadas pelos genes

	 * @author Thiago Lechuga
	 * @since 2.0
	 */
	public double distance(Gene a_from, Gene a_to) {	  
		double distancia=0;

		try{						    		    
			//Função copiada do CONCORDE
			
			GeneTSP a=(GeneTSP)a_from;
			GeneTSP b=(GeneTSP)a_to;
			
			
			double xd = a.getPos_x() - b.getPos_x();
		    double yd = a.getPos_y() - b.getPos_y();
		    double r  = Math.sqrt(xd*xd + yd*yd) + 0.5;

		    return (long) r;			
  
		}catch (Exception e) {
			e.printStackTrace();
		}

		return distancia;
	}

	/**
	 * Função que embaralha os genes
	 */
	protected void shuffle(final Gene[] a_genes) {
		Gene t;
		// shuffle:
		for (int r = 0; r < 10 * a_genes.length; r++) {
			for (int i = getStartOffset(); i < a_genes.length; i++) {
				int p = getStartOffset()
				+ getConfiguration().getRandomGenerator().
				nextInt(a_genes.length - getStartOffset());
				t = a_genes[i];
				a_genes[i] = a_genes[p];
				a_genes[p] = t;
			}
		}
	}

	private int getCidades() {
		return cidades;
	}

	private void setCidades(int cidades) {
		this.cidades = cidades;		
	}

	public Configuration getConfiguration() {
		return config;
	}
	public void setConfiguration(Configuration config) {
		this.config = config;
	}

	public Genotype getPopulacao() {
		return populacao;
	}

	public void setPopulacao(Genotype populacao) {
		this.populacao = populacao;
	}

	public void evoluir(int geracoes) {
		this.setMaxEvolution(geracoes);
		Genotype population=this.getPopulacao();

		//IChromosome best = null;
		//Evolution:
		for (int i = 0; i < getMaxEvolution(); i++) {
			population.evolve();
			//best = population.getFittestChromosome();
			//if (best.getFitnessValue() >= getAcceptableCost()) {
			// System.out.println("AcceptableCost:"+getAcceptableCost());
			//break Evolution;
			//}
		}
	}	
}
