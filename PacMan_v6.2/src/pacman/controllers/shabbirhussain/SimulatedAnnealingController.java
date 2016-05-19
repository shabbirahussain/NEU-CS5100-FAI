/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers.shabbirhussain;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.shabbirhussain.HillClimberController;
import pacman.controllers.examples.RandomGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

/**
 *
 * @author Shabbir Hussain
 */
public class SimulatedAnnealingController extends Controller<MOVE> {
	private static final int MAX_DEPTH = 10;
	private static final int MAX_TIME = 100;
	public static RandomGhosts ghosts = new RandomGhosts();
	private static Random rnd = new Random();
	private static HillClimberController hillClimber = new HillClimberController();
	
	private static final Boolean USE_MOVE_MEMORY = false;
	private Queue<MOVE> moveList; // Stores moves if solution is found
	private int targetIndexSave;
	/**
	 * Default Constructor
	 */
	public SimulatedAnnealingController(){
		moveList = new LinkedList<MOVE>();
	}
	
	/**
	 * Compute the next move given a copy of the current game and a time the move has to be computed by. This is the method contestants need to implement. Many examples are available in pacman.controllers.examples Your controllers must be in the files: pacman.entries.pacman.MyPacMan.java for Pac-Man controllers or pacman.entries.ghosts.MyGhosts.java for ghosts controllers.
	 *
	 * Overrides: getMove(...) in Controller
	 * 
	 * @param game : A copy of the current game
	 * @param timeDue : The time the next move is due
	 * @return The move to be played (i.e., the move calculated by your controller)
	 */
	public MOVE getMove(Game game, long timeDue) {
		System.out.println("Starting new Search =======================>");

		if(!USE_MOVE_MEMORY || this.moveList.isEmpty()){ // start new search
			this.moveList.clear();
			PacManNode pmNode = new PacManNode(game, 0, new LinkedList<MOVE>());
			int targetIndex = SearchHelper.getNearestActivePill(game);
			PacManNode solution = simulatedAnnealing(pmNode, targetIndex, MAX_DEPTH);	
			
			this.moveList 			= new LinkedList<MOVE>(solution.moves);
			this.targetIndexSave 	= targetIndex;	
		}else{					// Get a move from list
			GameView.addPoints(game, Color.CYAN, targetIndexSave);
			Game gameCopy = game.copy();
			for(MOVE m : this.moveList){
				int currentNodeIndex = gameCopy.getPacmanCurrentNodeIndex();
				gameCopy.updatePacMan(m);				
				GameView.addLines(game, Color.CYAN, currentNodeIndex, gameCopy.getPacmanCurrentNodeIndex());
			}
		}
		System.out.println("Solution Moves Set:" + this.moveList);
		if (moveList.isEmpty()){	 // Can't do anything. No solutions exists within limits. use a random move.
			return SearchHelper.getRandomMove(game); 
		}
		
		return moveList.remove();
	}
	/**
	 * Gets best move using simulated annealing hill climbing.
	 * @param game: Current state of game.
	 * @param targetIndex: Target index in the maze to find distance.
	 * @param maxdepth: Maximum depth a search has to be performed.
	 * @return A PacManNode with best score and move list.
	 */
	public PacManNode simulatedAnnealing(final PacManNode pmNode, final int targetIndex, final int maxdepth) {
		int bestDistance = Integer.MAX_VALUE;
		int newBestDistance;
		int time = 1;		
		
		PacManNode bestNeighbor 	= pmNode;
		PacManNode newBestNeighbor 	= bestNeighbor;
		do{
			// Get some neighbors
			LinkedList<PacManNode> neighbors = hillClimber.getNeighbors(bestNeighbor);			
			
			//Calculate probabilities.
			LinkedList<Double> probabilities = calculateProbabilities(bestNeighbor, neighbors);
			System.out.println("Probabilities:" + probabilities);
			
			// Get the most optimal neighbor based on probabilities
			newBestNeighbor = getBestNeighborFromProbabilities(neighbors, probabilities);
			if (newBestNeighbor == null) break;
			
			System.out.println("Current Best:" + bestNeighbor.minimaxScore + "\t\t\t New Best:" + newBestNeighbor.minimaxScore);

			bestNeighbor = newBestNeighbor;
			
			// break if end result is found
			if(bestDistance == 0 || time++ > MAX_TIME)  break;			
		}while(true); // Infinite loop
		
		return bestNeighbor;
	}
	/**
	 * Runs a roulette wheel to select a neighbor using probabilities provided.
	 * @param neighbors: A list of PacManNode(s) having current game state.
	 * @param probabilities: A list of probabilities for each node.
	 * @return : A random neighbor depending on probabilities.
	 */
	private PacManNode getBestNeighborFromProbabilities(LinkedList<PacManNode> neighbors, LinkedList<Double> probabilities){
		Double rouletteNumber = rnd.nextDouble();
		Double currentTotal = 0.0;
		for(int i=0; i<neighbors.size(); i++){
			currentTotal += probabilities.get(i);
			if(currentTotal >= rouletteNumber) return neighbors.get(i);
		}
		return null;
	} 
	
	/**
	 * Calculates the probabilities of neighbors given closer the neighbor more is his probability. 
	 * @param neighbors: A Linked list containing games
	 * @param targetIndex: Target for calculating probabilities.
	 * @return LinkedList array of float containing probability with total of 1.
	 */
	private LinkedList<Double> calculateProbabilities(PacManNode pmStartNode, LinkedList<PacManNode> neighbors){
		LinkedList<Double> probArr = new LinkedList<Double>();
		LinkedList<Double> manDist = new LinkedList<Double>();
		
		// Total distance and exponential distance with randomness decreasing with time.
		double currDist;
		double totalDistance = 0.0;
		for (int i = 0; i < neighbors.size(); i++) {
			currDist = SmartController.getHeuristicValue(pmStartNode, neighbors.get(i)); //SearchHelper.getDistanceFromCurrPos(n, targetIndex);
			currDist /= Integer.MAX_VALUE;
			manDist.add(i, currDist);
			totalDistance += currDist;
		}
	
		// Calculate probabilities.
		for (int i = 0; i < neighbors.size(); i++) {
			// Nearest node will have highest value.
			if(totalDistance !=0 )
				probArr.add(i, (manDist.get(i) / totalDistance));
			else 
				probArr.add(i, 1.0);
		
		}
		return probArr;
	}
}
