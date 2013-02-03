// SudokuSolver by Will Shelver
package me.will_s.school.sudoku.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import me.will_s.school.sudoku.Grid;
import me.will_s.school.sudoku.TestInterface;

public class DLXImpl {
	private final Stack<Node> solutionParts;
	private final List<Grid> solutions;
	private boolean interrupted;
	private int level;
	private List<Node> emptyColumns;
	
	private final RootNode root;
	
	public DLXImpl(NodeManager nodeManager) {
		this.solutionParts = nodeManager.preExistingSolutionParts;
		this.solutions = new ArrayList<Grid>(1);
		this.interrupted = false;
		this.root = nodeManager.root;
		this.level = this.solutionParts.size();// + 1;
	}
	
	public List<Grid> solve() {
		// Recursion!
		this.algorithmX();
		// return array of all possible solutions (should only be 1 entry)
		return this.solutions;
	}
	
	private void algorithmX() {
		
		// Check for interrupts, indicating we should abort
		if (Thread.interrupted()) {
			this.interrupted = true;
			if (Solver.DEBUG) {
				TestInterface.dbgout("Thread interrupt received");
				this.writeStack();
			}
		}
		
		// If all constraints have been satisfied, the contents of solutionParts
		// constitute a full solution
		if (root.right == root) {
			addCurrentAsSolution();
			return;
		}
		
		//
		if (Solver.DEBUG) {
			TestInterface.divide();
			// Must not try to get more than 81 solution parts
			if (this.level >= 81) {
				TestInterface.dbgout("Level 81 reached, backtracking");
				return;
			} else {
				TestInterface.dbgout("Entering level " + this.level);
			}
		}
		
		// Make sure this state can be used to proceed
		this.emptyColumns = this.getEmptyColumns();
		if (emptyColumns != null) {
			if (Solver.DEBUG) {
				TestInterface.dbgout("Level " + this.level
						+ " is not progressable, backtracking");
				String out = new String("Empty columns ("
						+ this.emptyColumns.size() + "): ");
				for (Node node : this.emptyColumns) {
					out += node.getNumberString() + ", ";
				}
				TestInterface.dbgout(out.substring(0, out.length() - 2));
				// this.writeStack();
				TestInterface.divide();
			}
			return;
		}
		
		// debug
		if (this.level == 1) {
			HeaderNode node = getNextColumn(root);
			do {
				TestInterface.dbgout(node.getNumberString());
				node = getNextColumn(node);
			} while (node != root);
			TestInterface.divide();
			for (node = getNextColumn(root); node != root; node = getNextColumn(node)) {
				TestInterface.dbgout(node.getNumberString());
			}
		}
		
		HeaderNode column = getNextColumn(root);
		// Iterate over unsatisfied constraints (columns)
		do {
			if (Solver.DEBUG) {
				TestInterface.dbgout("Trying column "
						+ column.getNumberString());
			}
			
			cover(column);
			
			// debug_xOut(column);
			
			Node nodeInColumn = column.down;
			// Iterate over every solution part (row) that satisfies this
			// constraint
			do {
				// Return if interrupted. This propogates up the stack, as
				// this.interrupted is never set false again
				if (this.interrupted) {
					if (Solver.DEBUG) {
						TestInterface.dbgout("Aborting now due to interrupt (level "
								+ this.level + ")");
					}
					return;
				}
				
				// Debug - print constraint under consideration
				if (Solver.DEBUG) {
					TestInterface.dbgout("Trying solution part "
							+ nodeInColumn.getNumberString());
					/*
					 * String out = new String(); ("(currently satisfies " );
					 * 
					 * Node n = colNode; do { out += n.head.getNumberString () +
					 * ", "; n = n.right; } while (n != colNode);
					 * 
					 * TestInterface.dbgout(out .substring (0, out.length() - 2)
					 * + ")");
					 */
					writeStack();
				}
				
				// Add the solution part to the complete solution
				solutionParts.push(nodeInColumn);
				
				// debug
				
				// TestInterface.printGrid(getCurrentGridState());
				
				Node rowNode;
				
				// Cover every other column this row satisfies
				rowNode = nodeInColumn.right;
				do {
					cover(rowNode.head);
					rowNode = rowNode.right;
				} while (rowNode != nodeInColumn);
				
				this.level++;
				
				// oh boy, here we go
				// Recurse with new state, to satisfy next constraint
				algorithmX();
				
				this.level--;
				
				// Remove last added solution part
				solutionParts.pop();
				
				// Uncover other columns satisfied by this row (previously
				// covered)
				do {
					uncover(rowNode.head);
					rowNode = rowNode.left;
				} while (rowNode != nodeInColumn);
				
				// Select next row to try, loop round
				nodeInColumn = nodeInColumn.down;
			} while (nodeInColumn != column);
			
			uncover(column);
			
			// Select next column to try, loop round
			column = getNextColumn(column);
		} while (column != root);
	}
	
