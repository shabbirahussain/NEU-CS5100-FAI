/**
 * 
 */
package pacman.controllers.shabbirhussain;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.MOVE;

/**
 * @author Shabbir Hussain
 * 
 * Helper class with utility functions for search algorithms.
 */
public class SearchHelper {
	private  static Random rnd = new Random();
	/**
	 * Gets the distance between current state of game of targetIndex
	 * @param game: Copy of current game. 
	 * @param targetIndex: Target index in the maze to find distance.
	 * @return
	 */
	public static int getDistanceFromCurrPos(final Game game, final int targetIndex){
		return game.getManhattanDistance(game.getPacmanCurrentNodeIndex(), targetIndex);
	}
	
	/**
	 * Gets indices of nearest active pills using Manhattan distance between current pacman and pill.
	 * @param game: Copy of current game.
	 * @return The index of nearest activepill to pacman. If no more pills are there returns -1.
	 */
	public static int getNearestActivePill(Game game) {
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
		double minDistance = Integer.MAX_VALUE;
		int nearestPillIndex = -1;

		for (int anyPillIndex : allPillsIndices) {
			//double distance = game.getManhattanDistance(currentNodeIndex, anyPillIndex);
			double distance = game.getEuclideanDistance(currentNodeIndex, anyPillIndex);
			if (distance < minDistance) {
				minDistance = distance;
				nearestPillIndex = anyPillIndex;
			}
		}
		GameView.addPoints(game, Color.GREEN, nearestPillIndex);
		return nearestPillIndex;		
	}
	
	/**
	 * Generates a random move for current game, from valid moves set where no backtracking is allowed.
	 * @param game: Copy of current game.
	 * @return A MOVE from available set of valid moves.
	 */
	public static MOVE getRandomMove(Game game){
		MOVE[] allMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
		return allMoves[rnd.nextInt(allMoves.length)]; 
	}
	/**
	 * Generates a random move for current game, from valid moves set where backtracking is allowed.
	 * @param game: Copy of current game.
	 * @return A MOVE from available set of valid moves.
	 */
	public static MOVE getRandomMoveWithBacktracking(Game game){
		MOVE[] allMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		return allMoves[rnd.nextInt(allMoves.length)]; 
	}
}
