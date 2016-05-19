/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers.shabbirhussain;

import java.awt.Color;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import pacman.controllers.Controller;
import pacman.controllers.examples.RandomGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

/**
 *
 * @author Shabbir Hussain
 */
public class HillClimberController extends Controller<MOVE> {
	private static final int MAX_DEPTH = 10;
	private static final int MAX_RETRY = 4;
	private static final int MAX_TIME  = 10;
	public static RandomGhosts ghosts = new RandomGhosts();
	
	private static final Boolean USE_MOVE_MEMORY = false;
	private Queue<MOVE> moveList; // Stores moves if solution is found
	private int targetIndexSave;
	/**
	 * Default Constructor
	 */
	public HillClimberController(){
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
			PacManNode solution = hillClimber(pmNode, targetIndex, MAX_DEPTH);
			
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
	 * Gets best move using steep hill climbing.
	 * @param game: Current state of game.
	 * @param targetIndex: Target index in the maze to find distance.
	 * @param maxdepth: Maximum depth a search has to be performed.
	 * @return A PacManNode with best score and move list.
	 */
	public PacManNode hillClimber(final PacManNode pmNode, final int targetIndex, final int maxdepth) {
		int bestDistance = Integer.MAX_VALUE;
		int newBestDistance;
		PriorityQueue<PacManNode> aStarQueue = new PriorityQueue<PacManNode>();
		aStarQueue.add(pmNode);
		
		PacManNode bestNeighbor 	= pmNode;
		PacManNode newBestNeighbor 	= bestNeighbor;	
		
		int timeElapsed  = 0;
		do{
			
			// Get some neighbors
			aStarQueue = new PriorityQueue<PacManNode>(getNeighbors(bestNeighbor));
			
			//Get Best neighbor.
			if(aStarQueue.isEmpty()) continue;
			newBestNeighbor = aStarQueue.remove();
			
			System.out.println("Current Best:" + bestNeighbor.minimaxScore + "\t\t\t New Best:" + newBestNeighbor.minimaxScore);
			
			// A better neighbor exists continue search.
			if(bestNeighbor.minimaxScore < newBestNeighbor.minimaxScore) 
				bestNeighbor = newBestNeighbor;
			else
				break;
			
		}while(timeElapsed++ < MAX_TIME); // Infinite loop
		
		return bestNeighbor;
	}
	/**
	 * Selects the best neighbor depending on its distance from target
	 * @param neighbors : A LinkedList of PacManNodes to choose the best neighbor.
	 * @return : Best possible PacManNode.
	 */
	public PacManNode getBestNeighbor(LinkedList<PacManNode> neighbors, final int targetIndex){
		int bestDistance = Integer.MAX_VALUE;
		int currDistance ;
		PacManNode bestNeighbor = null;
		
		for (PacManNode n: neighbors) {
			currDistance = SearchHelper.getDistanceFromCurrPos(n.game, targetIndex);
			if(currDistance < bestDistance){	// Found a better Node
				bestDistance = currDistance;
				bestNeighbor = n;
			}
		}
		return bestNeighbor;
	}
	
	/**
	 * Returns neighbors of current game state.
	 * @param game 		: Copy of current game.
	 * @return A list of games advanced by distance steps with randomly generated neighbors.
	 */
	public LinkedList<PacManNode> getNeighbors(final PacManNode pmnode){
		LinkedList<PacManNode> neighbors = new LinkedList<PacManNode>();

		neighbors.add(getNeighbor(pmnode, 0));
		
		// Generate a distance 1 neighbor
		neighbors.add(getNeighbor(pmnode, 1));

		// Generate a distance 2 neighbor
		neighbors.add(getNeighbor(pmnode, 2));
		
		// Generate a distance 3 neighbor
		neighbors.add(getNeighbor(pmnode, 3));
		
		// Generate a distance 5+ neighbor
		neighbors.add(getNeighbor(pmnode, 19));
		//*/
		
		/*
		neighbors.add(getNeighbor(pmnode, 0));
		
		// Generate a distance 1 neighbor
		neighbors.add(getNeighbor(pmnode, 1));
		neighbors.add(getNeighbor(pmnode, 1));
		neighbors.add(getNeighbor(pmnode, 1));
		
		

		// Generate a distance 2 neighbor
		neighbors.add(getNeighbor(pmnode, 2));
		neighbors.add(getNeighbor(pmnode, 2));
		neighbors.add(getNeighbor(pmnode, 2));
		
		// Generate a distance 3 neighbor
		neighbors.add(getNeighbor(pmnode, 3));
		neighbors.add(getNeighbor(pmnode, 3));
		
		// Generate a distance 5+ neighbor
		neighbors.add(getNeighbor(pmnode, 5));
		neighbors.add(getNeighbor(pmnode, 7));
		neighbors.add(getNeighbor(pmnode, 19));
		neighbors.add(getNeighbor(pmnode, 29));
		//*/
		
		// Filter branches where pacman is eaten
		
		Boolean flagNodeRemoved = false;
		do{
			flagNodeRemoved = false;
			for(PacManNode n : neighbors){
				if(n.game.wasPacManEaten()){
					neighbors.remove(n);
					flagNodeRemoved = true;
					break;
				}
			}
		}while(flagNodeRemoved);
		//*/
		return neighbors;
	}
	/**
	 * Returns neighbor of current game state.
	 * @param game 		: Copy of current game.
	 * @param atDepth	: Neighbor generation distance.
	 * @return A PacManNode with Game advanced by distance steps with randomly generated neighbors.
	 */
	public PacManNode getNeighbor(PacManNode pmnode1, int atDepth){
		MOVE m;
		PacManNode pmnode2;
		Queue<MOVE> currentMoves = new LinkedList<MOVE>(pmnode1.moves);
		Game gameCopy = pmnode1.game.copy();
		int i;
		for(i=0; i < atDepth; i++){
			if (false &i == 0)
				m = SearchHelper.getRandomMoveWithBacktracking(gameCopy);
			else // DO NOT GO BACK;
				m = SearchHelper.getRandomMove(gameCopy);
			
			int currentIndex = gameCopy.getPacmanCurrentNodeIndex();			
			gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
			currentMoves.add(m);
			
			// If path leads to ghost cut the branch after atleast one move
			// if (gameCopy.wasPacManEaten()) break;
			GameView.addLines(pmnode1.game, Color.MAGENTA, currentIndex, gameCopy.getPacmanCurrentNodeIndex());
		}
		pmnode2 = new PacManNode(gameCopy, i, currentMoves);
		pmnode2.updateMinimaxScore(SmartController.getHeuristicValue(pmnode1, pmnode2));
		
		return pmnode2;
	}
}
