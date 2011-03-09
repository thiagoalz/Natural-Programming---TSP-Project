/*
 * OptAinet.java
 *
 * Author: Paul Andrews
 *
 * Last Modified: 19/10/2005
 *
 * The opt-aiNet algorithm contained within this class is based on that 
 * presented by Leandro N. de Castro and Jon Timmis in "An Artificial Immune
 * Network for Multimodal Function Optimisation", Proceedings of the IEEE
 * Congress on Evolutionary Computation, 2002
 */

package optainet;

import java.util.ArrayList;

/**
 * This class represents the main functionality of the opt-aiNet algorithm 
 * described by de Castro and Timmis 2002 (see reference above). Please refer to
 * de Castro and Timmis 2002 for the algorithm pseudocode and description of how
 * the algorithm work.
 */
public class OptAinet {
  
  private ArrayList<NetworkCell> cellList;  // List of all the network cells 
  private int numInitCells;       // Number of initial network cells  
  private int numClones;          // Number of clones to make of each network cell
  private int maxIter;            // Maximum number of algorithm iterations
  private int numDims;            // Number of optimisation problem dimensions
  private double suppThres;       // Threshold value for network cell suppression
  private double errorThres;      // Threshold value for average population error during clonal selection
  private double divRatio;        // Proportion of current population size to be added for diversity
  private double mutnParam;       // Affinity proportionate mutation parameter
  private double[] lowerBounds;   // Lower bound on each optimisation problem dimension
  private double[] upperBounds;   // Upper bound on each optimisation problem dimension
  
  /**
   * Creates a new instance of OptAinet setting the given algorithm parameters
   * @param numInitCells the initial number of network cells
   * @param numClones the number of clones to be made of each network cell
   * @param maxIter the maximum number of algorith iterations
   * @param suppThres the network cell suppresion threshold
   * @param errorThres the average error threshold value used during clonal selection
   * @param divRatio the proportion of population size to be used when adding diversity
   * @param mutnParam the affinity proportionate mutation parameter
   * @param numDims the number of optimisation problem dimensions
   * @param lowerBounds the array of dimension lower bounds
   * @param upperBounds the array of dimension upper bounds
   */
  public OptAinet(int numInitCells, int numClones, int maxIter, double suppThres, double errorThres, 
                  double divRatio, double mutnParam, int numDims, double[] lowerBounds, double[] upperBounds) {
    
    cellList = new ArrayList<NetworkCell>();
    
    this.numInitCells = numInitCells;
    this.numClones = numClones;
    this.maxIter = maxIter;
    this.numDims = numDims;
    this.suppThres = suppThres;
    this.errorThres = errorThres;
    this.divRatio = divRatio;
    this.mutnParam = mutnParam;
    this.lowerBounds = lowerBounds;
    this.upperBounds = upperBounds;
  }
  
  /**
   * Provides the main control loop for the opt-aiNet algorithm. The algorithm is
   * run until a stopping condition is met. Once the algorithm has completed, 
   * details of each network cell is printed to the standard output.
   */
  public void optimise() {
    int iter = 0;         // Iteration count
    int preNumCells = 0;  // Number of network cells in the previous iteration
    boolean proceed;      // Loop stopping condition variable
    
    // Add the initial number of cells to the network
    
    addCells(numInitCells);
    
    // Iterate until a stopping condition is met
    
    do {
      iter++;
      
      // Perform clonal selection and network cell interactions
      
      clonalSelection();
      networkInteractions();
      
      // If maximum iteration reached, or there has been no change in the number
      // if cells in the network since the last iteration, then terminate the loop
      // Else continue looping, making a note of the number of cells in the 
      // network for comparison in the next iteration, and add a number of new
      // cells to the network depending given by the divRation parameter
      
      if ((iter == maxIter) || (preNumCells == cellList.size()))
        proceed = false;
      else {
        proceed = true;
        preNumCells = cellList.size();
        addCells((int) Math.round(cellList.size() * divRatio));        
      }      
    } while (proceed);
    
    // Print out the details of each cell in the network, givin all the 
    // dimension values followed by the cell's fitness according to the 
    // optimistaion function
    
    for (int i = 0 ; i < cellList.size() ; i++) {      
      for (int j = 0 ; j < numDims ; j++)
        System.out.print(cellList.get(i).getDimension(j) + " ");
      //System.out.println(cellList.get(i).getFitness());
      System.out.println("");
    }
  }
  
  /**
   * Adds a specified number of new cells to the network. Each cell has its 
   * dimension values set to random values.
   * @param numCells the number of new cells to add
   */
  private void addCells(int numCells) {
    NetworkCell cell;
  
    for ( int i = 0 ; i < numCells ; i++ ) {
      cell = new NetworkCell(mutnParam,numDims,lowerBounds,upperBounds);
      cellList.add(cell);
    }
  }
  
