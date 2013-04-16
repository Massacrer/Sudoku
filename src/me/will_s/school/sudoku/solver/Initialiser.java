package me.will_s.school.sudoku.solver;

import me.will_s.school.sudoku.Grid;
import me.will_s.school.sudoku.TestInterface;
import me.will_s.school.sudoku.solver.HeaderNode.ConstraintType;

/** Class to convert a given {@link Grid} into a linked-list structure for the
 * DLX algorithm to work with */
class Initialiser {
	/** @param grid
	 *            The grid to convert
	 * @return a {@link NodeManager} containing the nodes of the linked list
	 *         structure, or {@code null} if the thread was interrupted at any
	 *         point during execution of these methods, indicating that the
	 *         process should be aborted */
	public static NodeManager initialise(Grid grid) {
		NodeManager nodeManager = new NodeManager();
		// init columns
		if (initHeaders(nodeManager)) {
			return null;
		}
		// link in nodes
		if (addRows(nodeManager)) {
			return null;
		}
		debug_postInit(nodeManager.root);
		// Remove existing numbers using DLX cover
		coverExisting(nodeManager, grid);
		TestInterface.divide();
		debug_postInit(nodeManager.root);
		return nodeManager;
	}
	
	// Columns represent constraints e.g. {R1C1 has a number}, {R1 has a 1},
	// etc. These are collected at the end and used to generate the solution by
	// following the instructions of the nodes in the column e.g. constraint {R1
	// has a 1} will be satisfied by precisely 1 solution part per number, e.g.
	// {R1 C2 V3}. This means to place a 3 in R1, C2 in the output grid.
	/** Initialises the {@link HeaderNode HeaderNodes} of the linked list, which
	 * represent the constraints that must be satisfied to form a solution to a
	 * Sudoku problem
	 * 
	 * @param nodeManager
	 *            The {@link NodeManager} to populate with header nodes
	 * @return {@code true} if the thread was interrupted in this method,
	 *         {@code false} otherwise */
	private static boolean initHeaders(NodeManager nodeManager) {
		RootNode root = nodeManager.root;
		HeaderNode last = root;
		// init constraints
		// cells, rows, columns, boxes
		// cells: j,k = r,c
		// rows: j,k = c,v
		// columns: j,k = r,v
		// boxes: j,k = box,v
		// box = 3 * (r / 3) + (c / 3)
		
		int number;
		
		for (HeaderNode.ConstraintType type : HeaderNode.ConstraintType.values()) {
			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < 9; k++) {
					
					number = SolutionPart.getSolutionPart(type.ordinal(), j, k);
					HeaderNode n = new HeaderNode(last, root, number);
					nodeManager.headers.get(type).add(n);
					n.left.right = n;
					last = n;
					if (TestInterface.DEBUG) {
						// debug_HeadInit(n); TODO: more debug code here
					}
					
				}
			}
			if (Thread.interrupted()) {
				return true;
			}
		}
		// complete the linked list
		last.right = root;
		root.left = last;
		return false;
	}
	
	/** Method used to print verbose debugging information during initialisation
	 * of new {@code HeaderNode}s
	 * 
	 * @param n
	 *            The number of the {@code HeaderNode} just created */
	@SuppressWarnings("unused")
	private static void debug_HeadInit(Node n) {
		TestInterface.dbgout("Init header " + n.getNumberString() + "\tleft "
				+ n.left.getNumberString() + ",\tright "
				+ n.right.getNumberString() + ",\tleft.right "
				+ n.left.right.getNumberString() + ",\tright.left "
				+ n.right.left.getNumberString());
	}
	
	/** Adds {@link Node Nodes} in rows corresponding to placements of numbers in
	 * cells. These {@code Nodes} are also linked into columns to indicate that
	 * they satisfy the constraint represented by that column.
	 * 
	 * @param nodeManager
	 *            The {@link NodeManager} to add the rows to
	 * 
	 * @return {@code true} if the thread was interrupted in this method,
	 *         {@code false} otherwise */
	private static boolean addRows(NodeManager nodeManager) {
		
		for (int r = 0; r < 9; r++) { // Select row
			for (int c = 0; c < 9; c++) { // Select column
				for (int v = 0; v < 9; v++) { // Select value
					short solutionPartData = SolutionPart.getSolutionPart(r, c,
							v);
					Row row = new Row(9 * 9 * r + 9 * c + v);
					nodeManager.rows.add(row);
					
					HeaderNode head;
					
					// Node to cover cell constraint
					head = nodeManager.getHeader(ConstraintType.SQUARE, r, c);
					row.add(new Node(head, solutionPartData));
					// Cover row constraint
					head = nodeManager.getHeader(ConstraintType.ROW, r, v);
					row.add(new Node(head, solutionPartData));
					// Row constraint
					head = nodeManager.getHeader(ConstraintType.COLUMN, c, v);
					row.add(new Node(head, solutionPartData));
					// Box constraint
					head = nodeManager.getHeader(ConstraintType.BOX,
							(3 * (r / 3)) + (c / 3), v);
					row.add(new Node(head, solutionPartData));
					
					// Link the nodes in the row into the linked list structure
					row.linkNodes();
					
					if (TestInterface.DEBUG) {
						// debug_RowNodeInit(r, c, v, row); //TODO: debug code
						// here
					}
				}
			}
			if (Thread.interrupted()) {
				return true;
			}
		}
		return false;
	}
	
	/** Method to print verbose debugging information about the initialisation of
	 * a {@link Node} in a row
	 * 
	 * @param r
	 *            The row of the value placement represented by this
	 *            {@code Node}
	 * @param c
	 *            The column
	 * @param v
	 *            The value
	 * @param row
	 *            The {@code Row} that the {@code Node} was placed in */
	@SuppressWarnings("unused")
	private static void debug_RowNodeInit(int r, int c, int v, Row row) {
		for (int i = 0; i < 4; i++) {
			Node node = row.get(i);
			String part;
			switch (i) {
				case 0:
					part = "cell";
					break;
				case 1:
					part = "row";
					break;
				case 2:
					part = "column";
					break;
				case 3:
					part = "box";
					break;
				default:
					part = "Invalid part no. ( i = " + i + ")";
					break;
			}
			TestInterface.dbgout("Enter " + node.toString() + " - " + part);
			TestInterface.dbgout("\tr:" + r + ", c:" + c + ", v:" + v
					+ ", row: " + row.number + ", part id: "
					+ Integer.toBinaryString(node.solutionPart));
			TestInterface.dbgout("\tleft: " + node.left.toString());
			TestInterface.dbgout("\tright: " + node.right.toString());
			TestInterface.dbgout("\tup: " + node.up.toString());
			TestInterface.dbgout("\tdown: " + node.down.toString());
		}
	}
	
	/** Customises the linked list to the particular Sudoku grid that is to be
	 * solved.<br />
	 * <br />
	 * This is done by:
	 * <ol>
	 * <li>Examining the supplied {@link Grid} for filled squares</li>
	 * <li>Locating the row corresponding to the placement of a number in a
	 * square, as found in the grid</li>
	 * <li>Since that solution part is known to be present in the grid, all
	 * columns (constraints) it satisfies can be removed from the set of
	 * unsatisfied constraints, by removing them from the header row of the
	 * linked list</li>
	 * <li>Since the constraints have been removed from the list due to being
	 * satisfied, any other rows that satisfy them are also removed from the
	 * list, due to the 'exactly once' stipulation of exact cover problems</li>
	 * </ol>
	 * 
	 * @param nodeManager
	 *            See {@link NodeManager}
	 * @param grid
	 *            The {@code Grid} that is to be solved */
	// TODO: re-examine and check for completeness
	private static void coverExisting(NodeManager nodeManager, Grid grid) {
		Row row;
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				// v = 0 for no number, 1-9 for valid numbers
				int v = grid.get(r, c);
				if (v > 0) {
					// This cell already has a value assigned to it
					// This existing placement satisfies constraints, so mark
					// them as such (cover them). Multiple runs of cover()
					// affecting the same row should not have compounding
					// effects UNLESS the column has subsequently been used in
					// another cover operation
					row = nodeManager.rows.get(9 * 9 * r + 9 * c + (v - 1));
					for (Node node : row) {
						DLXImpl.cover(node.head);
					}
					if (TestInterface.DEBUG) {
						TestInterface.dbgout("[" + r + ":" + c + "] value: "
								+ v + ", row no: " + row.number + ", err = "
								+ (row.number - (9 * 9 * r + 9 * c + v)));
					}
					// Add a node in this row (doesn't matter which, same
					// SolutionPart value) to the list of pre-existing solution
					// parts
					nodeManager.existingSolutionParts.push(row.get(0).solutionPart);
				}
			}
		}
	}
	
	/** Method to print verbose debugging information about the state of the grid
	 * after the initialisation has been completed
	 * 
	 * @param root
	 *            The {@code RootNode} of the grid to analyse */
	@SuppressWarnings("unused")
	private static void debug_postInit(RootNode root) {
		TestInterface.divide();
		TestInterface.dbgout("Grid layout:");
		String out = new String();
		for (Node head = root.right; head != root; head = head.right) {
			out += "Header " + head.getNumberString() + ":\t";
			
			for (Node node = head.down; node != head; node = node.down) {
				out += node.getNumberString() + ",\t";
			}
			out += "\n";
		}
		TestInterface.dbgout(out + "...");
	}
}