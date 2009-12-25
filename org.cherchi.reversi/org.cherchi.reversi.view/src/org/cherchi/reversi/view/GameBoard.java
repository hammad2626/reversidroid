package org.cherchi.reversi.view;

import org.cherchi.reversi.logic.GameFacade;
import org.cherchi.reversi.logic.GameLogic;
import org.cherchi.reversi.logic.internal.GameFacadeImpl;
import org.cherchi.reversi.logic.internal.GameLogicImpl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * This is the game board canvas
 * 
 * @author Fernando Cherchi
 * 
 */
public class GameBoard extends View {

	// //////////////////// CONSTANTS ///////////////////////////////////

	/**
	 * The number of columns of this board
	 */
	private static int NUMBER_OF_COLUMNS = 8;

	/**
	 * The number of rows of this board
	 */
	private static int NUMBER_OF_ROWS = 8;

	/**
	 * The top margin
	 */
	private static int TOP_MARGIN = 0;

	/**
	 * Vertical margin
	 */
	private static int LEFT_MARGIN = 0;

	// //////////////////// FIELDS ///////////////////////////////////

	/**
	 * Access to the game facade. Interface
	 */
	private GameFacade gameFacade;

	/**
	 * access to the graphic system
	 */
	private Canvas canvas;

	/**
	 * dimensions
	 */
	private int width;

	private int height;

	private int cellWidth;

	private int cellHeight;

	private int backgroundColor = Color.rgb(50, 50, 50);

	/**
	 * The color used by the player 1
	 */
	private int playerOneColor = Color.BLUE;
	private int playerOneInsideColor = Color.rgb(0, 0, 150);

	/**
	 * Color used by player 2
	 */
	private int playerTwoColor = Color.RED;
	private int playerTwoInsideColor = Color.rgb(150, 0, 0);

	/**
	 * indicates if the help of possible position is selected
	 */
	private boolean markAllowedPositionIsSelected = true;

	/**
	 * The first time parameters are not calculated
	 */
	private boolean isNotCalulatedParameters = true;

	// ///////////////////////////////// LIFETIME //////////////////////////////
	/**
	 * Default constructor
	 */
	public GameBoard(Context context, AttributeSet attr) {
		super(context, attr);

		// ok, I am not going to use spring for such a simple program,
		// but the POJO design, supports spring injection, is testable and
		// mockable!
		this.gameFacade = new GameFacadeImpl();
		this.gameFacade.setGameLogic(new GameLogicImpl());

	}

	// /////////////////////// EVENTS ////////////////////////////////////////

	/**
	 * Occurs when drawing the board
	 * 
	 * @param canvas
	 */
	public void onDraw(Canvas canvas) {

		this.canvas = canvas;

		if (isNotCalulatedParameters) {
			this.calculateGraphicParameters();
			this.isNotCalulatedParameters = false;
		}
		//this.setBackgroundColor(this.backgroundColor);
		// drawing the game board
		this.drawBoard();

		// setting all possible position (can be only for level 1)
		this.markAllowedPositions(this.gameFacade.getCurrentPlayer());

	}

	/**
	 * Occurs when user touches the screen
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();
		// is this a click?
		if (action == MotionEvent.ACTION_DOWN) {

			float x = event.getX();
			float y = event.getY();
			// getting column and row where the click was perfomed
			int col = transformCoordinateXInColumn(x);
			int row = transformCoordinateYInRow(y);

			this.setPosition(col, row, this.gameFacade.getCurrentPlayer());
			this.invalidate();
			this.refreshCounters();
			
			return true;
		} else {
			return false;
		}

	};

	
	// ////////////////////// PUBLIC METHODS /////////////////////////////

	// ////////////////////// PRIVATE METHODS ////////////////////////////
	
	/**
	 * Paints the counters
	 */
	private void refreshCounters() {
		
		int one = this.gameFacade.getCounterForPlayer(GameFacade.PLAYER_ONE);
	}


	/**
	 * draws a fill circle in the column and row given having in mind the player
	 * color
	 */
	private void setPosition(int col, int row, int player) {

		this.gameFacade.set(player, col, row);
	}

	/**
	 * converts a coordinate into a row
	 * 
	 * @param y
	 * @return
	 */
	private int transformCoordinateYInRow(float y) {

		int row = (int) ((y - TOP_MARGIN) / this.cellHeight);

		// if tapped outside the board
		if (row < 0 || row >= GameLogic.ROWS) {
			row = -1;
		}

		return row;
	}

	/**
	 * converts a coordinate into a column
	 * 
	 * @param x
	 * @return
	 */
	private int transformCoordinateXInColumn(float x) {

		int col = (int) ((x - LEFT_MARGIN) / this.cellWidth);

		// if tapped outside the board
		if (col < 0 || col >= GameLogic.COLS) {
			col = -1;
		}

		return col;
	}

