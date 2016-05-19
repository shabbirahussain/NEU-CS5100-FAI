/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers.shabbirhussain;

import java.awt.Color;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.jnn.main.Executor;

import java.util.ArrayList;
import java.util.Comparator;

import pacman.controllers.Controller;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.shabbirhussain.logger.Logger;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

/**
 *
 * @author Shabbir Hussain
 */
public class PerceptronController extends Controller<MOVE> {
	private static final Logger lgLogger = new Logger("");
	/**
	 * Default Constructor
	 * @throws IOException 
	 */
	public PerceptronController(){
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
		String features = lgLogger.extractFeatures(game);
		Double nnOutput = Executor.run(features);
		MOVE   nnMove   = lgLogger.unScaleMove(nnOutput);
		System.out.println("O/p="+ nnMove);
		
		return nnMove;
	}
}
