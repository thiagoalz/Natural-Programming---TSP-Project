/*
 * OptFunction.java
 *
 * Author: Paul Andrews
 *
 * Last Modified: 12/10/2005
 */

package optainet;

/**
 * Classe com a função a ser solucionada.
 */
public class OptFunction {
  
  /**
   * Creates a new instance of OptFunction 
   */
  public OptFunction() { }
  
  /**
   * Avalia uma célula
   * Fitness function: f(x,y) = x.sin(4x.PI) - y.sin(4y.PI+PI) + 1
   * @param dimensions the dimension values of a network cell
   * @return the evaluated fitness of the network cell dimension values
   */
  public double evaluateCell(double[] dimensions) {
    double fitness;
   
    fitness = dimensions[0] * Math.sin(4 * Math.PI * dimensions[0]) 
              - dimensions[1] * Math.sin(4 * Math.PI * dimensions[1] + Math.PI) + 1;    
    
    return fitness;
  }
  
}