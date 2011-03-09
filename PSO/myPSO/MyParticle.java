package myPSO;

import net.sourceforge.jswarm_pso.Particle;

/**
 * Partícula
 * @author Thiago Lechuga
 */
public class MyParticle extends Particle {

	/** Number of dimentions for this particle */
	public static int NUMBER_OF_DIMENTIONS = 2;

	//-------------------------------------------------------------------------
	// Constructor/s
	//-------------------------------------------------------------------------
	
	/**
	 * Default constructor
	 */
	public MyParticle() {
		super(NUMBER_OF_DIMENTIONS);	
	}

	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------

	/** Convert to string() */
	public String toString() {
		String str = super.toString();
		return str;
	}
}