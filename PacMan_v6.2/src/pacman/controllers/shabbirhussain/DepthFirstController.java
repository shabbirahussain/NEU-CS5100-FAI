/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers.shabbirhussain;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import pacman.controllers.Controller;
import pacman.controllers.examples.RandomGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

/**
 *
 * @author Shabbir Hussain
 */
public class DepthFirstController extends Controller<MOVE> {
	private static final int MAX_DEPTH = 30;
	public static RandomGhosts ghosts = new RandomGhosts();
	
	private static final Boolean USE_MOVE_MEMORY = false;
	private Queue<MOVE> moveList; // Stores moves if solution is found
	private int targetIndexSave;
	/**
	 * Default Constructor
	 */
	public DepthFirstController(){
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
			PacManNode solution = depthFirstSearch(pmNode, targetIndex, MAX_DEPTH);
			
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
	 * Performs depth first search from pacman to target index.
	 * @param game : Copy of current game.
	 * @param targetIndex : Index to which search has to be performed.
	 * @param maxdepth : Maximum depth search is allowed to go.
	 * @return A MOVE which leads to a path closer to target. null if no paths found within limit.
	 */
	public PacManNode depthFirstSearch(final PacManNode pmStartNode, final int targetIndex, final int maxdepth) {
		Stack<PacManNode> dfsStack = new Stack<PacManNode>();
		dfsStack.push(pmStartNode);
		PacManNode pmNode = null;

		while (!dfsStack.isEmpty()) {
			pmNode = dfsStack.pop(); // Pop topmost node from stack.
			
			int currentIndex = pmNode.game.getPacmanCurrentNodeIndex();
			if (currentIndex == targetIndex) break; // Found target

			// Continue searching
			if (pmNode.depth <= maxdepth) {
				LinkedList<PacManNode> pmNodeChildren = getAllChildren4Depth0(pmNode);
				for(PacManNode pmChild : pmNodeChildren){
					// If path leads to ghost cut the branch, do not add it to stack.
					if (pmNode.game.wasPacManEaten())  continue;
					
					// Add next state to stack.
					dfsStack.push(pmChild);				
				}
			}
		}
		return pmNode; // No route found for current depth.
	}
	/**
	 * Generates states of Ms. Pacman.
	 * @param pmNode: Current game state in PacManNode
	 * @return: A list of new PacManNode after advancing game
	 */
	private LinkedList<PacManNode> getAllChildren4Depth0(PacManNode pmNode){
		int currentNodeIndex = pmNode.game.getPacmanCurrentNodeIndex();
		MOVE lastMove        = pmNode.game.getPacmanLastMoveMade();
		MOVE[] availableMoves;
		
		if (pmNode.depth == 0)
			availableMoves = pmNode.game.getPossibleMoves(currentNodeIndex);
		else
			availableMoves = pmNode.game.getPossibleMoves(currentNodeIndex, lastMove);
		
		
		LinkedList<PacManNode> pmChildNodes = new LinkedList<PacManNode>();
		for(MOVE m : availableMoves){
			// Create a child
			Queue<MOVE> movesQueue = new LinkedList<MOVE>(pmNode.moves);
			movesQueue.add(m);
			
			Game gameCopy = pmNode.game.copy();
			gameCopy.advanceGame(m, ghosts.getMove());
			
			PacManNode pmChildNode = new PacManNode(gameCopy, pmNode.depth + 1, movesQueue);
			pmChildNodes.add(pmChildNode);
			
			int nextNodeIndex = pmChildNode.game.getPacmanCurrentNodeIndex();
			GameView.addLines(pmNode.game, Color.MAGENTA, currentNodeIndex, nextNodeIndex);
		}
		return pmChildNodes;
	}
}
