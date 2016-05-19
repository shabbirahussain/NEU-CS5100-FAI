/**
 * 
 */
package pacman.controllers.shabbirhussain.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * @author shabbirhussain
 *
 */
public class Logger {
	public String TRAINING_FILE_LOC ;
	
	private BufferedWriter bw;
	public Logger(String filePath) {
		// TODO Auto-generated constructor stub
		TRAINING_FILE_LOC = filePath; //= "/Users/shabbirhussain/Documents/Temp/PacmanData/run1.dat";
		
	}
	/**
	 * logs the game to a file
	 * @param game
	 * @param action
	 */
	public void log(Game game, MOVE action) {
		StringBuilder sb     = new StringBuilder();
		
		// Build output
		sb.append(scaleMove(action)+",");
		
		// Append features
		sb.append(this.extractFeatures(game));
		
		try{
			File file = new File(TRAINING_FILE_LOC);
			if (!file.exists()) 
				file.createNewFile();
			
			bw = new BufferedWriter(new FileWriter(file, true));
			System.out.print(sb);
			bw.write(sb.toString());
			bw.close();
		}catch(Exception e){
			System.out.println(e.getStackTrace());
		}
	}
	
	/**
	 * Scales the given move
	 * @param m
	 * @return
	 */
	public Double scaleMove(MOVE m){
		return ((m.ordinal()+1)/4.0);
	}
	
	/**
	 * Returns percentage to move mapping
	 * @param x
	 * @return
	 */
	public MOVE unScaleMove(Double x){
		x = (double) Math.round(x*100);
		Double temp= x; //Math.max(0, Math.min(1, x));
		
		System.out.println(x);
		if(temp<=-5)  return MOVE.DOWN;
		if(temp<=0)  return MOVE.LEFT;
		if(temp<=+5)  return MOVE.RIGHT;
		return MOVE.UP;
	}
	
	/**
	 * Extracts features from game
	 * @param game
	 * @return
	 */
	public String extractFeatures(Game game){
		StringBuilder sb     = new StringBuilder();
		StringBuilder sbPill = new StringBuilder();
		StringBuilder sbGhst = new StringBuilder();
		
		
		// Append features
		// Pill direction
		Integer pacManNodeIndex = game.getPacmanCurrentNodeIndex();
		Integer nPillIdx = game.getClosestNodeIndexFromNodeIndex(pacManNodeIndex, game.getActivePillsIndices(), DM.PATH);	
		MOVE pmanBstMove = game.getApproximateNextMoveTowardsTarget(pacManNodeIndex, nPillIdx, game.getPacmanLastMoveMade(), DM.PATH);
		
		
		//Integer nGhstIdx = game.getClosestNodeIndexFromNodeIndex(game.getGhostCurrentNodeIndex(ghostType)), game.getActivePillsIndices(), DM.PATH);
		Double minGhstDistance = 4.0;
		for(MOVE pacManMove : MOVE.values()){
			Game gameCopy = game.copy();
			gameCopy.updatePacMan(pacManMove);
			
			sbPill.append((pmanBstMove == pacManMove)? 1 : 0);
			sbPill.append(",");
			
			for(GHOST g:GHOST.values()){
				Double ghstDistance = game.getDistance(gameCopy.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(g), DM.PATH);
				if(ghstDistance>=0)
					minGhstDistance = Math.min(minGhstDistance, ghstDistance);
			}
			sbGhst.append((minGhstDistance < 4.0)? 1 : 0);
			sbGhst.append(",");
		}	
		sb.append(sbPill);
		sb.append(sbGhst);
		sb.setLength(sb.length()-1);
		sb.append("\n");
		
		return sb.toString();
	}
}
