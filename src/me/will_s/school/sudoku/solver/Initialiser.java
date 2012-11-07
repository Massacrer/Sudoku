// SudokuSolver by Will Shelver
package me.will_s.school.sudoku.solver;

import java.util.List;
import android.util.SparseArray;

class Initialiser {
	
	public static RootNode initialise(List<List<Integer>> grid) {
		NodeManager nodeManager = new NodeManager();
		initHeaders(nodeManager);
		linkConstraints(nodeManager);
		// TODO: insert calls to DLX cover method here, to remove existing
		// numbers
		return nodeManager.root;
	}
	
	// Init column headers
	// Columns represent solution parts in the Sudoku grid e.g. (R1C2 = 3)
	// There are a total of 9^3 = 729 headers to create
	private static void initHeaders(NodeManager nm) {
		HeaderNode root = nm.root;
		HeaderNode left = root;
		for (int r = 1; r <= 9; r++) {
			for (int c = 1; c <= 9; c++) {
				for (int v = 1; v <= 9; v++) {
					HeaderNode n = new HeaderNode(left, root, new SolutionPart(
							r, c, v));
					nm.headers.add(n);
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
	private static void linkConstraints(NodeManager nodeManager) {
		// Link constraints to solutions (solutions = header rows, constraints =
		// numbered entries in arr
		NodeLinker nodeLinker = new NodeLinker();
		Node header = nodeManager.headers.get(0);
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				for (int v = 0; v < 9; v++) {
					Node[] nodes = get4((HeaderNode) header, nodeManager);
					// Insert nodes into their proper constraints
					// First node: cell
					nodeLinker.link(nodes[0], 0 * 9 * 9 + 9 * r + c);
					// Second: row
					nodeLinker.link(nodes[1], 1 * 9 * 9 + 9 * r + v);
					// Third: column
					nodeLinker.link(nodes[2], 2 * 9 * 9 + 9 * c + v);
					// Fourth: box
					// Note: using integer division to get floor(r/3) etc.
					// easily
					// TODO: make sure this cluster of a calculation works as
					// expected in final impl. Unit testing indicates success.
					nodeLinker.link(nodes[3], 3 * 9 * 9
							+ (9 * (3 * (r / 3) + (c / 3))) + (v + 1));
					header = header.right;
				}
			}
		}
	}
	
	// Returns 4 Nodes, vertically linked (all belong to the same solution part)
	private static Node[] get4(HeaderNode head, NodeManager nodeManager) {
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
		for (Node node : nodes) {
			nodeManager.nodes.add(node);
		}
		return nodes;
	}
	
	// Given the full 729x324 sparse grid, remove given numbers as possibilities
	// Removed - use functionality from main solver to do this without code
	// duplication
}

class NodeLinker {
	private ConstraintHolder constraintHolder;
	
	NodeLinker() {
		this.constraintHolder = new ConstraintHolder();
	}
	
	// Called from initConstraints(), maps a node to the constraint it covers
	void link(Node node, int constraint) {
		Node nodeAtConstraint = constraintHolder.arr.get(constraint);
		constraintHolder.arr.put(constraint, node);
		if (nodeAtConstraint == null) {
			// This is the first node in this constraint, simply add it to the
			// array and return, no need for mapping
			return;
		}
		// Due to order of calls from linkConstraints(), current node will
		// always be rightmost
		// Insert node into grid
		node.right = nodeAtConstraint;
		node.left = node.right.left;
		node.right.left = node;
		node.left.right = node;
	}
	
	class ConstraintHolder {
		// Create temporary array as a framework to hold constraints
		/*
		 * Layout: 0: Cells(81), 1: Rows(81), 2: Columns(81), 3: Boxes(81)
		 */
		public SparseArray<Node> arr;
		
		public ConstraintHolder() {
			arr = new SparseArray<Node>(324);
		}
	}
}