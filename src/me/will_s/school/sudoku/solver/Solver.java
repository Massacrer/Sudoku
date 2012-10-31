// SudokuSolver by Will Shelver
package me.will_s.school.sudoku.solver;

import java.util.List;

//TODO: javadoc comments

public class Solver {
	// List of nodes, used as strong reference to nodes to prevent garbage
	// collection
	// Note: nodes[0] is root, nodes[1] to [729] will contain the header nodes
	// Removed - refer to Node, HeaderNode & RootNode for strong ref lists
	
	// Public-facing method. Note: argument subject to change in type, passed in
	// by UI-level code.
	// Settling on List<List<Integer>> for now.
	public static void solve(List<List<Integer>> grid) {
		Initialiser.initialise(grid);
		// DLX goes here
	}
}

// Take note: this class will need reworking slightly if this code is being
// reused to solve grids larger that 9x9
class SolutionPart {
	// Using short to save space, using bit shifting to store 3 values (row,
	// column, value, each in range 1 <= x <= 9)
	// Assigning 4 bytes to each int, so max unsigned value of each variable =
	// 2^4 = 16
	// Order of storage (msb -> lsb) is row, column, value
	// So data bytes in short looks like 0000rrrrccccvvvv
	private short info = 0;
	
	public SolutionPart(int r, int c, int v) {
		// Make sure all 3 values are sane for the grid size, and can be stored
		// in 4 bytes.
		if ((r < 1) || (r > 9) || (c < 1) || (c > 9) || (v < 1) || (v > 9)) {
			throw new IllegalArgumentException("Argument out of bounds: r" + r
					+ ",c" + c + ",v" + v);
		}
		
		// TODO: probably not necessary, implement check and confirm at some
		// point
		r = r & 0x0000000f;
		c = c & 0x0000000f;
		v = v & 0x0000000f;
		
		short i = (short) ((r << 8) | (c << 4) | (v));
		info = i;
	}
	
	public int getRow() {
		int i = info;
		i = i & 0x00000f00;
		return i >> 8;
	}
	
	public int getColumn() {
		int i = info;
		i = i & 0x000000f0;
		return i >> 4;
	}
	
	public int getValue() {
		int i = info;
		i = i & 0x0000000f;
		return i;
	}
}