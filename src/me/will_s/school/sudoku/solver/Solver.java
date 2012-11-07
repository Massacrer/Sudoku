// SudokuSolver by Will Shelver
package me.will_s.school.sudoku.solver;

import java.util.List;

//TODO: javadoc comments

class Solver {
	private RootNode rootNode;
	private List<List<Integer>>[] grids;
	private Thread solverThread;
	
	public static SolverThread solve(List<List<Integer>> grid) {
		// run solver in new thread, return solver
		SolverThread solverThread = new SolverThread(grid);
		solverThread.start();
		return solverThread;
	}
	
	public Thread getSolverThread() {
		return this.solverThread;
	}
	
	private Solver(List<List<Integer>> grid) {
		grids = null;
		rootNode = Initialiser.initialise(grid);
		return;
	}
	
	// Do not call from main (UI) thread
	private List<List<Integer>>[] solve() {
		List<List<Integer>>[] result = DancingLinks.solve(rootNode);
		return result;
	}
	
	private void setResult(List<List<Integer>>[] grids) {
		synchronized (this.grids) {
			this.grids = grids;
		}
		// In case of thread blocking, wake up other threads (probably UI) after
		// assignment is complete
		this.grids.notifyAll();
	}
	
	public List<List<Integer>>[] getResult() {
		List<List<Integer>>[] grids;
		synchronized (this.grids) {
			grids = this.grids;
		}
		// In case of thread blocking, wake up other threads (probably UI) after
		// assignment is complete
		this.grids.notifyAll();
		return grids;
	}
	
	static class SolverThread extends Thread {
		private List<List<Integer>> grid;
		private Solver solver;
		
		public SolverThread(List<List<Integer>> grid) {
			this.grid = grid;
			this.start();
		}
		
		public Solver getSolver() {
			return this.solver;
		}
		
		@Override
		public void run() {
			solver = new Solver(this.grid);
			solver.solverThread = this;
			// Heavy lifting here
			solver.setResult(solver.solve());
		}
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