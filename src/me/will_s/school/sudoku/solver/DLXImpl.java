// SudokuSolver by Will Shelver
package me.will_s.school.sudoku.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import me.will_s.school.sudoku.Grid;

public class DLXImpl {
	private final NodeManager nodeManager;
	private Stack<Short> solutionParts;
	private List<Grid> solutions;
	
	public DLXImpl(NodeManager nodeManager) {
		this.nodeManager = nodeManager;
		this.solutionParts = new Stack<Short>();
		this.solutions = new ArrayList<Grid>(1);
	}
	
	public List<Grid> solve() {
		// return array of all possible solutions (should only be 1 entry)
		return this.solutions;
	}
	
	private void algorithmX(RootNode root) {
		if (nodeManager.root.right == nodeManager.root) {
			addCurrentAsSolution();
		}
	}
	
	private Node selectColumn() {
		// Replace this with a more efficient implementation if / when able.
		// This will do for now.
		return nodeManager.root.right;
	}
	
	private void addCurrentAsSolution() {
		Grid grid = new Grid(9);
		for (Short s : this.solutionParts) {
			SolutionPart sp = new SolutionPart(s);
			grid.set(sp.getRow(), sp.getColumn(), sp.getValue());
		}
		this.solutions.add(grid);
	}
	
	static void cover(HeaderNode head) {
		head.right = head.left;
		head.left = head.right;
		for (Node node = head.down; node != head; node = node.down) {
			
		}
	}
	
	static void uncover(HeaderNode head) {
		
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