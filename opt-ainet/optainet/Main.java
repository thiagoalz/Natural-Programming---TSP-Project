/*
 * Main.java
 *
 * Author: Paul Andrews
 *
 * Last Modified: 20/10/2005
 */

package optainet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class Main {
  
  private int numCells=20;           // Número inicial de celulas  
  private int numDimensions=2;      // dimensão
  private int numClones=10;          // Numero de clones
  private int maxIter=500;            // Numero maximo de iteracoes
  private double suppThres=0.2;       // Threshold para supressão
  private double errorThres=0.001;      // Threshold para erro média da populaçao durante seleção clonal
  private double divRatio=0.4;        // Proporçao da população atual para ser adicionada para diversidade
  private double mutnParam=100.0;       // Mutação
  private double[] lowerBounds={-1.0,-1.0};  
  private double[] upperBounds={1.0,1.0};  

  
  /**
   * Creates a new instance of Main
   * @param args the command line arguments
   */
  public Main(String[] args) {
    
     //Criando o aptAinet
      OptAinet opt = new OptAinet(numCells,numClones,maxIter,suppThres,errorThres,
          divRatio,mutnParam,numDimensions,lowerBounds,upperBounds);
      
      //otimiando 
      opt.optimise();

  }
  
  
  /**
   * Creates an instance of Main
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    new Main(args);
  }
  
}