	@SuppressWarnings("unused")
	private void debug_xOut(HeaderNode head) {
		TestInterface.dbgout("Level " + this.level + ", " + " column: "
				+ head.toString() + ", " + this.solutions.size() + " solutions");
		this.writeStack();
		TestInterface.divide();
	}
	
	/**
	 * Writes the current contents of the solution part stack to the debug log
	 */
	private void writeStack() {
		TestInterface.dbgout("Current solution parts ("
				+ this.solutionParts.size() + "): ");
		for (Node n : this.solutionParts) {
			TestInterface.dbgout("\t" + n.toString());
		}
	}
	
	/**
	 * Returns a node to try next in the list, based on the current selected
	 * node. Must return root after all valid headers have been returned
	 * 
	 * @param current
	 *            The current header being satisfied
	 * @return The next header to try to satisfy, based on any type of
	 *         efficiency algorithm. Currently only returns the node to the
	 *         right of the current node, for simplicity. A more efficient
	 *         algorithm may choose based on the header's size field.
	 */
	private HeaderNode getNextColumn(HeaderNode current) {
		// Replace this with a more efficient implementation if / when able.
		// This will do for now.
		return (HeaderNode) current.right;
	}
	
	/**
	 * @return The column nodes that are empty, if any. Null otherwise.
	 */
	// Fairly inefficient, fix on implementation of columnManager
	private List<Node> getEmptyColumns() {
		List<Node> nodes = new ArrayList<Node>();
		
		Node node = root.right;
		do {
			if (node.down == node) {
				nodes.add(node);
			}
			node = node.right;
		} while (node != root);
		
		return nodes.size() == 0 ? null : nodes;
	}
	
	private Grid getCurrentGridState() {
		Grid grid = new Grid(9);
		for (Node n : this.solutionParts) {
			SolutionPart sp = n.getSolutionPart();
			grid.set(sp.getA(), sp.getB(), sp.getC());
		}
		return grid;
	}
	
	private void addCurrentAsSolution() {
		this.solutions.add(getCurrentGridState());
		if (Solver.DEBUG) {
			TestInterface.dbgout("Adding current stack as complete solution:");
			this.writeStack();
		}
	}
	
	/**
	 * Covers a given column. This removes it from the list of columns, and also
	 * removes any rows that satisfy this column.<br />
	 * <br />
	 * In terms of the sudoku structure, it marks this constraint as satisfied,
	 * and eliminates any other solution parts from consideration that could
	 * also satisfy it, since constraints can only be satisfied once
	 * 
	 * @param head
	 *            The {@link HeaderNode header node} of the column to be covered
	 */
	static void cover(HeaderNode head) {
		// Cover header node
		head.right.left = head.left;
		head.left.right = head.right;
		// Select each row that satisfies this column
		for (Node node = head.down; node != head; node = node.down) {
			// Select each node in the row EXCEPT the one in the covered column.
			// These can be left as the column itself is removed, and must be
			// left to allow uncover() to find these rows again
			for (Node rowNode = node.right; rowNode != node; rowNode = rowNode.right) {
				// Remove this node from its column
				rowNode.up.down = rowNode.down;
				rowNode.down.up = rowNode.up;
				rowNode.head.size--;
			}
		}
	}
	
	/**
	 * Uncovers a previously covered column
	 * 
	 * @param head
	 *            The header node of the column to uncover
	 */
	static void uncover(HeaderNode head) {
		// Loop through column to uncover in reverse order
		for (Node node = head.up; node != head; node = node.up) {
			// Select each (currently vertically isolated) node in the row
			for (Node rowNode = node.left; rowNode != node; rowNode = rowNode.left) {
				rowNode.up.down = rowNode;
				rowNode.down.up = rowNode;
				rowNode.head.size++;
			}
		}
		head.left.right = head;
		head.right.left = head;
	}
}

/**
 * Class to manage the cache of column sizes, and allow the selection of a
 * column that will allow the algorithm to proceed efficiently, that is, a
 * column with a low number of nodes in it
 */
class ColumnManager {
	private final NodeManager nodeManager;
	private final List<HeaderNode> columnSizes = new ArrayList<HeaderNode>();
	private HeaderNode optimalNode;
	
	public ColumnManager(NodeManager nodeManager) {
		this.nodeManager = nodeManager;
		RootNode root = nodeManager.root;
		short i = 0;
		for (HeaderNode head = (HeaderNode) root.right; head != root; head = (HeaderNode) head.right) {
			columnSizes.add(head);
			i++;
		}
		this.optimalNode = (HeaderNode) root.right;
		findOptimalNode();
	}
	
	public void update(HeaderNode head) {
		
	}
	
	private void findOptimalNode() {
		short optimal = optimalNode.size;
		HeaderNode newOptimal = optimalNode;
		for (HeaderNode head = (HeaderNode) nodeManager.root.right; head != nodeManager.root; head = (HeaderNode) head.right) {
			if (head.size < optimal) {
				newOptimal = head;
				optimal = head.size;
			}
		}
	}
	
	public HeaderNode getColumn() {
		return this.optimalNode;
	}
}