package me.will_s.school.sudoku.solver;

import java.util.List;
import android.util.SparseArray;

class Initialiser {
	
	public static void initialise(List<List<Integer>> grid) {
		initHeaders();
		linkConstraints();
		// TODO: insert calls to DLX cover method here, to remove existing
		// numbers
	}
	
	// Init column headers
	// Columns represent solution parts in the Sudoku grid e.g. (R1C2 = 3)
	// There are a total of 9^3 = 729 headers to create
	public static void initHeaders() {
		HeaderNode root = RootNode.get();
		HeaderNode left = root;
		for (int r = 1; r <= 9; r++) {
			for (int c = 1; c <= 9; c++) {
				for (int v = 1; v <= 9; v++) {
					HeaderNode n = new HeaderNode(left, root, new SolutionPart(
							r, c, v));
					// TODO: check if final assignment to n.right.left works as
					// expected
					n.left.right = n;
					if (r == 9 && c == 9 && v == 9) {
						n.right.left = n;
					}
					// Advance (pointer to prev. element) to current element and
					// end loop
					left = n;
				}
			}
		}
	}
	
	// Initialise all constraints - link each RCV with the constraints it
	// fulfils by creation of a node at the point of intersection
	public static void linkConstraints() {
		// Create temporary array as a framework to hold constraints
		/*
		 * Layout: 0: Cells(81), 1: Rows(81), 2: Columns(81), 3: Boxes(81)
		 * 
		 * For each 81, index is determined by 9c + r. Note: columns and rows
		 * are indexed 0 - 8. Overall index = (81 x section) + (9 x row) +
		 * column
		 */
		SparseArray<Node> arr = new SparseArray<Node>(324);
		// Link constraints to solutions (solutions = header rows, constraints =
		// numbered entries in arr
		for (int r = 1; r <= 9; r++) {
			for (int c = 1; c <= 9; c++) {
				for (int v = 1; v <= 9; v++) {
					
				}
			}
		}
	}
	
	// Returns 5 Nodes, vertically linked (all belong to the same solution part)
	private static Node[] get5(HeaderNode head) {
		Node[] nodes = new Node[5];
		for (int i = 0; i < 5; i++) {
			nodes[i] = new Node(head);
		}
		for (int i = 1; i < 4; i++) {
			nodes[i].up = nodes[i - 1];
			nodes[i].down = nodes[i + 1];
		}
		nodes[0].down = nodes[1];
		nodes[5].up = nodes[4];
		// Note: circular linking includes header nodes for some reason
		// TODO: find out while less tired
		nodes[0].up = head;
		nodes[5].down = head;
		return nodes;
	}
	
	// Called from initConstraints(), sets up a single mapping of an RCV (header
	// node) to the constraints it covers
	private static void link(int row, int column, int value) {
		
	}
	
	// Given the full 729x324 sparse grid, remove given numbers as possibilities
	// Removed - use functionality from main solver to do this without code
	// duplication
}