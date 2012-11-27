// SudokuSolver by Will Shelver
package me.will_s.school.sudoku.solver;

import me.will_s.school.sudoku.Grid;
import android.util.SparseArray;

class Initialiser {
	
	/**
	 * Returns the doubly linked list representation of the input grid, for
	 * passing to the {@link Solver#thing() Solve() method}
	 * 
	 * @param grid
	 *            The Sudoku grid to find the solutions of
	 * @return The node manager containing links to the newly initialised doubly
	 *         linked list
	 */
	public static NodeManager initialise(Grid grid) {
		NodeManager nodeManager = new NodeManager();
		initHeaders(nodeManager);
		linkConstraints(nodeManager);
		// TODO: insert calls to DLX cover method here, to remove existing
		// numbers
		return nodeManager;
	}
	
	/**
	 * Method to set up the linked list of column {@link HeaderNode header
	 * nodes} that represent solution parts (see code comments)
	 * 
	 * @param nodeManager
	 *            The {@link NodeManager node manager} for this initialisation.
	 */
	// Init column headers
	// Columns represent solution parts in the Sudoku grid e.g. (R1C2 = 3)
	// Total no. of column headers == 9^3 = 729
	private static void initHeaders(NodeManager nodeManager) {
		HeaderNode root = nodeManager.root;
		HeaderNode left = root;
		for (int r = 1; r <= 9; r++) {
			for (int c = 1; c <= 9; c++) {
				for (int v = 1; v <= 9; v++) {
					HeaderNode n = new HeaderNode(left, root, new SolutionPart(
							r, c, v));
					nodeManager.headers.add(n);
					// TODO: check if final assignment to n.right.left works as
					// expected
					n.left.right = n;
					// This is in an if() to prevent rootNode.left being updated
					// 729 times for no reason
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
	
	// LATEST
	// Guide does it wrong way round
	// TODO-FIXME: remove that line - no guide exists
	// Rows represent requirements (constraints), eg. (R1C1 contains a
	// number), (R1 contains a 1), (C1 contains a 1), (box (top_left) contains a
	// 1)
	// Total no. of rows == (9^2)*4 = 324
	
	// Columns represent solution parts, eg. (R1C1==1) satisfies (R1C1
	// contains a number), (R1 contains a 1) etc.
	// Total no. of columns = 9^3 = 729
	
	/**
	 * Inserts {@link Node nodes} into the list structure to indicate that a
	 * solution part fulfils a particular constraint
	 * 
	 * This method is where the hard work is done in filling out the linked
	 * list. It uses the number of each {@link HeaderNode header node} to
	 * identify which constraints it covers, by treating each one as the
	 * declaration of a particular number in a particular cell
	 * 
	 * For example, the first header node represents the solution part {Row 1,
	 * Column 1, Value 1}
	 * 
	 * @param nodeManager
	 *            The {@link NodeManager node manager} for this initialisation.
	 */
	
	// Initialise all constraints - link each RCV with the constraints it
	// fulfils by creation of a node at the point of intersection
	private static void linkConstraints(NodeManager nodeManager) {
		// Link constraints to solutions (solutions = header rows, constraints =
		// numbered entries in arr
		NodeLinker nodeLinker = new NodeLinker();
		Node header = nodeManager.headers.get(0);
		// Iterate over each header, considering them by their r, c, v values
		// TODO: make sure this is done properly. Index being off at all will
		// break the entire thing
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
					// expected in final impl. Isolated testing indicates
					// success.
					nodeLinker.link(nodes[3], 3 * 9 * 9
							+ (9 * (3 * (r / 3) + (c / 3))) + (v + 1));
					header = header.right;
				}
			}
		}
	}
	
	/**
	 * Returns 4 {@link Node nodes}, vertically linked as a convenience
	 * 
	 * These nodes are used by the
	 * {@link Initialiser#linkConstraints(NodeManager) linkConstraints} method,
	 * and are part of the same column, so must be vertically linked
	 * 
	 * @param head
	 *            the {@link HeaderNode header node} to include in the
	 *            vertically linked loop. Also needed to initialise the nodes
	 *            properly, as each requires a link to its header node
	 * @param nodeManager
	 *            The {@link NodeManager node manager} for this initialisation.
	 * @return an array of {@link Node nodes}, vertically linked in a loop
	 */
	// Returns 4 Nodes, vertically linked (all belong to the same solution part)
	private static Node[] get4(HeaderNode head, NodeManager nodeManager) {
		Node[] nodes = new Node[4];
		for (int i = 0; i < 4; i++) {
			nodes[i] = new Node(head);
		}
		// Note: values here are correct, nodes[0].up and nodes[3].down must be
		// set separately, as they form the loop
		for (int i = 1; i < 3; i++) {
			nodes[i].up = nodes[i - 1];
			nodes[i].down = nodes[i + 1];
		}
		// Form the loop
		nodes[0].up = head;
		nodes[0].down = nodes[1];
		nodes[4].up = nodes[3];
		nodes[4].down = head;
		for (Node node : nodes) {
			nodeManager.nodes.add(node);
		}
		return nodes;
	}
}

class NodeLinker {
	/**
	 * Temporary array used as a framework to hold constraints Layout: 0:
	 * Cells(81), 1: Rows(81), 2: Columns(81), 3: Boxes(81)
	 */
	private SparseArray<Node> nodes;
	
	public NodeLinker() {
		nodes = new SparseArray<Node>(324);
	}
	
	/**
	 * Maps a node to the constraint it covers. Constraints are represented by
	 * rows in the list structure, so the node is inserted into the correct row
	 * and linked with any pre-existing nodes in the row to form the horizontal
	 * loop
	 * 
	 * @param node
	 *            The node to link into the horizontal loop
	 * @param constraint
	 *            the row to insert the node into
	 */
	// Called from initConstraints(), maps a node to the constraint it covers
	public void link(Node node, int constraint) {
		Node rightmostNodeAtConstraint = nodes.get(constraint);
		nodes.put(constraint, node);
		if (rightmostNodeAtConstraint == null) {
			// This is the first node in this constraint, simply return after
			// adding it, no need for mapping
			return;
		}
		// Due to order of calls from linkConstraints(), current node will
		// always be rightmost
		
		// Insert node into grid
		node.right = rightmostNodeAtConstraint;
		node.left = node.right.left;
		node.right.left = node;
		node.left.right = node;
	}
}