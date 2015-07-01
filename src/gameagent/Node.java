package gameagent;

import gameagent.Agent.Players;

public class Node {
	public Players player;
	public double value;
	public int visits, num_children, child_start;
	public Move last_move;
	
	public Node(int visits, double value, Move last_move, Players player) {
		this.visits = visits;
		this.value = value;
		this.last_move = last_move;
		this.player = player;
		num_children = 0;
	}
}