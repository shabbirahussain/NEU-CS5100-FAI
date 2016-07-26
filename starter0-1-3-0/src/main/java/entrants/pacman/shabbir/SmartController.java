/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entrants.pacman.shabbir;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import pacman.controllers.Controller;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.RandomGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

/**
 *
 * @author Shabbir Hussain
 */
public class SmartController extends Controller<MOVE> {
	private static final int MAX_DEPTH = 15;
	private static final int MAX_TIME  = 15; 
	private static final int MAX_POOL_SIZE = 10;
	private static final int EVOLUTION_LIMIT = 10;
	private static Random rnd = new Random();
	
	//private static AggressiveGhosts ghosts = new AggressiveGhosts();
	private static RandomGhosts ghosts = new RandomGhosts();
	private static boolean DEBUG = false; 
	
	public static Comparator<PacManNode> pmanNodeComparator =
		new Comparator<PacManNode>() {
	        public int compare(PacManNode pn1, PacManNode pn2) {
	        	if (pn1.minimaxScore == pn2.minimaxScore) return 0;
	        	return (pn1.minimaxScore > pn2.minimaxScore) ? -1 : 1;
	        }};
	        
	private static final Boolean USE_MOVE_MEMORY = false;
	private Queue<MOVE> moveList; // Stores moves if solution is found
	private int targetIndexSave;
	
	
	/**
	 * Default Constructor
	 * @throws IOException 
	 */
	public SmartController(){
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
		//System.out.println(Arrays.asList(game.getActivePillsIndices()));
//		for(int i: game.getActivePillsIndices()){
//			System.out.print(","+i);
//		}
//		System.out.println(game.getActivePillsIndices().length 
//				+ "\t" 
//				+ game.getNumberOfActivePills());
		
		//System.out.println("Starting new Search =======================>");
		//if(game.wasPacManEaten()) {int i=1/0;}
		//System.out.println(game.getGameState());
		//System.out.println(game);
		  
		if(!USE_MOVE_MEMORY || this.moveList.isEmpty()){ // start new search
			this.moveList.clear();
			PacManNode pmNode = new PacManNode(game, 0, new LinkedList<MOVE>());
			PacManNode solution = iSmartController(pmNode, MAX_DEPTH);
			
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
		}
		//System.out.println("Solution Moves Set:" + this.moveList);
		
		if (moveList.isEmpty()){	 // Can't do anything. No solutions exists within limits. use a random move.			
			return SearchHelper.getRandomMove(game); 
		}
		MOVE action = moveList.remove();
		//lgr.log(game, action);
		return action;
	}
	/**
	 * Gets best move using steep hill climbing.
	 * @param game: Current state of game.
	 * @param targetIndex: Target index in the maze to find distance.
	 * @param maxdepth: Maximum depth a search has to be performed.
	 * @return A PacManNode with best score and move list.
	 */
	public PacManNode iSmartController(final PacManNode pmStartNode, final int maxdepth) {
		List<PacManNode> openList   = new ArrayList<PacManNode>();
		List<PacManNode> oldList    = new ArrayList<PacManNode>();
		List<PacManNode> closedList = new ArrayList<PacManNode>();

		openList.addAll(getNeighbors(pmStartNode, pmStartNode));			

		int timeElapsed = 0;
		while (!openList.isEmpty() && (timeElapsed++ < MAX_TIME)) {
			//System.out.println("Elapsed=" + timeElapsed);
			
			PacManNode pmNode;
			// Evolve each node in parents pool
			for(int i=0; i<openList.size() && i<=EVOLUTION_LIMIT; i++){
				pmNode = openList.remove(0);
				//System.out.println("i="+i+"\t"+pmNode);
				// Explore only newly created children
				//if(pmNode.depth == (timeElapsed-1))
				//closedList.add(pmNode); 
				openList.addAll(getNeighbors(pmStartNode, pmNode));
			}
			// Discard tail beyond maximum of pool
			openList.sort(pmanNodeComparator);
			openList = openList.subList(0, Math.min(openList.size(), MAX_POOL_SIZE));
			

			/*
			for(int i=0; i<openList.size() && i<=EVOLUTION_LIMIT; i++)
				if(oldList.contains(openList.get(i))){
					System.out.println("i="+i+"\topenList="+openList.get(i) + "\toldList="+oldList.get(i));
					System.out.println("Old Team member loaded:");
					//int j =1/0;
					break;
					
				}
			oldList = openList;
			//*/
		};
		//closedList.addAll(openList);
		//closedList.sort(pmanNodeComparator);
		
		return openList.remove(0);
	}
	/**
	 * Generates heuristic value of each node state
	 * @param pmNode: Current state of game in terms of PacManNode
	 * @return: Heuristic value of PacManNode
	 */
	public static double getHeuristicValue(PacManNode pmStartNode, PacManNode pmNode){
		double heuristicScore 	 = 0;
		
		// Objective 1 [Value:E^6] : Stay Alive	
		double oldLives          = pmStartNode.game.getPacmanNumberOfLivesRemaining();
		double newLives 		 = pmNode.game.getPacmanNumberOfLivesRemaining();
		newLives		 		 = Integer.MIN_VALUE * (oldLives - newLives);
		heuristicScore 			+= newLives;
		
		// Objective 2 [Value:E^2]: Complete Level priority
		double oldLevelTime		 = pmStartNode.game.getCurrentLevelTime();
		double newLevelTime  	 = pmNode.game.getCurrentLevelTime();
		//newLevelTime		 	 = (newLevelTime==0)? Integer.MAX_VALUE : (newLevelTime - oldLevelTime);
		newLevelTime		 	 = (newLevelTime==0)? Integer.MAX_VALUE : -0 * (newLevelTime - oldLevelTime);
		heuristicScore 			+= newLevelTime;
		
		// Objective 3 [Value:E^3]: Keep safe distance from Ghosts Min 10
		double oldSafetyDist	 = getNormalGhostDistance(pmStartNode.game);
		double newSafetyDist	 = getNormalGhostDistance(pmNode.game);
		double newSafetyDist1	 = (Math.min(newSafetyDist, 10) - Math.min(oldSafetyDist, 10)) * 500;
		heuristicScore 			+= newSafetyDist1;
		
		// Objective 3 [Value:E^3]: Keep safe distance from Ghosts Min 10
		double oldAverageDist	 = getAverageGhostDistance(pmStartNode.game);
		double newAverageDist	 = getAverageGhostDistance(pmNode.game);
		newAverageDist	 		 = (Math.min(newAverageDist, 10) - Math.min(oldAverageDist, 10)) * 100;
		//heuristicScore 			+= newAverageDist;
		
		
		
		// Objective 4 [Value:E^4]: Eat Ghost
		double numOfGhostEaten   = pmNode.game.getNumGhostsEaten();
		numOfGhostEaten 		 = numOfGhostEaten * 10000;
		heuristicScore 			+= numOfGhostEaten;

		// Objective 6 [Value:E^3]: Eat food pill
		double oldActivePillCnt	 = pmStartNode.game.getNumberOfActivePills();
		double newActivePillCnt	 = pmNode.game.getNumberOfActivePills();
		newActivePillCnt	  	 = 1000			*  (oldActivePillCnt - newActivePillCnt);
		heuristicScore 			+= newActivePillCnt;

		// Objective 6 [Value:E^4]: Avoid power pill
		double oldPowerCnt		 = pmStartNode.game.getNumberOfPowerPills();
		double newPowerCnt		 = pmNode.game.getNumberOfPowerPills();
		newPowerCnt		 	 	 = -10000 		* (oldPowerCnt - newPowerCnt);
		heuristicScore 			+= newPowerCnt;
				
		
		// Objective 5 [Value:E^3]: Try to eat ghosts
		double newDisToFeast = 0;
		if(numOfGhostEaten==0){
			double oldDisToFeast	 = getEdibleGhostDistance(pmStartNode.game);
			newDisToFeast	 		 = getEdibleGhostDistance(pmNode.game);
			newDisToFeast		 	 = 1000 	* (oldDisToFeast - newDisToFeast);
			heuristicScore 			+= newDisToFeast;
		}
		
	
		// Objective 5 [Value:E^1]: Try to get to a the pill
		double newFoodDist = 0;
		if(newActivePillCnt==0 && newPowerCnt==0){
			double oldFoodDist		 = pmStartNode.game.getEuclideanDistance(pmStartNode.game.getPacmanCurrentNodeIndex(), SearchHelper.getNearestActivePill(pmStartNode.game));
			newFoodDist  	 = pmNode.game.getEuclideanDistance(pmNode.game.getPacmanCurrentNodeIndex(), SearchHelper.getNearestActivePill(pmNode.game));
			newFoodDist		 	 	 = 100			* (oldFoodDist - newFoodDist);
			heuristicScore 			+= newFoodDist;
		}
		

		//heuristicScore 			 	+= pmStartNode.minimaxScore;
		heuristicScore 			 	+= pmStartNode.minimaxScore * pmStartNode.depth;
		heuristicScore				/= pmNode.depth;
		
		
		// Slash score for time penalty
		//if(TIME_PENALTY!=0)
		//	heuristicScore /= TIME_PENALTY * newLevelTime;
		
		
		if(DEBUG){
			System.out.print("\n"+pmNode.moves);
			System.out.print("\n prev score\t\t= "		+pmStartNode.minimaxScore);
			System.out.print("\n newLives\t\t= "		+newLives);
			System.out.print("\n newLevelTime\t\t= "	+newLevelTime);
			System.out.print("\n newFoodDist\t\t= "		+newFoodDist);
			System.out.print("\n newActivePillCnt\t= "	+newActivePillCnt);
			System.out.print("\n newPowerCnt\t\t= "		+newPowerCnt);
			System.out.print("\n newSafetyDist\t\t= "	+newSafetyDist1 +"\told="+oldSafetyDist+"\tnew="+newSafetyDist);
			System.out.print("\n newDisToFeast\t\t= "	+newDisToFeast);
			System.out.print("\n numOfGhostEaten\t= "	+numOfGhostEaten);
			System.out.print("\n heuristicScore\t= "	+heuristicScore);
			System.out.print("\n");
		}
		return heuristicScore;
	}
	