	/**
	 * draws a circle in the column and row given having in mind the player
	 * color
	 */
	private void drawPosition(int col, int row, int player, boolean fill) {

		// calculating the center of the cell
		int cellMediumX = (col * this.cellWidth + (col + 1) * this.cellWidth) / 2;
		int cellMediumY = (row * this.cellHeight + (row + 1) * this.cellHeight) / 2;

		// applying the margins
		int cx = cellMediumX + LEFT_MARGIN;
		int cy = cellMediumY + TOP_MARGIN;
		// now the radius
		int radius = (this.cellWidth - 2) / 2 - 2;

		// just painting a circle of the player's color
		int borderColor = this.getColorForPlayer(player);
		int insideColor = this.getInsideColorForPlayer(player);
		this.drawCircle(borderColor, insideColor, fill, cx, cy, radius);

	}

	/**
	 * Just draws a circle with the given style
	 * 
	 * @param borderColor
	 *            color of the chip
	 * @param isSolid
	 *            if is solid or just the border (transparent)
	 * @param cx
	 *            center x
	 * @param cy
	 *            center y
	 * @param radius
	 *            size of the chip
	 */
	private void drawCircle(int borderColor, int insideColor, boolean isSolid,
			int cx, int cy, int radius) {

		Paint paint = new Paint();

		if (!isSolid) {
			// if the shadow is being painted, changing alpha channel
			borderColor = Color.argb(77, Color.red(borderColor), Color
					.green(borderColor), Color.blue(borderColor));

			insideColor = Color.argb(77, Color.red(insideColor), Color
					.green(insideColor), Color.blue(insideColor));
		}
		paint.setColor(borderColor);
		paint.setAntiAlias(true);

		this.canvas.drawCircle(cx, cy, radius, paint);
		paint.setColor(insideColor);
		this.canvas.drawCircle(cx, cy, radius - 4, paint);
	}

	/**
	 * Marks in the screen the allowed positions
	 * 
	 * @param playerOne
	 */
	private void markAllowedPositions(int playerOne) {
		if (this.markAllowedPositionIsSelected) {

			// getting the positions to mark
			int[][] allowedPos = this.gameFacade.getAllowedPositionsForPlayer();

			for (int i = 0; i < GameLogic.COLS; i++) {
				for (int j = 0; j < GameLogic.ROWS; j++) {
					// if there is a position of a player to draw
					if (allowedPos[i][j] != GameLogic.EMPTY) {
						// drawing hypothetic positions
						this.drawPosition(i, j, allowedPos[i][j], false);
					}
				}
			}
		}
	}

	/**
	 * Gets the color for the inside of the circle of the given player
	 */
	private int getInsideColorForPlayer(int player) {
		if (player == GameLogic.PLAYER_ONE) {
			return this.playerOneInsideColor;
		} else {
			return this.playerTwoInsideColor;
		}
	}

	/**
	 * Gets the color for the given player
	 */
	private int getColorForPlayer(int player) {
		if (player == GameLogic.PLAYER_ONE) {
			return this.playerOneColor;
		} else {
			return this.playerTwoColor;
		}
	}

	/**
	 * Draws the initial lines that forms the board
	 */
	private void drawBoard() {

		this.drawGrid();
		this.drawPositions();
	}

	/**
	 * Calculates the graphic parameters such as cell width, cell height, etc
	 */
	private void calculateGraphicParameters() {

		View gameBoard = findViewById(R.id.GameBoard01);

		width = gameBoard.getWidth();
		height = gameBoard.getHeight();
		

		// getting the minor (the board is a square)
		if (height > width) {
			height = width;
		} else {
			width = height;
		}
		
		//converting the dimensions in 8 multiple 
		while (width % 8 != 0) {
			width--;
			height --;
		}

		cellWidth = (width - LEFT_MARGIN * 2) / NUMBER_OF_COLUMNS;
		cellHeight = (height - TOP_MARGIN * 2) / NUMBER_OF_ROWS;
	}

	/**
	 * Draws the grid of the board
	 * 
	 * @param paint
	 */
	private void drawGrid() {
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		for (int col = 0; col <= NUMBER_OF_COLUMNS; col++) {
			// vertical lines
			int x = col * cellWidth + LEFT_MARGIN;
			canvas.drawLine(x, TOP_MARGIN, x, height - TOP_MARGIN * 1, paint);
}

		for (int row = 0; row < NUMBER_OF_ROWS + 1; row++) {

			int y = row * cellHeight + TOP_MARGIN;
			// horizontal lines
			canvas.drawLine(LEFT_MARGIN, y, width - (LEFT_MARGIN * 1), y,
							paint);
		}
	}

	/**
	 * Draws all the chips
	 */
	private void drawPositions() {

		int[][] gameMatrix = this.gameFacade.getGameMatrix();

		for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
			for (int row = 0; row < NUMBER_OF_ROWS; row++) {
				if (gameMatrix[col][row] != GameLogic.EMPTY) {
					this.drawPosition(col, row, gameMatrix[col][row], true);
				}
			}
		}

	}

	/**
	 * @param gameFacade
	 *            the gameFacade to set
	 */
	public void setGameFacade(GameFacade gameFacade) {
		this.gameFacade = gameFacade;
	}

	/**
	 * @return the gameFacade
	 */
	public GameFacade getGameLogic() {
		return gameFacade;
	}

}