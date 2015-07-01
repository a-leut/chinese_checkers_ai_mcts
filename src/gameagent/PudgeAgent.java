package gameagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PudgeAgent extends Agent {
	private double EPSILON = 0.25;
	private double EXPLORE_FACTOR = 17;
	private int DELAY = 9500;
	private int DEPTH = 13;
	private int EXPAND_THRESHOLD = 32;
	private int move_c;
	private double W = 0.1;
	boolean DEBUG = true;
	Random rnd = new Random();

	List<Move> possible_moves = new ArrayList<Move>();
	List<Node> game_tree = new ArrayList<Node>();
	Map<Move, Double> score_history = new HashMap<Move, Double>();
	Map<Move, Integer> play_history = new HashMap<Move, Integer>();

	public PudgeAgent() {
		super("Pudge_the_Butcher");
		move_c = 1;
	}
	
	// High level function for computing the best move
	protected Move nextMove() {
		game_tree.clear();
		game_tree.add(new Node(0, 0, null, current_player));
		expand(0, state);
		Move m = null;
		boolean over = false;
		System.err.println("Finding Move " + move_c + "...");
		score_history.clear();
		play_history.clear();
		MoveTimer t = new MoveTimer(DELAY);
		t.start();
		while (!t.isDone() && !over) {
			MoveReturn mr = getBestMove();
			m = mr.m;
			over = mr.gg;
		}
		if (DEBUG) {
			System.err.println("Total samples " + game_tree.get(0).visits);
			System.err.println("Best move: " + m);
			int start = game_tree.get(0).child_start;
			int end = start + game_tree.get(0).num_children;
			for (int j = start; j < end; j++) {
				Node n = game_tree.get(j);
				System.err.println("     " + "(" + n.last_move.from + ", "
						+ n.last_move.to + ") " + n.visits
						+ " samples; value: " + getUCBProgressiveVal(0, j));
			}
		}
		move_c++;
		return m;
	}
	
	// Adds 1 to move's win history, creates key if it doesn't exist
	private void addScore(Move m, double score) {
		if (score_history.containsKey(m)) {
			score_history.put(m, score_history.get(m) + score);
		} else {
			score_history.put(m, score);
		}
	}
	
	// Adds 1 to move's play history, creates key if it doesn't exist
	private void addPlay(Move m) {
		if (play_history.containsKey(m)) {
			play_history.put(m, play_history.get(m) + 1);
		} else {
			play_history.put(m, 1);
		}
	}
	
	// Gets move's win value, returns 0 if no value found
	private double getScore(Move m) {
		Double j = score_history.get(m);
		if (j == null) {
			return 0.0;
		} else {
			return j;
		}
	}
	
	// Gets number of times a move was played, returns 1 if no value found
	private int getPlay(Move m) {
		Integer j = play_history.get(m);
		if (j == null) {
			return 1;
		} else {
			return j;
		}
	}
	
	List<Integer> visited = new ArrayList<Integer>();
	List<Move> child_moves = new ArrayList<Move>();
	
	private class MoveReturn {
		public boolean gg;
		public Move m;
		
		public MoveReturn(Move m, boolean gg) {
			this.gg = gg;
			this.m = m;
		}
	}
	
	// High level function for computing the best move
	private MoveReturn getBestMove() {
		GameState t_state = new GameState(state);
		// Make sure we always return game winning move
		possible_moves.clear();
		t_state.getForwardMoves(possible_moves);
		for (Move m : possible_moves) {
			t_state.applyMove(m);
			if (t_state.winner() == state.getCurrentPlayer()) {
				return new MoveReturn(m, true);
			}
			t_state.undoMove(m);
		}
		// Selection
		visited.clear();
		int i = 0;
		visited.add(i);
		while (!isLeaf(i)) {
			i = selectBestChild(i);
			visited.add(i);
			t_state.applyMove(game_tree.get(i).last_move);
		}
		// Expansion
		possible_moves.clear();
		t_state.getMoves(possible_moves);
		
		double value = Double.NaN;

		if (!possible_moves.isEmpty()) {
			// get list of moves we've already expanded
			child_moves.clear();
			for (int j = game_tree.get(i).child_start; j < game_tree.get(i).num_children; j++) {
				child_moves.add(game_tree.get(j).last_move);
			}
			// if we have unseen moves
			if (possible_moves.size() > child_moves.size()) {
				possible_moves.removeAll(child_moves);
				addPlay(possible_moves.get(0));
				t_state.applyMove(possible_moves.get(0));
				value = doPlayout(t_state);
				// expand if we've seen this move enough
				if (getPlay(possible_moves.get(0)) > EXPAND_THRESHOLD) {
					singleExpand(i, possible_moves.get(0), t_state, value);
				}
			} else {
				int best = selectBestChild(i);
				t_state.applyMove(game_tree.get(best).last_move);
				visited.add(best);
				value = doPlayout(t_state);
			}
		} else {
			value = doPlayout(t_state);
		}
		// Back propagation
		for (int j : visited) {
			updateValue(j, value);
		}
		return new MoveReturn(game_tree.get(selectBestChild(0)).last_move, false);
	}
	
	// Expand the designated node with a node for m
	private void singleExpand(int node, Move m, GameState s, double value) {
		game_tree.add(new Node(1, value, m, s.getCurrentPlayerEnum()));
	}
	
	// Expand the designated node and add its children to the tree
	private void expand(int node, GameState s) {
		if (!isLeaf(node)) {
			System.out.println("PROBLEM: INTERNAL NODE EXPANDED");
		}
		possible_moves.clear();
		s.getMoves(possible_moves);
		n = game_tree.get(node);
		n.child_start = game_tree.size();
		n.num_children = possible_moves.size();
		for (Move m : possible_moves) {
			game_tree.add(new Node(0, 0, m, s.getCurrentPlayerEnum()));
		}
	}
	

	// Update the value of a node with the score, negative for the other 
	private void updateValue(int node, double score) {
		Node n = game_tree.get(node);
		if (my_player == n.player) {
			n.value += score;
		} else {
			n.value -= score;
		}
		n.visits++;
	}
	
	// Use the our modified UCB rule to find the best child
	private int selectBestChild(int node) {
		int start = game_tree.get(node).child_start;
		int end = start + game_tree.get(node).num_children;
		int selected = -1;
		double ucb_value;
		double best_value = -Double.MAX_VALUE;
		for (int i = start; i < end; i++) {
			ucb_value = getUCBProgressiveVal(i, node);
			if (ucb_value > best_value) {
				best_value = ucb_value;
				selected = i;
			}
		}
		return selected;
	}
	
	Node n, p;
	// Get the modified UCB value of a given node
	double sa;
	private double getUCBProgressiveVal(int node, int parent) {
		n = game_tree.get(node);
		p = game_tree.get(parent);
		
		double v = (n.value / n.visits)
				+ (EXPLORE_FACTOR * (Math.sqrt(Math.log(p.visits) / n.visits)))
				+ ((getScore(n.last_move) / getPlay(n.last_move))
					* (W / (n.visits * 109 - n.value + 1)));

		if (Double.isNaN(v)) {
			// Unvisited nodes come first
			return Double.MAX_VALUE;
		} else {
			//System.err.println(v);
			return v;
		}
	}
	
	// Is the designated node a leaf
	private boolean isLeaf(int node) {
		return game_tree.get(node).num_children == 0;
	}

	// play out the game, returning the 1 for win 0 for loss
	private double doPlayout(GameState s) {
		Move m = null;
		int d = 0;
		GameState r = new GameState(s);
		while(d < DEPTH && !r.gameOver()) {
			d++;
			m = nextEpsilonGreedyMove(r, EPSILON, false);
			if (m != null) {
				r.applyMove(m);
			} else {
				m = nextEpsilonGreedyMove(r, EPSILON, true);
			}
		}
		// get score for side we're on
		double multi = r.getCurrentPlayer() == getPlayer() ? -1.0 : 1.0;
		double score = distanceEvaluate(r);
		// update heuristic knowledge
		if (score > 0) {
			addScore(m, score);
		}
		addPlay(m);
		return score * multi;
	}
	
	private Move nextEpsilonGreedyMove(GameState s, double epsilon, boolean all_moves) {
		possible_moves.clear();
		if (all_moves) {
			s.getMoves(possible_moves);
		} else {
			s.getForwardMoves(possible_moves);
		}
		if (possible_moves.isEmpty()) {
			return null;
		}
		double e = rnd.nextDouble();
		if (e < epsilon) {
			return possible_moves.get(rnd.nextInt(possible_moves.size()));			
		} else {
			return possible_moves.get(0);
		}
	}

	public double distanceEvaluate(GameState s) {
		int score = 0;
		for (int i = 0; i < s.board.length; i++) {
			if (s.board[i] == 1) {
				score += Eval.pl.get(i);
			} else if (s.board[i] == 2) {
				score = score - (17 - Eval.pl.get(i));
			}
		}
		return Players.player1 == my_player ?  score : score * -1.0;
	}

}
