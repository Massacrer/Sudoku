package me.will_s.school.sudoku.solver;

import java.util.List;
import me.will_s.school.sudoku.Grid;
import me.will_s.school.sudoku.TestInterface;
import me.will_s.school.sudoku.solver.HeaderNode.ConstraintType;

class Initialiser {
	public static NodeManager initialise(Grid grid) {
		NodeManager nodeManager = new NodeManager();
		boolean interrupted = false;
		// init columns
		interrupted = initHeaders(nodeManager);
		if (interrupted) {
			return null;
		}
		// link in nodes
		interrupted = addRows(nodeManager);
		if (interrupted) {
			return null;
		}
		// debug_postInit(nodeManager.root);
		// Remove existing numbers using DLX cover
		coverExisting(nodeManager, grid);
		return nodeManager;
	}
	
	// Columns represent constraints e.g. {R1C1 has a number}, {R1 has a 1},
	// etc. These are collected at the end and used to generate the solution by
	// following the instructions of the nodes in the column e.g. constraint {R1
	// has a 1} will be satisfied by precisely 1 solution part per number, e.g.
	// {R1 C2 V3}. This means to place a 3 in R1, C2 in the output grid.
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
					if (Solver.DEBUG) {
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
	
	@SuppressWarnings("unused")
	private static void debug_HeadInit(Node n) {
		TestInterface.dbgout("Init header " + n.getNumberString() + "\tleft "
				+ n.left.getNumberString() + ",\tright "
				+ n.right.getNumberString() + ",\tleft.right "
				+ n.left.right.getNumberString() + ",\tright.left "
				+ n.right.left.getNumberString());
	}
	
	/**
	 * Adds nodes in rows corresponding to placements of numbers in cells
	 * 
	 * @param nodeManager
	 *            See {@link NodeManager}
	 */
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
					
					if (Solver.DEBUG) {
						// debug_RowNodeInit(r, c, v, row); TODO: debug code
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
	
	/**
	 * Customises the linked list to the particular Sudoku grid that is to be
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
	 * satisfied, any other rows that satisfy them must also be removed from the
	 * list, due to the 'exactly once' stipulation of exact cover problems</li>
	 * </ol>
	 * 
	 * @param nodeManager
	 *            See {@link NodeManager}
	 * @param grid
	 *            the {@link Grid} that is to be solved
	 */
	// TODO: re-examine and check for completeness
	private static void coverExisting(NodeManager nodeManager, Grid grid) {
		Row row;
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				int v = grid.get(r, c);
				if (v != 0) {
					// This cell already has a value assigned to it
					// This existing placement satisfies constraints, so mark
					// them as such (cover them). Multiple runs of cover()
					// affecting the same row should not have compounding
					// effects
					row = nodeManager.rows.get(9 * 9 * r + 9 * c + v);
					for (Node node : row) {
						DLXImpl.cover(node.head);
					}
					// Add a node in this row (doesn't matter which, same
					// SolutionPart value) to the list of pre-existing solution
					// parts
					nodeManager.preExistingSolutionParts.push(row.get(0));
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static void debug_postInit(RootNode root) {
		TestInterface.divide();
		TestInterface.dbgout("Grid layout:");
		String out = new String("Headers: ");
		Node firstNode = root.right.down;
		for (int i = 0; i < 326; i++) {
			firstNode = firstNode.left;
			out += firstNode.hashCode() + ", ";
		}
		TestInterface.dbgout(out.substring(0, out.length() - 2) + "...");
		TestInterface.divide();
		TestInterface.dbgout("Rows: ");
		// Iterate over rows
		for (int i = 0; i < 800; i++) {
			// Iterate over nodes in row, starting with row 1 (first row of
			// non-headers)
		}
	}
	
	@SuppressWarnings("unused")
	private static void debug_coverExisting() {
		
	}
}