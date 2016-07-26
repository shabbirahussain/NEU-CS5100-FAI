/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entrants.pacman.shabbir;

import java.util.Comparator;
import java.util.Queue;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Stores future game state with depth and moves information.
 * @author Shabbir Hussain
 */
public class PacManNode implements Comparable<PacManNode>{
    Game game;
    int depth;
    Queue<MOVE> moves;
    double minimaxScore;
    
    /**
     * Creates a new PacManNode.
     * @param game: Current game state.
     * @param depth: Distance from root state of game from which search is started.
     * @param moves: Moves which lead to current state starting from root state.
     */
    public PacManNode(Game game, int depth, Queue<MOVE> moves) {
        this.game = game;
        this.depth = depth;
        this.moves = moves;
    }
    /**
     * Updates minimax score to PacManNode
     * @param score: Is the score given.
     */
    public void updateMinimaxScore(double score) {
        this.minimaxScore = score;
    }
    /**
     * Returns string representation of object
     */
    @Override
    public String toString(){
    	int currentNodeIndex = this.game.getPacmanCurrentNodeIndex();
    	String s = "[Index=" + currentNodeIndex + "]"
    			+ "[Depth=" + this.depth + "]"
    			+ "[MiniMax=" + this.minimaxScore + "]"
    			+ "[Moves=" + this.moves + "]";
    	return s;
    } 
    
	public int compareTo(PacManNode pn2){
		if (this.minimaxScore == pn2.minimaxScore) return 0;
    	return (this.minimaxScore > pn2.minimaxScore) ? -1 : 1;
	}
}	
