// SudokuSolver by Will Shelver
package me.will_s.school.sudoku.solver;

import java.util.ArrayList;
import java.util.List;
import me.will_s.school.sudoku.Grid;

public class Solver {
	/** An {@code Object} to acquire a monitor lock on, for use with Java's
	 * threading model to avoid multithreading issues such as memory corruption
	 * due to concurrent modification */
	private final Object lock = new Object();
	
	/** The list of all solutions found by the {@link SolverThread} managed by
	 * this instance */
	private List<Grid> solutions;
	
	/** The {@link SolverThread} managed by this {@link Solver} */
	private final Thread solverThread;
	
	/** An optional Runnable. The run() method is called on this after solving is
	 * complete. Intended implementation is for the caller to provide an
	 * implementation of Runnable (possibly itself) to handle this event, and
	 * call {@link #getResult()} */
	private Runnable callback = null;
	
	/** True if this {@link Solver} has a result ready */
	private boolean solveComplete;
	
	/** Factory method that returns an instance of {@link Solver} specific to
	 * this input grid, that forms the link between the calling code and the
	 * solving thread
	 * 
	 * @param grid
	 *            The {@link Grid} representing the puzzle to be solved
	 * @param callback
	 *            An optional {@link Runnable} that contains code to notify the
	 *            caller that this {@link Solver} has finished running. Set null
	 *            to disable this functionality, in which case the caller must
	 *            call {@link #getResult()} itself.
	 * @return A {@link Solver} that forms an interface to the solver thread */
	public static Solver startSolve(Grid grid, Runnable callback) {
		Solver solver = new Solver(grid, callback);
		return solver;
	}
	
	/** @return True if this solver is currently managing a running
	 *         {@link SolverThread}, false otherwise. */
	public boolean isRunning() {
		return this.solverThread.isAlive();
	}
	
	/** Causes the {@link SolverThread} managed by this instance to terminate
	 * fairly quickly and painlessly. */
	public void abortSolve() {
		if (this.solverThread.isAlive()) {
			this.solverThread.interrupt();
		}
	}
	
	/** Private constructor to allow the static methods to work
	 * 
	 * @param grid
	 *            The puzzle to be solved */
	private Solver(Grid grid, Runnable callback) {
		this.callback = callback;
		solutions = new ArrayList<Grid>();
		solveComplete = false;
		solverThread = new SolverThread(grid, new Callback());
		solverThread.start();
	}
	
	/** Internal method to receive the solution from the thread and store it.
	 * Wakes up any threads waiting on {@link #solutions} to be updated, then
	 * runs the code in {@link #callback}.
	 * 
	 * @param grids
	 *            The set of results */
	private void setResult(List<Grid> grids) {
		synchronized (this.lock) {
			this.solutions = grids;
			this.solveComplete = true;
			this.lock.notifyAll();
		}
		
		// Notify callers via callback
		if (this.callback instanceof Runnable) {
			callback.run();
		}
	}
	
	/** @return True if this solver has a result ready to return via a call to
	 *         {@link #getResult()}, false otherwise */
	public boolean isSolveComplete() {
		return this.solveComplete;
	}
	
	/** @return The list of all possible solutions to the problem (in most cases,
	 *         there will only be one). If the solve is not yet complete, this
	 *         method will block until it is complete, at which point it will
	 *         return normally. */
	public List<Grid> getResult() {
		if (!this.solveComplete) {
			try {
				this.lock.wait();
			} catch (InterruptedException e) {
				// Should never be thrown
			}
		}
		return solutions;
	}
	
	/** A class used by {@link SolverThread} to return the solution it finds */
	class Callback {
		public void notifyResultReady(List<Grid> grids) {
			setResult(grids);
		}
	}
}

/** Thread that runs the DLX algorithm, to prevent excessive load on the main
 * (UI) thread */
class SolverThread extends Thread {
	private final Grid puzzle;
	private final Solver.Callback callback;
	
	public SolverThread(Grid grid, Solver.Callback callback) {
		this.puzzle = grid;
		this.callback = callback;
	}
	
	@Override
	public void run() {
		NodeManager nodeManager = Initialiser.initialise(puzzle);
		if (nodeManager == null) {
			// Initialiser was interrupted
			return;
		}
		List<Grid> result = new DLXImplV2(nodeManager).solve();
		
		nodeManager = null;
		
		this.callback.notifyResultReady(result);
		Runtime.getRuntime().gc(); // Clear up after ourselves
	}
}