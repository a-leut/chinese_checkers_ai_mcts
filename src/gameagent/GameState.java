package gameagent;

import gameagent.Agent.Players;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameState {
	
	public int[] board;
	private int currentPlayer = 0;
	
	public GameState(GameState aState) {
		this();
		int[] b = new int[81];
		System.arraycopy(aState.getBoard(), 0, b, 0, 81);
		this.board = b;
		this.currentPlayer = aState.currentPlayer;
	}
	
	public GameState() {
		reset();
	}
	
	public GameState(int[] board, int currentPlayer) {
		this.board = board;
		this.currentPlayer = currentPlayer;
	}
	
	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}
	
	public Players getCurrentPlayerEnum() {
		return currentPlayer == 1 ? Players.player1 : Players.player2;
	}

	public int[] getBoard() {
		return board;
	}
	
	// Reset the board to the initial state
	public void reset() {
		board = new int[] { 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0,
				0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0,
				0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 2, 2, 2,
				2 };
		currentPlayer = 1;
	}

	// Gets valid moves for current board and puts them in the moves list.
	Move last, temp;
	static Random rnd = new Random();
	ArrayList<Integer> jump_visited = new ArrayList<Integer>();
	public void getMoves(List<Move> moves) {
		moves.clear();
		jump_visited.clear();
		for (int i = 0; i < 81; ++i) {
			if (board[i] == currentPlayer) {
				jump_visited.clear();
				jump_visited.add(i);
				getMovesJump(moves, jump_visited, i, i);
				getMovesSingleStep(moves, i);
			}
		}
		// Best move first
		if (currentPlayer == 1) {
			Collections.sort(moves, Collections.reverseOrder());
		} else {
			Collections.sort(moves);
		}
		// Random tie breaking (otherwise, for example, we always move left)
		for (int j = 1; j < moves.size(); j++) {
			if (Move.getMoveDistance(moves.get(j)) == Move
					.getMoveDistance(moves.get(j - 1))) {
				if (rnd.nextBoolean()) {
					Collections.swap(moves, j, j - 1);
				}
			}
		}
	}
	
	public void getForwardMoves(List<Move> moves) {
		moves.clear();
		jump_visited.clear();
		for (int i = 0; i < 81; ++i) {
			if (board[i] == currentPlayer) {
				jump_visited.clear();
				jump_visited.add(i);
				getMovesJumpForward(moves, jump_visited, i, i);
				getMovesSingleStepForward(moves, i);
			}
		}
		// best move first
		if (currentPlayer == 1) {
			Collections.sort(moves, Collections.reverseOrder());
		} else {
			Collections.sort(moves);
		}
		
		// random tie break
		for (int j = 1; j < moves.size(); j++) {
			if(Move.getMoveDistance(moves.get(j)) == 
			   Move.getMoveDistance(moves.get(j-1))) {
				if (rnd.nextBoolean()) {
					Collections.swap(moves, j, j-1);
				}
			}
		}
	}

	private void getMovesJump(List<Move> moves, List<Integer> visted,
			int from, int orig_from) {
		int row = from / 9;
		int col = from % 9;

		// Up Left
		if (col > 1 && board[from - 1] != 0 && board[from - 2] == 0
				&& !visted.contains(from - 2)) {
			moves.add(new Move(orig_from, from - 2));
			visted.add(from - 2);
			getMovesJump(moves, visted, from - 2, orig_from);
		}

		// Up Right
		if (row > 1 && board[from - 9] != 0 && board[from - 18] == 0
				&& !visted.contains(from - 18)) {
			moves.add(new Move(orig_from, from - 18));
			visted.add(from - 18);
			getMovesJump(moves, visted, from - 18, orig_from);
		}

		// Left
		if (col > 1 && row < 7 && board[from + 8] != 0 && board[from + 16] == 0
				&& !visted.contains(from + 16)) {
			moves.add(new Move(orig_from, from + 16));
			visted.add(from + 16);
			getMovesJump(moves, visted, from + 16, orig_from);
		}

		// Right
		if (col < 7 && row > 1 && board[from - 8] != 0 && board[from - 16] == 0
				&& !visted.contains(from - 16)) {
			moves.add(new Move(orig_from, from - 16));
			visted.add(from - 16);
			getMovesJump(moves, visted, from - 16, orig_from);
		}

		// Down Left
		if (row < 7 && board[from + 9] != 0 && board[from + 18] == 0
				&& !visted.contains(from + 18)) {
			moves.add(new Move(orig_from, from + 18));
			visted.add(from + 18);
			getMovesJump(moves, visted, from + 18, orig_from);
		}

		// Down Right
		if (col < 7 && board[from + 1] != 0 && board[from + 2] == 0
				&& !visted.contains(from + 2)) {
			moves.add(new Move(orig_from, from + 2));
			visted.add(from + 2);
			getMovesJump(moves, visted, from + 2, orig_from);
		}
	}
	
	private void getMovesSingleStep(List<Move> moves, int from) {
		int row = from / 9;
		int col = from % 9;
		
		// Left
		if (col > 0 && row < 8 && board[from + 8] == 0)
			moves.add(new Move(from, from + 8));
		
		// Right
		if (col < 8 && row > 0 && board[from - 8] == 0)
			moves.add(new Move(from, from - 8));
		
		// Down Left
		if (row < 8 && board[from + 9] == 0)
			moves.add(new Move(from, from + 9));
		
		// Down Right
		if (col < 8 && board[from + 1] == 0)
			moves.add(new Move(from, from + 1));
		
		// Up Left
		if (col > 0 && board[from - 1] == 0)
			moves.add(new Move(from, from - 1));
		
		// Up Right
		if (row > 0 && board[from - 9] == 0)
			moves.add(new Move(from, from - 9));
	}
	
	private void getMovesJumpForward(List<Move> moves, List<Integer> visted, int from, int orig_from) {
		int row = from / 9;
		int col = from % 9;
		
		if (currentPlayer == 1) {
			// Down Left
			if (row < 7 && board[from + 9] != 0 && board[from + 18] == 0
					&& !visted.contains(from + 18)) {
				moves.add(new Move(orig_from, from + 18));
				visted.add(from + 18);
				getMovesJump(moves, visted, from + 18, orig_from);
			}

			// Down Right
			if (col < 7 && board[from + 1] != 0 && board[from + 2] == 0
					&& !visted.contains(from + 2)) {
				moves.add(new Move(orig_from, from + 2));
				visted.add(from + 2);
				getMovesJump(moves, visted, from + 2, orig_from);
			}
		} else {
			// Up Left
			if (col > 1 && board[from - 1] != 0 && board[from - 2] == 0
					&& !visted.contains(from - 2)) {
				moves.add(new Move(orig_from, from - 2));
				visted.add(from - 2);
				getMovesJump(moves, visted, from - 2, orig_from);
			}

			// Up Right
			if (row > 1 && board[from - 9] != 0 && board[from - 18] == 0
					&& !visted.contains(from - 18)) {
				moves.add(new Move(orig_from, from - 18));
				visted.add(from - 18);
				getMovesJump(moves, visted, from - 18, orig_from);
			}
		}
	}
	
	private void getMovesSingleStepForward(List<Move> moves, int from) {
		int row = from / 9;
		int col = from % 9;

		if (currentPlayer == 1) {
			// Down Left
			if (row < 8 && board[from + 9] == 0)
				moves.add(new Move(from, from + 9));
			
			// Down Right
			if (col < 8 && board[from + 1] == 0)
				moves.add(new Move(from, from + 1));
		} else {
			// Up Left
			if (col > 0 && board[from - 1] == 0)
				moves.add(new Move(from, from - 1));
			
			// Up Right
			if (row > 0 && board[from - 9] == 0)
				moves.add(new Move(from, from - 9));
		}
	}



	// Apply the move m, returning true if m is a valid move, false if not
	public void applyMove(Move m) {	
		int temp = board[m.from];
		board[m.from] = board[m.to];
		board[m.to] = temp;
		swapTurn();
	}

	// Undo the move m, returning true if m is a move that can be undone, false
	// if not
	public void undoMove(Move m) {	
		int temp = board[m.from];
		board[m.from] = board[m.to];
		board[m.to] = temp;
		swapTurn();
	}

	// Returns true iff the game is over
	public boolean gameOver() {
		return player1Wins() || player2Wins();
	}

	// Return the player who won, assuming the game is over
	public int winner() {
		if (player1Wins())
			return 1;
		if (player2Wins())
			return 2;
		return -1; // No one has won
	}

	public void set_board(int[] new_board) {
		if (new_board.length != 81)
			return;
		board = new_board;
	}

	// Loads the state stored in the string, returning true if it is a valid
	// state, false if not
	public boolean loadState(String newState) {
		// Tokenize newState using whitespace as delimiter
		String[] tokenized = newState.split(" ");

		// Ensure the length
		if (tokenized.length != 82)
			return false;

		// Validate first item, whose turn it is
		if (!tokenized[0].equals("1") && !tokenized[0].equals("2"))
			return false;

		try {
			currentPlayer = Integer.parseInt(tokenized[0]);
		} catch (NumberFormatException e) {
			return false;
		}

		// Ensure rest of tokens are valid
		for (int i = 1, e = tokenized.length; i != e; ++i) {
			try {
				int val = Integer.parseInt(tokenized[i]);
				if (0 <= val && val <= 2)
					board[i - 1] = val;
				else
					return false;
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return true;
	}

	// Dump out the current state, usable with loadState
	public String dumpState() {
		StringBuilder out = new StringBuilder();
		out.append(currentPlayer);
		for (int i = 0; i < board.length; ++i)
			out.append(" " + board[i]);
		return out.toString();
	}

	// Translates a sequence of tokens from the move format used to the local
	// move type
	public Move translateToLocal(String[] tokens) {
		// The numbers in the MOVE command sent by the moderator is already in
		// the
		// format we need
		try {
			Move m = new Move(0, 0);
			m.from = Integer.parseInt(tokens[2]);
			m.to = Integer.parseInt(tokens[4]);
			return m;
		} catch (NumberFormatException e) {
			return new Move(0, 0);
		}
	}

	public void swapTurn() {
		// irrelevant premature optimization ftw
		currentPlayer = 3 - currentPlayer;
	}

	public boolean player1Wins() {
		// Wins by having all the bottom triangle filled and at least one is
		// from the
		// first player

		boolean p1inTriangle = false;
		int target[] = new int[] { 53, 61, 62, 69, 70, 71, 77, 78, 79, 80 };
		for (int i : target) {
			if (board[i] == 0)
				return false;
			if (board[i] == 1)
				p1inTriangle = true;
		}

		return p1inTriangle;
	}

	public boolean player2Wins() {
		// Wins by having all of top triangle filled and at least one is from
		// the
		// second player

		boolean p2inTriangle = false;
		int target[] = new int[] { 0, 1, 2, 3, 9, 10, 11, 18, 19, 27 };
		for (int i : target) {
			if (board[i] == 0)
				return false;
			if (board[i] == 2)
				p2inTriangle = true;
		}

		return p2inTriangle;
	}
}