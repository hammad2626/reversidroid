package org.cherchi.reversi.logic.internal;

import org.cherchi.reversi.logic.GameEventsListener;
import org.cherchi.reversi.logic.GameLogic;


/**
 * The thread for the machine thinking
 * @author Fernando Cherchi
 *
 */
public class MachineThread implements Runnable {

	// /////////////////////// PRIVATE FIELDS //////////////////////////

	/**
	 * The matrix
	 */
	private GameLogic gameLogic;
	
	/**
	 * This is the object to be notified of the game situation
	 */
	private GameEventsListener gameEventsListener;

	
	
	
	// /////////////////////// LIFETIME ////////////////////////////////
	
	// /////////////////////// PUBLIC METHODS //////////////////////////
	/**
	 * The background process
	 */
	@Override
	public void run() {
		
		boolean playerHasChanged;

		
		do {
			Movement machineMovement = this.machinePlays();
			if (machineMovement != null) {
				playerHasChanged = this.doMovement(GameLogic.PLAYER_TWO,
						machineMovement.getColumn(), machineMovement
								.getRow());
			} else {
				//if machine movement is null.. machine cannot play 
				playerHasChanged = true;
			}
			// it can happen that the human can not play...
		} while (!playerHasChanged);
		
	}
	
	// /////////////////////// PRIVATE METHODS //////////////////////////


	/**
	 * Calculates the machine movement
	 * @return
	 */
	private Movement machinePlays() {

		AI ai = new AI(this.gameLogic.getBoard());
		Movement best = ai.getBestMove(GameLogic.PLAYER_TWO);
		return best;
	}
	
	/**
	 * makes a movement, conquer positions, update possible positions, toggles
	 * player
	 * 
	 * @param player
	 * @param col
	 * @param row
	 * @return if the turn has to change
	 */
	private boolean doMovement(int player, int col, int row) {

		boolean changePlayer = false;
		if (this.gameLogic.canSet(player, col, row)) {
			this.gameLogic.setStone(player, col, row);
			this.gameLogic.conquerPosition(player, col, row);
			changePlayer = this.togglePlayer();
		}
		this.notifyChanges();
		return changePlayer;
	}
	
	
	/**
	 * Notifies the listener for the changes occurred in the game
	 */
	private void notifyChanges() {
		if (this.gameEventsListener != null) {

			int p1 = this.gameLogic.getCounterForPlayer(GameLogic.PLAYER_ONE);
			int p2 = this.gameLogic.getCounterForPlayer(GameLogic.PLAYER_TWO);

			this.gameEventsListener.onScoreChanged(p1, p2);

			if (this.gameLogic.isFinished()) {
				int winner = GameLogic.EMPTY;
				if (p1 > p2) {
					winner = GameLogic.PLAYER_ONE;
				} else if (p2 > p1) {
					winner = GameLogic.PLAYER_TWO;
				}
				this.gameEventsListener.onGameFinished(winner);
			}
		}
	}
	

	/**
	 * Changes the player
	 * 
	 * @return if the player has been toggled (if the opponent player can play)
	 */
	private boolean togglePlayer() {

		int current = this.gameLogic.getCurrentPlayer();
		boolean toggled;
		// if the next player can play (has at least one place to put the
		// chip)
		if (!this.gameLogic.isBlockedPlayer(GameUtils.opponent(current))) {
			// just toggles
			this.gameLogic.setCurrentPlayer(GameUtils.opponent(current));
			toggled = true;
		} else {
			System.out.println(String.format(
					"player %d cannot play!!!!!!!!!!!!!!!!!!!", GameUtils
							.opponent(current)));
			toggled = false;
		}
		return toggled;
	}
	
	// /////////////////////// ACCESSORS //////////////////////////

	/**
	 * Setter for the game logic
	 */
	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
	}

	/**
	 * Sets the game event listener
	 * @param gameEventsListener
	 */
	public void setGameEventsListener(GameEventsListener gameEventsListener) {
		this.gameEventsListener = gameEventsListener;
	}

}