  /**
   * Provides the main control loop for the affinity maturation stage of the 
   * opt-aiNet algorithm.
   */
  private void clonalSelection() {
    boolean proceed;            // Loop stopping condition variable
    double preAvgFitness = 0.0; // Average population fitness before clonal selection
    double postAvgFitness;      // Average population fitness after clonal selection
    double fitnessSum;          // Sum of fitnesses of all network cells
    
    // Iterate until stopping condition is met
    
    do {
      // Clonal selection: evaluate all cells against optimistaion function 
      // then clone accordingly
      
      evaluateCells();
      cloneCells();
      
      // Calculate the average fitness of all network cells 
      
      fitnessSum = 0;
      
      for (int i = 0 ; i < cellList.size() ; i++) 
        fitnessSum = fitnessSum + cellList.get(i).getFitness();
      
      postAvgFitness = fitnessSum / cellList.size();
      
      // If the difference between the average cell fitnesses before and after
      // clonal selectionS is below a pre-defined threshold (errorThres) then
      // stop iterating, Else continue to iterate noting the current average
      // cell fitness for comparison in the next iteration
      
      if ((postAvgFitness-preAvgFitness) < errorThres)
        proceed = false;
      else {
        proceed = true;
        preAvgFitness = postAvgFitness;
      }
      
    } while (proceed);
  }
  
  /**
   * Evaluates each network cell against the optimisation function and sets the 
   * normalised fitness for each network cell
   */
  private void evaluateCells() {
    double lowest = 0;                  // lowest cell fitness
    double highest = 0;                 // highest cell fitness
    boolean firstTime = true;           // the first element in the list

    // Evaluate each cell noting the lowest and highest fitnesses in the network

    for (NetworkCell cell : cellList) {
      cell.evaluate();

      // If this is the first cell to be evaluated then its fitness becomes 
      // both the lowest and highest seen sofar, Else check cell's fitness
      // against the current highest and lowest and change accordingly
      
      if ( firstTime ) {
        lowest = cell.getFitness();
        highest = cell.getFitness();
        firstTime = false;
      }
      else {
        if ( cell.getFitness() > highest )
          highest = cell.getFitness();

        if ( cell.getFitness() < lowest )
          lowest = cell.getFitness();
      }
    }

    // Normalise fitness for each cell based on the lowest and highest fitnesses
    // in the network

    for (NetworkCell cell : cellList) 
      cell.setFitnessNorm(lowest,highest);
  }
  
  /**
   * For each cell, a number of clones are creating, producing a clonal pool. 
   * All members of the clonal pool are mutated and the most fit clone is 
   * selected. If this clone is more fit than its parent cell, then the clone
   * replaces the parent cell in the network
   */
  private void cloneCells() {
    NetworkCell currentCell;      // Current network cell being cloned
    NetworkCell clones[] = new NetworkCell[numClones];  // Clonal pool
    int best;   // Array index of the current best clone in the clonal pool
    
    // All cells in the network undergo cloning, mutation and selection
    // Only the clones of the cell are mutated, with the parent remaining
    // unchanged, thus this is an elitest strategy
    
    for (int i = 0 ; i < cellList.size() ; i++) {
      currentCell = cellList.get(i);
      best = 0;       // Set the best clone to be the first by default
      
      // A set number of clones is produced dependent on the numClones
      // parameter. All clones are mutated proportionally to fitness and
      // evaluated against the optimistaion function. A note is made of the
      // clone with the highest fitness in the clonal pool
      
      for (int j = 0 ; j < numClones ; j++) {
        clones[j] = (NetworkCell) currentCell.clone();
        clones[j].mutate();
        clones[j].evaluate();
        
        if (clones[j].getFitness() > clones[best].getFitness())
          best = j;
      }
     
      // If the best clone produced has a better fitness than its parent cell
      // then the clone replaces the parent in the network. If it is not, then
      // the parent remains in the network
      
      if (clones[best].getFitness() > currentCell.getFitness())
        cellList.set(i,clones[best]);        
    }
  }
  
  /**
   * Carries out the interactions between the network cells. Affinities are
   * calculated between all network cells and if two cells have an affinity
   * below a pre-defined threshold, then the cell with the lowest fitness is
   * deleted from the network
   */
  private void networkInteractions() {
    double affinities[][] = new double [cellList.size()][cellList.size()];  // affinities array
    
    // Calculate affinities between all network cells, only the upper diagonal 
    // of the affinities array needs to be filled out
    
    for ( int i = 0 ; i < cellList.size() ; i++ )
      for ( int j = 0 ; j < cellList.size() ; j++ ) {
        if ( i > j )
          affinities[i][j] = 0;
        else
          affinities[i][j] = cellList.get(i).getAffinity(cellList.get(j));
      }

    // Network cells are copied into an array for comparision purposes, as 
    // cells will be deleted from the main cellList ArrayList, thus it cannot be 
    // iterated through
    
    NetworkCell[] cellArray = cellList.toArray(new NetworkCell[0]);
    
    // For each pair of cells in the network (the upper diagonal of the 
    // affinities array), if their affinity is below a threshold determined by 
    // the suppThres parameter, then the cell with the lowest fitness is removed 
    // from the network
    
    for ( int i = 0 ; i < cellArray.length ; i++ )
      for ( int j = 0 ; j < cellArray.length ; j++ )
        if ( (i < j) && (affinities[i][j] < suppThres) ) {
          if ( cellArray[i].getFitness() < cellArray[j].getFitness() )
            cellList.remove(cellArray[i]);
          else
            cellList.remove(cellArray[j]);
        }
  }
 
}
