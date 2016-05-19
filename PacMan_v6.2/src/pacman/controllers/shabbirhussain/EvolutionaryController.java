/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers.shabbirhussain;

import java.awt.Color;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
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
public class EvolutionaryController extends Controller<MOVE> {
	private static final int MAX_DEPTH = 10;
	private static final int MAX_TIME = 20;
	private static final int FITNESS_POOL_MAX_SIZE = 5;
	private static final double MUTATION_PROB=0.01; //in percentage %
	
	public static RandomGhosts ghosts = new RandomGhosts();
	private static Random rnd = new Random();
	private static AlphaBetaController   alphaBeta   = new AlphaBetaController();
	private static HillClimberController hillClimber = new HillClimberController();
	
	private static final Boolean USE_MOVE_MEMORY = true;
	private Queue<MOVE> moveList; // Stores moves if solution is found
	private int targetIndexSave;
	/**
	 * Default Constructor
	 */
	public EvolutionaryController(){
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
			PacManNode solution = evolutionaryAlgo(pmNode, targetIndex, MAX_DEPTH);	
			
			if(solution != null)
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
	public PacManNode evolutionaryAlgo(final PacManNode pmNode, final int targetIndex, final int maxdepth) {
		int time = 1;		
				
		// Get some neighbors
		LinkedList<PacManNode> parentsPool = getNeighbors(pmNode);
		
		do{	
			// Screen unfit nodes
			parentsPool = screenUnfitNodes(parentsPool, targetIndex, FITNESS_POOL_MAX_SIZE);
			
			// Evolve some nodes and return new pool.
			parentsPool = mutateNodes(parentsPool, time);
			
			if(time++ > MAX_TIME)  break;
			if(SearchHelper.getDistanceFromCurrPos(pmNode.game, targetIndex) == 0) break;
			
		}while(true); // Infinite loop
		
		// Get the best node remaining after evolution
		parentsPool = screenUnfitNodes(parentsPool, targetIndex, 1);
		
		return parentsPool.peek();
	}
	
	/**
	 * Given list of game states filters out unfit nodes below threshold.
	 * @param neighbors: A list of PacManNode 
	 * @param threshold: A limit in number of nodes to keep in memory.
	 * @return: A clipped list of PacManNode with unfit nodes clipped out. 
	 */
	private LinkedList<PacManNode> screenUnfitNodes(LinkedList<PacManNode> neighbors, final int targetIndex, int threshold){
		PriorityQueue<PacManNode> pqueue = new PriorityQueue<PacManNode>(
				new Comparator<PacManNode>() {
				    public int compare(PacManNode pn1, PacManNode pn2) {
				        // Astar_value = current cost + heuristic_estimate
				    	
				    	// Total no of moves. Each move costs 1.
				    	int currentCost1 = pn1.moves.size(); 
				    	int currentCost2 = pn1.moves.size();
				    	
				    	// Heuristic cost we use Manhattan distance of current node to target.
				    	int heuristicCost1 = SearchHelper.getDistanceFromCurrPos(pn1.game, targetIndex);
				    	int heuristicCost2 = SearchHelper.getDistanceFromCurrPos(pn2.game, targetIndex);
				    	
				    	int levelScore1    = pn1.game.getCurrentLevel() * 10000;
				    	int levelScore2    = pn2.game.getCurrentLevel() * 10000;
				    	
				    	return ((currentCost1 + heuristicCost1 + levelScore1) < (currentCost2 + heuristicCost2 + levelScore2)) ? -1 : 1;
				    }
				    public int compare1(PacManNode pn1, PacManNode pn2) {				    	
				    	// Heuristic value we use Manhattan distance of current node to target.
				    	double heuristicValue1 = alphaBeta.getHeuristicValue(pn1);
				    	double heuristicValue2 = alphaBeta.getHeuristicValue(pn2);
				    	
				    	return (heuristicValue1 > heuristicValue2) ? -1 : 1;
				    }
				});
		pqueue.addAll(neighbors);
		
		LinkedList<PacManNode> solutionNeighbors= new LinkedList<PacManNode>();
		for(int i=0; i<threshold & (!pqueue.isEmpty()); i++){
			solutionNeighbors.add(pqueue.remove());
		}
		return solutionNeighbors;
	}
	
	/**
	 * Runs a roulette wheel to select a neighbor using probabilities provided.
	 * @param neighbors: A list of PacManNode(s) having current game state.
	 * @return : A randomly mutate a node based on probability.
	 */
	private LinkedList<PacManNode> mutateNodes(LinkedList<PacManNode> neighbors, int time){
		LinkedList<PacManNode> solutionNeighbors= new LinkedList<PacManNode>(neighbors);
		
		for(PacManNode pmNode : neighbors){
			LinkedList<MOVE> currentMoves = new LinkedList<MOVE>(pmNode.moves);
			LinkedList<MOVE> newMoves     = new LinkedList<MOVE>();
			
			Game gameCopy = pmNode.game.copy();
			// Backtrack Moves to start point
			for(int j=(currentMoves.size()-1); j>=0; j--){
				gameCopy.updatePacMan(currentMoves.get(j).opposite());			
			}

			PacManNode pmNodeChild = new PacManNode(gameCopy, 0, newMoves);
			
			int mutationPoint = ((time / MAX_TIME) * currentMoves.size());
			for(int i=0; i<currentMoves.size(); i++){
				if(i >= mutationPoint && rnd.nextDouble() <= MUTATION_PROB){
					// Linear reduction in mutation
					GameView.addPoints(gameCopy, Color.YELLOW, pmNodeChild.game.getPacmanCurrentNodeIndex());
					
					pmNodeChild = getNeighbor(pmNodeChild, (currentMoves.size() - i));
					
					GameView.addPoints(gameCopy, Color.RED, pmNodeChild.game.getPacmanCurrentNodeIndex());
					break;
				}else{
					newMoves.add(currentMoves.get(i));
					gameCopy.updatePacMan(newMoves.get(i));	
					pmNodeChild = new PacManNode(pmNodeChild.game, pmNodeChild.depth, newMoves);
				}
			}
			solutionNeighbors.add(pmNodeChild);
		}
		return solutionNeighbors;
	}
	

	/**
	 * Returns neighbors of current game state.
	 * @param game 		: Copy of current game.
	 * @return A list of games advanced by distance steps with randomly generated neighbors.
	 */
	public LinkedList<PacManNode> getNeighbors(final PacManNode pmnode){
		LinkedList<PacManNode> neighbors = new LinkedList<PacManNode>();

		// Generate a distance 1 neighbor
		neighbors.add(getNeighbor(pmnode, 1));

		// Generate a distance 2 neighbor
		neighbors.add(getNeighbor(pmnode, 2));
		
		// Generate a distance 3 neighbor
		
		// Generate a distance 5+ neighbor
		neighbors.add(getNeighbor(pmnode, 37));
		
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
			if (i == 0)
				m = SearchHelper.getRandomMoveWithBacktracking(gameCopy);
			else // DO NOT GO BACK;
				m = SearchHelper.getRandomMove(gameCopy);
			
			int currentIndex = gameCopy.getPacmanCurrentNodeIndex();			
			gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
			currentMoves.add(m);
			
			// If path leads to ghost cut the branch after atleast one move
			if (gameCopy.wasPacManEaten()) break;
			GameView.addLines(pmnode1.game, Color.MAGENTA, currentIndex, gameCopy.getPacmanCurrentNodeIndex());
		}
		pmnode2 = new PacManNode(gameCopy, i, currentMoves);
		return pmnode2;
	}
}
