package me.will_s.school.sudoku.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import me.will_s.school.sudoku.Grid;
import me.will_s.school.sudoku.TestInterface;

/** Class to manage the execution of the Dancing Links algorithm */
public class DLXImplV2 {
	/** The list of complete Sudoku grids that are valid solutions to the given
	 * puzzle. Can be empty, should normally contain only one entry */
	private final List<Grid> solutions;
	/** The {@link RootNode} of the current grid */
	private final RootNode root;
	/** The {@link Stack} of solution parts currently being used to try to build
	 * a solution. If this stack contains an entry for every cell in a puzzle,
	 * then the contents of the stack constitute a solution to the puzzle */
	private final Stack<Short> solutionParts;
	/** Whether the current thread has been interrupted since the last check.
	 * Stored here to allow the interrupt to propogate up the stack of recursive
	 * calls, and return quickly */
	private boolean interrupted;
	/** The current level of recursion, used for debugging */
	private int level;
	/** Currently unused, part of an optimisation on the number of total method
	 * calls made by selecting the next column as the column with the least
	 * nodes in it */
	private int lowestColumnSize;
	
	/** @param nodeManager
	 *            The {@link NodeManager} that contains the linked list
	 *            structure to solve */
	public DLXImplV2(NodeManager nodeManager) {
		this.root = nodeManager.root;
		this.solutions = new ArrayList<Grid>();
		this.solutionParts = nodeManager.existingSolutionParts;
		this.interrupted = false;
		this.level = this.solutionParts.size();
		this.setLowestColumnSize();
	}
	
	/** Solves the linked list structure for all solution grids
	 * 
	 * @return All possible solutions for the input */
	public List<Grid> solve() {
		try {
			if (TestInterface.DEBUG) {
				TestInterface.dbgout("Initial state:");
				TestInterface.printGrid(this.getCurrentGridState());
			}
			this.x();
		} catch (Exception e) {
			return null;
		}
		return this.solutions;
	}
	
	/** The main recursive method implementing Knuth's Algorithm X
	 * 
	 * @throws Exception
	 *             Thrown on any breaking error, currently including the
	 *             covering or uncovering of a non-header node */
	private void x() throws Exception {
		// Iterate over constraints to satisfy
		// for (Node column = this.root.right; column != this.root; column =
		// column.right) {
		
		// Note that we have entered another level of recursion
		this.level++;
		
		// DEBUG
		// warn if level is above 81
		if (this.level > 82) {
			System.out.println("WARNING: Current level: " + this.level);
			this.interrupted = true;
		}
		
		// Test if this produces a full solution - no remaining
		// unsatisfied constraints
		if (root.right == root || this.level == 82) {
			addCurrentAsSolution();
			this.level--;
			return;
		}
		// Select constraint to satisfy
		HeaderNode column = this.getNextColumn();
		
		// DEBUG
		if (TestInterface.DEBUG) {
			System.out.println("Current level: " + this.level);
			System.out.println("Considering column of " + column.toString()
					+ ", size: " + column.size);
		}
		
		// Remove this column from further consideration
		cover(column.head);
		
		// Variable for currently considered column
		Node nodeInColumn;
		
		// Iterate over solution parts that satisfy this constraint
		for (nodeInColumn = column.down; nodeInColumn != column; nodeInColumn = nodeInColumn.down) {
			
			// Select column to use as part of solution
			// TODO: Replace this with a more efficient selector
			// Node nodeInColumn = column.down;
			
			// Check for interrupt
			if (Thread.interrupted()) {
				this.interrupted = true;
			}
			
			// Return on interrupt
			if (this.interrupted) {
				return;
			}
			
			// Add this solution part to the total solution
			this.solutionParts.push(nodeInColumn.solutionPart);
			
			// Mark satisfied all constraints linked by this row
			for (Node nodeInRow = nodeInColumn.right; nodeInRow != nodeInColumn; nodeInRow = nodeInRow.right) {
				cover(nodeInRow.head);
			}
			
			// Recalculate the lowest column size
			this.setLowestColumnSize();
			// TODO: replace with simple flag to indicate presence of an empty
			// column?
			
			// Check if this solution is feasable, skip and return if not
			
			x();
			
			// Uncover all constraints previously covered
			for (Node nodeInRow = nodeInColumn.right; nodeInRow != nodeInColumn; nodeInRow = nodeInRow.right) {
				unCover(nodeInRow.head);
			}
			
			// Remove this solution part from the full solution
			this.solutionParts.pop();
		}
		// }
		
		// Add this column back for consideration
		unCover(nodeInColumn.head);
		
		// Decrease level of recursion
		this.level--;
	}
	