	/**
	 * Gets indices of nearest ghost using Manhattan distance between current pacman and pill.
	 * @param game: Copy of current game.
	 * @return The index of nearest ghost to pacman. 
	 */
	public static double getNormalGhostDistance(Game game) {
		int currentNodeIndex = game.getPacmanCurrentNodeIndex();
		double distMin			 = 100;
		
		for(GHOST ghostType : GHOST.values()){
			if ((game.getGhostLairTime(ghostType)!=-1) && !game.isGhostEdible(ghostType)){
				int targetNodeIndex = game.getGhostCurrentNodeIndex(ghostType);
				int distance = game.getShortestPathDistance(currentNodeIndex, targetNodeIndex);
				if(distance>=0)
					distMin = Math.min(distMin, distance);
			}
		}
		return distMin;
	}
	/**
	 * Gets indices of average ghost using Manhattan distance between current pacman and pill.
	 * @param game: Copy of current game.
	 * @return The index of nearest ghost to pacman. 
	 */
	public static double getAverageGhostDistance(Game game) {
		int currentNodeIndex = game.getPacmanCurrentNodeIndex();
		double distMin		 = 0;
		
		for(GHOST ghostType : GHOST.values()){
			if ((game.getGhostLairTime(ghostType)!=-1) && !game.isGhostEdible(ghostType)){
				int targetNodeIndex = game.getGhostCurrentNodeIndex(ghostType);
				int distance = game.getShortestPathDistance(currentNodeIndex, targetNodeIndex);
				if(distance>=0)
					distMin +=  distance;
			}
		}
		return distMin / GHOST.values().length;
	}
	/**
	 * Gets indices of nearest edible ghost using Manhattan distance between current pacman and pill.
	 * @param game: Copy of current game.
	 * @return The index of nearest ghost to pacman. 
	 */
	public static double getEdibleGhostDistance(Game game) {
		int currentNodeIndex = game.getPacmanCurrentNodeIndex();
		double distMin			 = 100;
		
		for(GHOST ghostType : GHOST.values()){
			if ((game.getGhostLairTime(ghostType)!=-1) && game.isGhostEdible(ghostType)){
				int targetNodeIndex = game.getGhostCurrentNodeIndex(ghostType);
				int distance = game.getShortestPathDistance(currentNodeIndex, targetNodeIndex);
				if(distance>=0)
					distMin = Math.min(distMin, distance);
				//distMin = Math.min(distMin, game.getDistance(currentNodeIndex, targetNodeIndex, DM.PATH));
			}
		}
		return distMin;
	}
		
