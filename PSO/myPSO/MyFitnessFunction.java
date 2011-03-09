package myPSO;

import net.sourceforge.jswarm_pso.FitnessFunction;

/**
 * Funçao
 * 		f ( x, y ) = x ⋅ sen(4πx ) − y ⋅ sen(4πy + π ) + 1
 * 
 * @author Thiago Lechuga
 */
public class MyFitnessFunction extends FitnessFunction {

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------

	/** Default constructor */
	public MyFitnessFunction() {
		super(true); // maximizar
	}

	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------

	/**
	 * Avaliar
	 * @param position : Particle's position
	 * @return Fitness function for a particle
	 */
	public double evaluate(double position[]) {
		double x = position[0];
		double y = position[1];
		return ( x * Math.sin( 4 * Math.PI * x ) ) - ( y * Math.sin( (4 * Math.PI * y) + Math.PI ) ) + 1;		
	}
}