	/** Called when the contents of {@link #solutionParts} form a complete
	 * solution. Adds the this solution to the set of complete solutions to be
	 * returned */
	private void addCurrentAsSolution() {
		Grid grid = this.getCurrentGridState();
		this.solutions.add(grid);
		// Debug
		boolean print = true;
		if (TestInterface.DEBUG && print) {
			System.out.println("Adding grid as solution:");
			System.out.println("Current level: " + this.level);
			TestInterface.printGrid(grid);
		}
	}
	
	/** Covers the given column, removing its {@link HeaderNode} from the
	 * horizontal list of headers, and removing nodes from their vertical lists
	 * in all rows satisfied by this column
	 * 
	 * @param colHead
	 *            The header node of the column to remove
	 * @throws Exception
	 *             Thrown if {@code colHead} is not a {@link HeaderNode} */
	static void cover(Node colHead) throws Exception {
		if (!(colHead instanceof HeaderNode)) {
			throw new Exception("Cannot cover non-header node "
					+ colHead.toString());
		}
		// Remove this node from the header row
		colHead.left.right = colHead.right;
		colHead.right.left = colHead.left;
		// Iterate over solution parts that satisfy this constraint
		for (Node row = colHead.down; row != colHead; row = row.down) {
			// Iterate over nodes in each row
			for (Node node = row.right; node != row; node = node.right) {
				// Remove this node from its vertical list
				node.up.down = node.down;
				node.down.up = node.up;
				node.head.size--;
			}
		}
	}
	
	/** Adds a column back into the list of constraints to be fulfilled, and
	 * re-adds any nodes in rows that were hidden due to a previous covering
	 * operation
	 * 
	 * @param colHead
	 *            The header node of the column to uncover
	 * @throws Exception
	 *             Thrown if colHead is not a header node */
	static void unCover(Node colHead) throws Exception {
		if (!(colHead instanceof HeaderNode)) {
			throw new Exception("Cannot uncover non-header node "
					+ colHead.toString());
		}
		// Add this node into the horizontal list of columns
		colHead.left.right = colHead;
		colHead.right.left = colHead;
		// Iterate in reverse order over rows that satisfy this column
		for (Node row = colHead.up; row != colHead; row = row.up) {
			// Iterate in reverse order over nodes in this row
			for (Node node = row.left; node != row; node = node.left) {
				// Add this node into its vertical list
				node.up.down = node;
				node.down.up = node;
				node.head.size++;
			}
		}
	}
	
	/** Calculates the column of lowest size, for use in the algorithm for
	 * determining if the current state is progressible or not, and sets
	 * {@link #lowestColumnSize} to it. */
	private void setLowestColumnSize() {
		int lowest = root.right.head.size;
		for (Node col = root.right; col != root; col = col.right) {
			if (col.head.size < lowest) {
				lowest = col.head.size;
			}
		}
		this.lowestColumnSize = lowest;
	}
	
	/** @return The next column the algorithm should test */
	// TODO: Replace with a more efficient implementation
	private HeaderNode getNextColumn() {
		return root.right.head;
	}
	
	/** Recreates a Sudoku grid from the current state of the linked list
	 * structure
	 * 
	 * @return The Sudoku grid representation of the current state */
	private Grid getCurrentGridState() {
		Grid grid = new Grid(9);
		SolutionPart part;
		for (Short s : this.solutionParts) {
			part = new SolutionPart(s);
			grid.set(part.getA(), part.getB(), part.getC() + 1);
		}
		return grid;
	}
}