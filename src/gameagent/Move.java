package gameagent;

import java.util.Random;

public class Move implements Comparable<Move>{

	public int from;
	public int to;
	static Random rnd = new Random();

	public Move(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public static int getMoveDistance(Move m) {
		return Eval.pl.get(m.from) - Eval.pl.get(m.to);
	}
	
	public static int getMoveDistance(int from, int to) {
		return Eval.pl.get(from) - Eval.pl.get(to);
	}
	
	@Override
	public String toString() {
		return "MOVE FROM " + from + " TO " + to;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Move))
			return false;
		Move m = (Move) other;
		return from == m.from && to == m.to;
	}

	@Override	
	public int compareTo(Move other) {
		return Move.getMoveDistance(other) - Move.getMoveDistance(this);
	}
}
