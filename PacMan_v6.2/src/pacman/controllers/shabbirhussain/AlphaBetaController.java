/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers.shabbirhussain;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

/**
 *
 * @author Shabbir Hussain
 */
public class AlphaBetaController extends Controller<MOVE> {
	private static final int MAX_DEPTH = 3;
	private static final int SEARCH_STEP_DEPTH = 4;
	private static Boolean DEBUG=false;
	private static Boolean ENABLE_PRUNING=true;
	
	private enum AGENT{
		GHOSTS,
		PACMAN
	}
	
	private static final Boolean USE_MOVE_MEMORY = true;
	private Queue<MOVE> moveList; // Stores moves if solution is found
	private int targetIndexSave;
	/**
	 * Default Constructor
	 */
	public AlphaBetaController(){
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
			int α = 0;
			int β = Integer.MAX_VALUE;
			PacManNode solution = alphaBeta(pmNode, MAX_DEPTH, α, β, true, null, pmNode);

			this.moveList 			= new LinkedList<MOVE>(solution.moves);
			this.targetIndexSave 	= solution.game.getPacmanCurrentNodeIndex();			
		}else{					// Get a move from list
			GameView.addPoints(game, Color.CYAN, targetIndexSave);
			Game gameCopy = game.copy();
			for(MOVE m : this.moveList){
				int currentNodeIndex = gameCopy.getPacmanCurrentNodeIndex();
				gameCopy.updatePacMan(m);				
				GameView.addLines(game, Color.CYAN, currentNodeIndex, gameCopy.getPacmanCurrentNodeIndex());
			}
			int actpillidx = SearchHelper.getNearestActivePill(game);
			System.out.println(game.getPacmanCurrentNodeIndex() + " => " + actpillidx);
		}
		System.out.println("Solution Moves Set:" + this.moveList);
		if (moveList.isEmpty()){	 // Can't do anything. No solutions exists within limits. use a random move.			
			return SearchHelper.getRandomMove(game); 
		}
		//int i=1/0;
		return moveList.remove();
	}
	/**
	 * Gets best move sequence using AlphaBeta pruning.
	 * @param pmNode: Current state of game as PacManNode.
	 * @param depth: Maximum search depth available.
	 * @param α: Value of Maximizer to root.
	 * @param β: Value of Minimizer to root.
	 * @param maximizingPlayer: Flag showing current player is maximizer or minimizer.
	 * @param g: Specifies the agent to run simulation for.
	 * @return: A PacManNode with best score and move list.
	 */
	private PacManNode alphaBeta(final PacManNode pmNode, final int depth, double α, double β, final Boolean maximizingPlayer, final GHOST g, PacManNode bestSolution) {
		/** 
		 * AlphaBeta Minimax (fail-safe version) algorithm from Wikipedia.
		 * =====================================================================
		 * function alphabeta(node, depth, α, β, maximizingPlayer)
		 * 		if depth = 0 or node is a terminal node
         * 			return the heuristic value of node
		 * 		if maximizingPlayer
		 * 			v := -∞
		 *  		for each child of node
		 *     	  		v := max(v, alphabeta(child, depth - 1, α, β, FALSE))
		 *    	  		α := max(α, v)
		 *      		if β ≤ α
		 *  	        	break (* β cut-off *)
		 * 	 		return v
		 * 		else
		 *			v := ∞
		 *			for each child of node
		 *				v := min(v, alphabeta(child, depth - 1, α, β, TRUE))
		 *				β := min(β, v)
		 *				if β ≤ α
		 *					break (* α cut-off *)
		 *			return v
		 * =====================================================================
		 */
		if (depth == 0 || pmNode.game.gameOver()){	
			bestSolution = pmNode;
			bestSolution.updateMinimaxScore(getHeuristicValue(bestSolution));
			if(DEBUG){
				for(int i=0; i<bestSolution.depth; i++) System.out.print(" ");
				System.out.println("End:" + bestSolution + " a=" + α + " b=" + β);
			}
			return bestSolution;
		}
		if(DEBUG){
			for(int i=0; i<pmNode.depth; i++) System.out.print(" ");
			System.out.println("Player:" + maximizingPlayer + " Node=" + pmNode + " a=" + α + " b=" + β + " g=" + g);
		}		
		double v;
		PacManNode solution;
		if (maximizingPlayer){
			v = Integer.MIN_VALUE;
			LinkedList<PacManNode> pmNodeChildren = generateSuccessor(pmNode, AGENT.PACMAN, null ,SEARCH_STEP_DEPTH);
			for(PacManNode pmChild : pmNodeChildren){
				solution = alphaBeta(pmChild, depth - 1, α, β, false, GHOST.values()[0], bestSolution);
				
				
				v = Math.max(v, solution.minimaxScore);
				//α = Math.max(α, v);
				if(α <= v){
					α = v;
					bestSolution = solution;
				}
				if(DEBUG){
					for(int i=0; i<pmNode.depth; i++) System.out.print(" ");
					System.out.println("Player:" + maximizingPlayer + " Node=" + solution + " a=" + α + " b=" + β + " g=" + g);
				}
								
				if (ENABLE_PRUNING && β <= α){
					GameView.addPoints(pmNode.game, Color.RED, pmNode.game.getPacmanCurrentNodeIndex());
					break;
				}
			}					
		}else{	 
			v = Integer.MAX_VALUE;
			LinkedList<PacManNode> pmNodeChildren = generateSuccessor(pmNode, AGENT.GHOSTS, g, SEARCH_STEP_DEPTH);

			for(PacManNode pmChild : pmNodeChildren){
				if(g.ordinal() == (GHOST.values().length-1))
					solution = alphaBeta(pmChild, depth - 1, α, β, true , null, bestSolution);	
				else
					solution = alphaBeta(pmChild, depth    , α, β, false, GHOST.values()[g.ordinal() + 1], bestSolution);	
								
				v = Math.min(v, solution.minimaxScore);
				//β = Math.min(β, v);
				if(β >= v){
					β = v;
					bestSolution = solution;
				}
				if(DEBUG){
					for(int i=0; i<pmNode.depth; i++) System.out.print(" ");
					System.out.println("Player:" + maximizingPlayer + " Node=" + solution + " a=" + α + " b=" + β + " g=" + g);
				}	
				
				if (ENABLE_PRUNING && β <= α){
					GameView.addPoints(pmNode.game, Color.ORANGE, pmNode.game.getPacmanCurrentNodeIndex());
					break;
				}
			}
		}//*/
		
		//for(int i=0; i<pmNode.depth; i++) System.out.print(" ");
		//System.out.println(bestSolution);
		
		return bestSolution;
	}
	/**
	 * Generates heuristic value of each node state
	 * @param pmNode: Current state of game in terms of PacManNode
	 * @return: Heuristic value of PacManNode
	 */
	public double getHeuristicValue(PacManNode pmNode){
		double heuristicScore 	 = 0;
		
		double currentScore 	 = pmNode.game.getScore();
		heuristicScore 		+= currentScore * 1000;
		
		double currentLevelTime = pmNode.game.getCurrentLevelTime();
		heuristicScore 		+= 1000/(currentLevelTime+1);
		
		double shortestFoodDistance = pmNode.moves.size() + (int)pmNode.game.getEuclideanDistance(pmNode.game.getPacmanCurrentNodeIndex(), SearchHelper.getNearestActivePill(pmNode.game));
		//int shortestFoodDistance = (int)pmNode.game.getEuclideanDistance(pmNode.game.getPacmanCurrentNodeIndex(), SearchHelper.getNearestActivePill(pmNode.game));
		shortestFoodDistance = 1000/(shortestFoodDistance+1);
		heuristicScore 		+= shortestFoodDistance;
		
		/*
		int foodDistance  	 = (int)getAverageActivePillDistance(pmNode.game);
		foodDistance		 = 1000/(foodDistance+1);
		heuristicScore 		+= foodDistance;
		*/

		double noOfActivePills	 = pmNode.game.getNumberOfActivePills();
		noOfActivePills		 = 200/(noOfActivePills+1);
		heuristicScore 		+= noOfActivePills;
		
		double noOfPowerPills	 = pmNode.game.getNumberOfPowerPills();
		noOfPowerPills		 = 400/(noOfPowerPills+1);
		heuristicScore 		+= noOfPowerPills;
		
		double currentLives 	 = pmNode.game.getPacmanNumberOfLivesRemaining();
		currentLives		 = currentLives * 20000;
		currentLives		 = (currentLives<0)? 0 :currentLives;
		heuristicScore 		+= currentLives;
		
		
		double distanceToGhost	 = getNearestGhostDistance(pmNode.game);
		distanceToGhost		 = Math.min(distanceToGhost, 5) * 200;
		heuristicScore 		+= distanceToGhost;
		
		
		//int distanceToEdibleGhost	 = getNearestEdibleGhostDistance(pmNode.game);
		//heuristicScore 		+= 100/distanceToEdibleGhost * 2;
		if(DEBUG || heuristicScore<0){
			System.out.println("currentScore="+currentScore);
			//System.out.println("foodDistance="+foodDistance);
			System.out.println("noOfActivePills="+noOfActivePills);
			System.out.println("noOfPowerPills="+noOfPowerPills);
			System.out.println("currentLives="+currentLives);
			System.out.println("distanceToGhost="+distanceToGhost);
			System.out.println("heuristicScore="+heuristicScore);
		}
		return heuristicScore;
	}
	/**
	 * Gets indices of avearge active pills distance.
	 * @param game: Copy of current game.
	 * @return The average distance from all pills.
	 */
	public static double getAverageActivePillDistance(Game game) {
		int activePillsIndices[] = game.getActivePillsIndices();
		int activePowerPillsIndices[] = game.getActivePowerPillsIndices();
		ArrayList<Integer> allPillsIndices = new ArrayList<Integer>();
		
		for(int activePillIndex : activePillsIndices){
			allPillsIndices.add(activePillIndex);
		}
		for(int powerPillIndex : activePowerPillsIndices){
			allPillsIndices.add(powerPillIndex);
		}
		
		int currentNodeIndex = game.getPacmanCurrentNodeIndex();
		double totDistance = 0;

		for (int anyPillIndex : allPillsIndices) {
			//double distance = game.getManhattanDistance(currentNodeIndex, anyPillIndex);
			totDistance += game.getEuclideanDistance(currentNodeIndex, anyPillIndex);
		}
		totDistance = totDistance / (allPillsIndices.size());
		return totDistance;		
	}
	/**
	 * Generates states of Ms. Pacman.
	 * @param pmNode: Current game state in PacManNode
	 * @param maxDepth: Depth at which nodes are generated.
	 * @return: A list of new PacManNode after advancing game
	 */
	private LinkedList<PacManNode> generateSuccessor(PacManNode pmStartNode, AGENT agent, GHOST g, int maxDepth){
		LinkedList<PacManNode> pmChildNodes = new LinkedList<PacManNode>();
		int relativeMaxDepth = maxDepth + pmStartNode.depth;
		
		Stack<PacManNode> unvisitedNodes = new Stack<PacManNode>();
		unvisitedNodes.push(pmStartNode); 
		
		while(!unvisitedNodes.isEmpty()){
			PacManNode pmNode = unvisitedNodes.pop();
			
			if (AGENT.PACMAN == agent){
				int currentNodeIndex = pmNode.game.getPacmanCurrentNodeIndex();
				MOVE lastMove        = pmNode.game.getPacmanLastMoveMade();
				MOVE[] availableMoves;
				
				if (pmNode.depth == 0)
					availableMoves = pmNode.game.getPossibleMoves(currentNodeIndex);
				else
					availableMoves = pmNode.game.getPossibleMoves(currentNodeIndex, lastMove);				
				
				for(MOVE m : availableMoves){
					// Create a child
					PacManNode pmChildNode = null;
					Queue<MOVE> movesQueue = new LinkedList<MOVE>(pmNode.moves);
					Game gameCopy = pmNode.game.copy();
					gameCopy.updatePacMan(m);
					gameCopy.updateGame();
					
					movesQueue.add(m);
					pmChildNode = new PacManNode(gameCopy, pmNode.depth + 1, movesQueue);
					int nextNodeIndex = pmChildNode.game.getPacmanCurrentNodeIndex();
					
					if(pmChildNode.depth == relativeMaxDepth)
						pmChildNodes.add(pmChildNode);
					else{
						if(pmChildNode.game.isJunction(nextNodeIndex))	pmChildNodes.add(pmChildNode);
						unvisitedNodes.push(pmChildNode);
					}
					GameView.addLines(pmNode.game, Color.MAGENTA, currentNodeIndex, nextNodeIndex);
				}
			}else{
				// Create a child
				PacManNode pmChildNode = null;
				int currentNodeIndex = pmNode.game.getGhostCurrentNodeIndex(g);
				MOVE lastMove        = pmNode.game.getGhostLastMoveMade(g);
				MOVE[] availableMoves;
				
				if ((pmNode.depth - pmStartNode.depth) == 0)
					availableMoves = pmNode.game.getPossibleMoves(currentNodeIndex);
				else
					availableMoves = pmNode.game.getPossibleMoves(currentNodeIndex, lastMove);				
				
				if (availableMoves.length == 0) {
					Queue<MOVE> movesQueue = new LinkedList<MOVE>(pmNode.moves);
					Game gameCopy = pmNode.game.copy();
					
					pmChildNode = new PacManNode(gameCopy, pmNode.depth + 1, movesQueue);					
					pmChildNodes.add(pmChildNode);
				}else{
					for(MOVE m : availableMoves){
						Queue<MOVE> movesQueue = new LinkedList<MOVE>(pmNode.moves);
						Game gameCopy = pmNode.game.copy();
						
						EnumMap<GHOST, MOVE> ghostsMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
						ghostsMoves.put(g, m);					
						gameCopy.updateGhosts(ghostsMoves);
						gameCopy.updateGame();
						
						//movesQueue.add(MOVE.NEUTRAL);
						pmChildNode = new PacManNode(gameCopy, pmNode.depth + 1, movesQueue);
						int nextNodeIndex = pmChildNode.game.getGhostCurrentNodeIndex(g);
						
						if(pmChildNode.depth == relativeMaxDepth)
							pmChildNodes.add(pmChildNode);
						else{
							if(pmChildNode.game.isJunction(nextNodeIndex))	pmChildNodes.add(pmChildNode);
							unvisitedNodes.push(pmChildNode);
						}
						GameView.addLines(pmNode.game, Color.YELLOW, currentNodeIndex, nextNodeIndex);
					}
				}
			}
		}
		return pmChildNodes;
	}
	/**
	 * Gets indices of nearest ghost using Manhattan distance between current pacman and pill.
	 * @param game: Copy of current game.
	 * @return The index of nearest ghost to pacman. 
	 */
	public int getNearestGhostDistance(Game game) {
		int currentNodeIndex = game.getPacmanCurrentNodeIndex();
		int distMin			 = 100;
		
		for(GHOST g : GHOST.values()){
			int targetNodeIndex = game.getGhostCurrentNodeIndex(g);
			if (!game.isGhostEdible(g))
				distMin = (int) Math.min(distMin, Math.max(0, game.getShortestPathDistance(currentNodeIndex, targetNodeIndex)));
		}
		//if(distMin==-1) distMin = Integer.MAX_VALUE;
		return distMin;
	}
	/**
	 * Gets indices of nearest edible ghost using Manhattan distance between current pacman and pill.
	 * @param game: Copy of current game.
	 * @return The index of nearest ghost to pacman. 
	 */
	public int getNearestEdibleGhostDistance(Game game) {
		int currentNodeIndex = game.getPacmanCurrentNodeIndex();
		int distMin			 = Integer.MAX_VALUE;
		
		for(GHOST g : GHOST.values()){
			int targetNodeIndex = game.getGhostCurrentNodeIndex(g);
			if (game.isGhostEdible(g))
				distMin = (int) Math.min(distMin, game.getDistance(currentNodeIndex, targetNodeIndex, DM.PATH));
		}
		//if(distMin==-1) distMin = Integer.MAX_VALUE;
		return distMin;
	}
	/** 
	 * Gets indices of average ghost using Manhattan distance between current pacman and pill.
	 * @param game: Copy of current game.
	 * @return The index of average ghost to pacman. 
	 */
	public int getAverageGhostDistance(Game game) {
		int currentNodeIndex = game.getPacmanCurrentNodeIndex();
		int distTot			 = Integer.MAX_VALUE;
		
		for(GHOST g : GHOST.values()){
			int targetNodeIndex = game.getGhostCurrentNodeIndex(g);
			distTot += (int) game.getDistance(currentNodeIndex, targetNodeIndex, DM.PATH);
		}
		//if(distMin==-1) distMin = Integer.MAX_VALUE;
		return distTot / 4;
	}
}
