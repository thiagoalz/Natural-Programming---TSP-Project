package myPSO;

import net.sourceforge.jswarm_pso.Particle;
import net.sourceforge.jswarm_pso.Swarm;
import net.sourceforge.jswarm_pso.example_2.SwarmShow2D;

public class MyPSO {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Iniciando execução");

		// Create a swarm (using 'MyParticle' as sample particle and 'MyFitnessFunction' as finess function)
		Swarm swarm = new Swarm(Swarm.DEFAULT_NUMBER_OF_PARTICLES, new MyParticle(), new MyFitnessFunction());

		// Tune swarm's update parameters (if needed)
		swarm.setInertia(0.95);
		//swarm.setParticleIncrement(0.8);
		//swarm.setGlobalIncrement(0.8);

		// Set position (and velocity) constraints. I.e.: where to look for solutions
		swarm.setMaxPosition(1);
		swarm.setMinPosition(-1);
		swarm.setMaxMinVelocity(0.1);
		
//////////////////////////////
		int numberOfIterations = 100;

		// Optimize (and time it)
		for( int i = 0; i < numberOfIterations; i++ )
			swarm.evolve();


		// Imprimindo resultados
		System.out.println(swarm.toStringStats());
		
		
		
		System.out.println("Posição das partículas");
		Particle[] vetor=swarm.getParticles();		
		for (int i=0; i<vetor.length;i++ ) {
			Particle atual=vetor[i];
			
			double[] pos=atual.getPosition();
			
			System.out.println(pos[0]+ "   " + pos[1]);
		  }


		// Show best position
		//double bestPosition[] = ss2d.getSwarm().getBestPosition();
		//System.out.println("Best position: [" + bestPosition[0] + ", " + bestPosition[1] + " ]\nBest fitness: " + ss2d.getSwarm().getBestFitness() + "\nKnown Solution: [0.0, 0.0]");
	}

}
