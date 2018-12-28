package edu.wm.cs.cs301.slidingpuzzle;

import java.util.*;

/**
 * @author roshanmanjaly
 *
 */

public class SimplePuzzleState implements PuzzleState {

	private int[][] board;
	private SimplePuzzleState l_state;
	private Operation l_operation;
	private Random rand = new Random();

	public SimplePuzzleState() {
		setToInitialState(2, 1);
	}

	public SimplePuzzleState(int[][] b, SimplePuzzleState state, Operation op) {
		board = b;
		l_state = state;
		l_operation = op;
	}

	@Override
	public void setToInitialState(int dimension, int numberOfEmptySlots) {
		l_operation = null;
		l_state = null;
		board = new int[dimension][dimension];
		int count = 1;
		for (int x = 0; x < (dimension); x++) {
			for (int y = 0; y < dimension; y++) {
				if (dimension * dimension - (numberOfEmptySlots) >= count) {
					board[x][y] = count;
					count = count + 1;
				}
			}
		}
	}

	@Override
	public int getValue(int row, int column) {
		return board[row][column];
	}

	@Override
	public PuzzleState getParent() {
		return l_state;
	}

	@Override
	public Operation getOperation() {
		return l_operation;
	}

	@Override
	public int getPathLength() {
		if (getParent() != null) {
			return 1 + getParent().getPathLength();
		}
		return 0;
	}

	public int getBoardSize() {
		return board.length;
	}

	@Override
	public PuzzleState move(int row, int column, Operation op) {

		int[][] board_2 = new int[board.length][board.length];
		for (int x = 0; x < board_2.length; x++) {
			for (int y = 0; y < board_2.length; y++) {
				board_2[x][y] = board[x][y];
			}
		}

		int swapRow = row;
		int swapCol = column;
		switch (op) {
		case MOVEDOWN:
			swapRow = swapRow + 1;
			break;
		case MOVEUP:
			swapRow = swapRow - 1;
			break;
		case MOVERIGHT:
			swapCol = swapCol + 1;
			break;
		case MOVELEFT:
			swapCol = swapCol - 1;
			break;
		}
		if (isEmpty(swapRow, swapCol)) {
			board_2[swapRow][swapCol] = board_2[row][column];
			board_2[row][column] = 0;
			return new SimplePuzzleState(board_2, this, op);
		}
		return null;
	}

	@Override
	public PuzzleState drag(int startRow, int startColumn, int endRow, int endColumn) {

		if (startRow == endRow && startColumn == endColumn) {
			return this; // base case; where we are already at the goal
		}
		PuzzleState move;
		if (startColumn < endColumn) {
			move = move(startRow, startColumn, Operation.MOVERIGHT);
			if (move != null) // if it worked
				return move.drag(startRow, startColumn + 1, endRow, endColumn);

		}
		if (startColumn > endColumn) {
			move = move(startRow, startColumn, Operation.MOVELEFT);
			if (move != null)
				return move.drag(startRow, startColumn - 1, endRow, endColumn);
		}
		if (startRow < endRow) {
			move = move(startRow, startColumn, Operation.MOVEDOWN);
			if (move != null)
				return move.drag(startRow + 1, startColumn, endRow, endColumn);
		}
		if (startRow > endRow) {
			move = move(startRow, startColumn, Operation.MOVEUP);
			if (move != null)
				return move.drag(startRow - 1, startColumn, endRow, endColumn);
		}
		return this;
	}

	@Override
	public PuzzleState shuffleBoard(int path_len) {

		if (path_len == 0) {
			return this;
		}
		Operation[] a = { Operation.MOVEDOWN, Operation.MOVERIGHT, Operation.MOVELEFT, Operation.MOVEUP };
		PuzzleState next_move = move(rand.nextInt(board.length), rand.nextInt(board.length), a[rand.nextInt(4)]);
		int nextRow = rand.nextInt(board.length);
		int nextCol = rand.nextInt(board.length);
		Operation next_operation = a[rand.nextInt(4)];
		while (next_move == null || next_operation == opInverse(l_operation)) {// keep trying until we make a possible move that
																		// can't
																		// possibly undo the last one we did
			nextRow = rand.nextInt(board.length);
			nextCol = rand.nextInt(board.length);
			next_operation = a[rand.nextInt(4)];
			next_move = move(nextRow, nextCol, next_operation);
			// System.out.println(next_move);

		}
		return next_move.shuffleBoard(path_len - 1);
	}

	// returns the inverse of the given operation (i.e. MOVEDOWN -> MOVEUP and vice
	// versa).
	public Operation opInverse(Operation o) {
		if (o == null)
			return null;
		switch (o) {
		case MOVEDOWN:
			return Operation.MOVEUP;
		case MOVEUP:
			return Operation.MOVEDOWN;
		case MOVELEFT:
			return Operation.MOVERIGHT;
		case MOVERIGHT:
			return Operation.MOVELEFT;
		default:
			return null;
		}
	}

	@Override
	public boolean isEmpty(int row, int column) {
		if (row < board.length && row >= 0 && column < board[0].length && column >= 0) {
			return board[row][column] == 0;
		}
		return false;
	}

	@Override
	public PuzzleState getStateWithShortestPath() {
		return this; 
	}

	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass())
			return false;
		SimplePuzzleState s = (SimplePuzzleState) obj;
		if (getBoardSize() != s.getBoardSize())
			return false;
		boolean result = true;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board.length; y++) {// b's a square so length and width are the same
				// System.out.println(""+s.getValue(x,y)+", "+this.getValue(x, y));
				result = result && s.getValue(x, y) == this.getValue(x, y);
			}
		}
		return result;
	}

	public int hashCode() {
		return 0;
	}

	public String toString() {
		String result = "";
		for (int[] x : board) {
			result += Arrays.toString(x) + "\n";
		}
		return result;
	}

}
