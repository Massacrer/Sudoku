package me.will_s.school.sudoku.solver;

import java.util.List;
import android.util.SparseArray;

class Initialiser {
	
	public static void initialise(List<List<Integer>> grid) {
		initHeaders();
		linkConstraints();
		ConstraintHolder.free(); // Free a bit of memory by removing constraint
									// framework
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
	
	static class ConstraintHolder {
		// Create temporary array as a framework to hold constraints
		/*
		 * Layout: 0: Cells(81), 1: Rows(81), 2: Columns(81), 3: Boxes(81)
		 */
		public static SparseArray<Node> arr;
		
		static {
			arr = new SparseArray<Node>(324);
		}
		
		public static void free() {
			arr = null;
		}
	}
	
	// Initialise all constraints - link each RCV with the constraints it
	// fulfils by creation of a node at the point of intersection
	public static void linkConstraints() {
		// Link constraints to solutions (solutions = header rows, constraints =
		// numbered entries in arr
		Node header = HeaderNode.nodes.get(0);
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				for (int v = 0; v < 9; v++) {
					Node[] nodes = get4((HeaderNode) header);
					// Insert nodes into their proper constraints
					// First node: cell
					link(nodes[0], 0 * 9 * 9 + 9 * r + c);
					// Second: row
					link(nodes[1], 1 * 9 * 9 + 9 * r + v);
					// Third: column
					link(nodes[2], 2 * 9 * 9 + 9 * c + v);
					// Fourth: box
					// Note: using integer division to get floor(r/3) etc.
					// easily
					// TODO: make sure this cluster of a calculation works as
					// expected in final impl. Unit testing indicates success.
					link(nodes[3], 3 * 9 * 9 + (9 * (3 * (r / 3) + (c / 3)))
							+ (v + 1));
					header = header.right;
				}
			}
		}
	}
	
	// Called from initConstraints(), maps a node to the constraint it covers
	private static void link(Node node, int constraint) {
		Node nodeAtConstraint = ConstraintHolder.arr.get(constraint);
		if (nodeAtConstraint == null) {
			// This is the first node in this constraint, simply add it to the
			// array and return, no need for mapping
			ConstraintHolder.arr.put(constraint, node);
			return;
		} else {
			// Due to order of calls from linkConstraints(), current node will
			// always be rightmost
			// Insert node into grid
			node.right = nodeAtConstraint;
			node.left = node.right.left;
			node.right.left = node;
			node.left.right = node;
		}
	}
	
	// Returns 4 Nodes, vertically linked (all belong to the same solution part)
	private static Node[] get4(HeaderNode head) {
		Node[] nodes = new Node[4];
		for (int i = 0; i < 4; i++) {
			nodes[i] = new Node(head);
		}
		for (int i = 1; i < 3; i++) {
			nodes[i].up = nodes[i - 1];
			nodes[i].down = nodes[i + 1];
		}
		nodes[0].down = nodes[1];
		nodes[4].up = nodes[3];
		// Note: circular linking includes header nodes for some reason
		// TODO: find out while less tired
		nodes[0].up = head;
		nodes[4].down = head;
		return nodes;
	}
	
	// Given the full 729x324 sparse grid, remove given numbers as possibilities
	// Removed - use functionality from main solver to do this without code
	// duplication
}