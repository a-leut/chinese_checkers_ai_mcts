package gameagent;

import java.util.ArrayList;
import java.util.Scanner;

// Base class for game agent (reused over multiple Chinese checkers assignments)
public abstract class Agent {
	protected Move opp_last_move = null;
	protected Players current_player;
	protected Players my_player;
	protected String name;
	protected String opp_name;
	protected Scanner stdin;
	protected GameState state = new GameState();
	
	public int getPlayer() {
		return my_player == Players.player1 ? 1 : 2;
	}
	
	protected static enum Players {
		player1, player2
	}

	public Agent(String p_name) {
		name = p_name;
		stdin = new Scanner(System.in);
	}
	
	protected abstract Move nextMove();

	public void playGame() {
		// Identify myself
		System.out.println("#name " + name);
		System.out.flush();

		// Wait for start of game
		waitForStart();

		// Main game loop
		for (;;) {

			if (current_player == my_player) {
				// My turn

				// Check if game is over
				if (state.gameOver()) {
					System.err.println("I, " + name + ", have lost");
					System.err.flush();
					switchCurrentPlayer();
					continue;
				}

				// Determine next move
				Move m = nextMove();

				// Apply it locally
				state.applyMove(m);

				// Tell the world
				printAndRecvEcho(m.toString());

				// It is the opponents turn
				switchCurrentPlayer();
			} else {
				// Wait for move from other player
				// Get server's next instruction
				String server_msg = readMessage();
				String[] tokens = server_msg.split(" ");

				// if (tokens.length == 5 && tokens[0] == "MOVE") {
				if (isValidMoveMessage(tokens)) {
					// Translate to local coordinates and update our local state
					Move m = state.translateToLocal(tokens);
					opp_last_move = m;
					state.applyMove(m);
					switchCurrentPlayer();
				} else if (tokens.length == 4 && tokens[0].equals("FINAL")
						&& tokens[2].equals("BEATS")) {
					// Game over
					if (tokens[1].equals(name) && tokens[3].equals(opp_name)) {
						System.err.println("I, " + name + ", have won!");
						System.err.flush();
					} else if (tokens[3].equals(name)
							&& tokens[1].equals(opp_name)) {
						System.err.println("I, " + name + ", have lost.");
						System.err.flush();
					} else {
						System.err
								.println("Did not find expected players in FINAL command.\n"
										+ "Found '"
										+ tokens[1]
										+ "' and '"
										+ tokens[3]
										+ "'. "
										+ "Expected '"
										+ name
										+ "' and '"
										+ opp_name
										+ "'.\n"
										+ "Received message '"
										+ server_msg
										+ "'");
						System.err.flush();
					}
					break;
				} else {
					// Unknown command
					System.err.println("Unknown command of '" + server_msg
							+ "' from the server");
					System.err.print("Tokens: (" + tokens.length + ")");
					for (String s : tokens)
						System.err.print("'" + s + "' ");
					System.err.print("\n");
					System.err.flush();
				}
			}
		}
	}

	// Sends a msg to stdout and verifies that the next message to come in is it
	// echoed back. This is how the server validates moves
	private void printAndRecvEcho(String msg) {
		System.out.println(msg);
		System.out.flush();

		String echo_recv = readMessage();
		if (!msg.equals(echo_recv)) {
			System.err.println("Expected echo of '" + msg + "'. Received '"
					+ echo_recv + "'");
			System.err.flush();
		}
	}

	// Reads a line, up to a newline from the server
	private String readMessage() {
		return stdin.nextLine().trim();
	}

	private String[] tokenize(String s) {
		return s.split(" ");
	}

	private void waitForStart() {
		for (;;) {
			String response = readMessage();
			String[] tokens = tokenize(response);

			if (tokens.length == 4 && tokens[0].equals("BEGIN")
					&& tokens[1].equals("CHINESECHECKERS")) {
				// Found BEGIN GAME message, determine if we play first
				if (tokens[2].equals(name)) {
					// We go first!
					opp_name = tokens[3];
					my_player = Players.player1;
					break;
				} else if (tokens[3].equals(name)) {
					// They go first
					opp_name = tokens[2];
					my_player = Players.player2;
					break;
				} else {
					System.err.println("Did not find '" + name
							+ "', my name, in the BEGIN command.\n"
							+ "# Found '" + tokens[2] + "' and '" + tokens[3]
							+ "'" + " as player names. Received message '"
							+ response + "'");
					System.err.flush();
					System.out.println("#quit");
					System.out.flush();
				}
			} else if (response.equals("DUMPSTATE")) {
				System.out.println(state.dumpState());
				System.out.flush();
			} else if (tokens[0].equals("LOADSTATE")) {
				String newState = response.substring(10);
				if (!state.loadState(newState)) {
					System.err.println("Unable to load '" + newState + "'");
					System.err.flush();
				}
			} else if (response.equals("LISTMOVES")) {
				ArrayList<Move> moves = new ArrayList<>();
				state.getMoves(moves);
				for (Move m : moves) {
					System.out.print(m.from + ", " + m.to + "; ");
				}
				System.out.print("\n");
				System.out.flush();
			} else if (tokens[0].equals("MOVE")) {
				Move m = state.translateToLocal(tokens);
				state.applyMove(m);

			} else {
				System.err.println("Unexpected message '" + response + "'");
				System.err.flush();
			}
		}

		// Player 1 goes first
		current_player = Players.player1;
	}

	private void switchCurrentPlayer() {
		current_player = (current_player == Players.player1) ? Players.player2
				: Players.player1;
	}

	private boolean isValidMoveMessage(String[] tokens) {
		return tokens.length == 5 && tokens[0].equals("MOVE")
				&& tokens[1].equals("FROM") && tokens[3].equals("TO");
	}
}