	/**
	 * Returns neighbors of current game state.
	 * @param game 		: Copy of current game.
	 * @return A list of games advanced by distance steps with randomly generated neighbors.
	 */
	public LinkedList<PacManNode> getNeighbors(final PacManNode pmStartNode, final PacManNode pmNode){
		LinkedList<PacManNode> neighbors = new LinkedList<PacManNode>();
		PacManNode pmNode2;
		MOVE moves[] = pmNode.game.getPossibleMoves(pmNode.game.getPacmanCurrentNodeIndex());
		for(MOVE pacManMove : moves){
			Queue<MOVE> currentMoves = new LinkedList<MOVE>(pmNode.moves);
			Game gameCopy = pmNode.game.copy();
			try{
				gameCopy.advanceGame(pacManMove, ghosts.getMove(gameCopy, 0));
			}catch(Exception e){
				gameCopy.updatePacMan(pacManMove);
			}
			
			currentMoves.add(pacManMove);
			pmNode2 = new PacManNode(gameCopy, pmNode.depth+1, currentMoves);
			pmNode2.updateMinimaxScore(getHeuristicValue(pmNode, pmNode2));
			
			int currentIndex = pmNode.game.getPacmanCurrentNodeIndex();
			GameView.addLines(pmNode.game, Color.MAGENTA, currentIndex, gameCopy.getPacmanCurrentNodeIndex());
			
			neighbors.add(pmNode2);
		}

		// Add random neighbors to tackle local maxima 
		if(MAX_DEPTH > 0){
			pmNode2 = getNeighbor(pmNode, 1+rnd.nextInt(MAX_DEPTH));
			pmNode2.updateMinimaxScore(getHeuristicValue(pmNode, pmNode2));
			neighbors.add(pmNode2);
		}
		
		return neighbors;
	}
	
