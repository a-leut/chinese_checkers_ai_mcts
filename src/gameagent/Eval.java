package gameagent;

import java.util.HashMap;

public class Eval {
	public static HashMap<Integer, Integer> pl = makeProgressLevels();
	
	// Used for mainly evaluation function, value is how far you are vertically across the board
	private static HashMap<Integer, Integer> makeProgressLevels() {
		HashMap<Integer, Integer> p = new HashMap<Integer, Integer>();
		int[][] t = { { 0 }, { 9, 1 }, { 18, 10, 2 }, { 27, 19, 11, 3 },
				{ 36, 28, 20, 12, 4 }, { 45, 37, 29, 21, 13, 5 },
				{ 54, 46, 38, 30, 22, 14, 6 },
				{ 63, 55, 47, 39, 31, 23, 15, 7 },
				{ 72, 64, 56, 48, 40, 32, 24, 16, 8 },
				{ 73, 65, 57, 49, 41, 33, 25, 17 },
				{ 74, 66, 58, 50, 42, 34, 26 }, { 75, 67, 59, 51, 43, 35 },
				{ 76, 68, 60, 52, 44 }, { 77, 69, 61, 53 }, { 78, 70, 62 },
				{ 79, 71 }, { 80 } };
		for (int i = 0; i < t.length; i++) {
			for (int e : t[i]) {
				p.put(e, i + 1);
			}
		}
		return p;
	}
}
