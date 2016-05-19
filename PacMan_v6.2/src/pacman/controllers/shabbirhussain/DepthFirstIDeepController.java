/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers.shabbirhussain;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

/**
 *
 * @author Shabbir Hussain
 */
public class DepthFirstIDeepController extends Controller<MOVE> {
	private static final int MAX_DEPTH = 100;
	private static DepthFirstController dfsController = new DepthFirstController(); 
	
	private static final Boolean USE_MOVE_MEMORY = false;
	private Queue<MOVE> moveList; // Stores moves if solution is found
	private int targetIndexSave;
	/**
	 * Default Constructor
	 */
	public DepthFirstIDeepController(){
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
			PacManNode solution = depthFirstSearchIDeep(pmNode, targetIndex, MAX_DEPTH);
			
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
	 * Performs depth first iterative deepening search from pacman to target index.
	 * @param game : Copy of current game.
	 * @param targetIndex : Index to which search has to be performed.
	 * @param maxdepth : Maximum depth search is allowed to go.
	 * @return A MOVE which leads to a path closer to target. null if no paths found within limit.
	 */
	private PacManNode depthFirstSearchIDeep(final PacManNode pmNode, final int targetIndex, final int maxdepth) {
		PacManNode solution = null;
		for (int i = 10; i <= maxdepth; i+=10) {
			System.out.println("\nStarting DFS with depth: " + i + " Target: " + targetIndex);
			
			// Run dfs with iterative deepening.
			solution = dfsController.depthFirstSearch(pmNode, targetIndex, i); 
			
			if (solution.game.getPacmanCurrentNodeIndex() == targetIndex) break;
		}
		return solution;
	}
}
