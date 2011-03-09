package controle.GA;

import org.jgap.*;
import org.jgap.impl.*;


public class GeneTSP extends IntegerGene {

	private Alelo alelo;
	
	private int tamanho;
	
	public GeneTSP(Configuration config,int tamanho) throws InvalidConfigurationException {				
		super(config,0,tamanho-1);		
		this.tamanho=tamanho;
	}
	
	public GeneTSP() throws InvalidConfigurationException{
		super();
	}

	public float getPos_x() {
		return ((Alelo)this.getAllele()).getPos_x();
	}

	public float getPos_y() {
		return ((Alelo)this.getAllele()).getPos_y();
	}
	
	public int getTamanho(){
		return this.tamanho;
	}
	
	public Gene newGene(){
		try {
			return new GeneTSP(super.getConfiguration(),this.tamanho);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return null;		
	}
	
	public void setAllele(Object alelo){
		this.alelo=(Alelo)alelo;
		super.setAllele(this.alelo.getNumero());
	}
	
	public Object getAllele(){
		return this.alelo;
	}
	
	/**
	   * Maps the value of this IntegerGene to within the bounds specified by
	   * the m_upperBounds and m_lowerBounds instance variables. The value's
	   * relative position within the integer range will be preserved within the
	   * bounds range (in other words, if the value is about halfway between the
	   * integer max and min, then the resulting value will be about halfway
	   * between the upper bounds and lower bounds). If the value is null or
	   * is already within the bounds, it will be left unchanged.
	   *
	   * @author Thiago Lechuga
	   * @author Klaus Meffert
	   * @since 1.0
	   */
	  protected void mapValueToWithinBounds() {
	    if (getAllele() != null) {
	    	Alelo alelo=(Alelo) getAllele();
	      Integer i_value = ( alelo.getNumero() );
	      // If the value exceeds either the upper or lower bounds, then
	      // map the value to within the legal range. To do this, we basically
	      // calculate the distance between the value and the integer min,
	      // determine how many bounds units that represents, and then add
	      // that number of units to the upper bound.
	      // -----------------------------------------------------------------
	      if (i_value.intValue() > this.getTamanho() ||
	          i_value.intValue() < 0) {
	        RandomGenerator rn;
	        if (getConfiguration() != null) {
	          rn = getConfiguration().getRandomGenerator();
	        }
	        else {
	          rn = new StockRandomGenerator();
	        }
	        if (this.getTamanho() - 0 == 0) {
	          setAllele(new Alelo(0,alelo.getPos_x(),alelo.getPos_y()));
	        }
	        else {
	          setAllele(new Alelo( (rn.nextInt(this.getTamanho() - 0) + 0),alelo.getPos_x(),alelo.getPos_y()));
	        }
	      }
	    }
	  }

	  /**
	   * Retrieves the int value of this Gene, which may be more convenient in
	   * some cases than the more general getAllele() method.
	   *
	   * @return the int value of this Gene
	   *
	   * @author Thiago Lechuga
	   * @since 1.0
	   */
	  public int intValue() {
	    return ( ((Alelo) getAllele())).getNumero();
	  }

	  /**
	   * @return string representation of this Gene's value that may be useful for
	   * display purposes
	   *
	   * @author Thiago Lechuga
	   * @since 2.4
	   */
	  public String toString() {
	    String s = "GeneTSP(" + 0 + "," + this.getTamanho() + ")"
	        + "=";
	    if (getInternalValue() == null) {
	      s += "null";
	    }
	    else {
	      s += getInternalValue().toString();
	    }
	    return s;
	  }
}