	/**
	 * Returns neighbor of current game state.
	 * @param game 		: Copy of current game.
	 * @param atDepth	: Neighbor generation distance.
	 * @return A PacManNode with Game advanced by distance steps with randomly generated neighbors.
	 */
	public PacManNode getNeighbor(PacManNode pmnode1, int atDepth){
		MOVE pacManMove;
		PacManNode pmnode2;
		Queue<MOVE> currentMoves = new LinkedList<MOVE>(pmnode1.moves);
		Game gameCopy = pmnode1.game.copy();
		int i;
		for(i=0; i < atDepth; i++){
			if (false & i == 0)
				pacManMove = SearchHelper.getRandomMoveWithBacktracking(gameCopy);
			else // DO NOT GO BACK;
				pacManMove = SearchHelper.getRandomMove(gameCopy);
			
			int currentIndex = gameCopy.getPacmanCurrentNodeIndex();
			try{
				gameCopy.advanceGame(pacManMove, ghosts.getMove(gameCopy, 0));
			}catch(Exception e){
				gameCopy.updatePacMan(pacManMove);
			}
			currentMoves.add(pacManMove);
			
			// If path leads to ghost cut the branch after atleast one move
			// if (gameCopy.wasPacManEaten()) break;
			GameView.addLines(pmnode1.game, Color.MAGENTA, currentIndex, gameCopy.getPacmanCurrentNodeIndex());
		}
		pmnode2 = new PacManNode(gameCopy, i, currentMoves);
		pmnode2.updateMinimaxScore(SmartController.getHeuristicValue(pmnode1, pmnode2));
		
		return pmnode2;
	}
}
