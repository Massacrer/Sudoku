package me.will_s.school.sudoku.solver;

import java.util.List;
import me.will_s.school.sudoku.Grid;

// The real init class, see comment above initHeaders. Old Initialiser to be harvested for code and deprecated.
class Initialiser {
	public static NodeManager initialise(Grid grid) {
		NodeManager nodeManager = new NodeManager();
		// init columns
		initHeaders(nodeManager);
		// link in nodes
		addRows(nodeManager);
		// Remove existing numbers using DLX cover
		
		return nodeManager;
	}
	
	// Columns represent constraints e.g. {R1C1 has a number}, {R1 has a 1},
	// etc. These are collected at the end and used to generate the solution by
	// following the instructions of the nodes in the column e.g. constraint {R1
	// has a 1} will be satisfied by precisely 1 solution part per number, e.g.
	// {R1 C2 V3}. This means to place a 3 in R1, C2 in the output grid.
	private static void initHeaders(NodeManager nodeManager) {
		RootNode root = nodeManager.root;
		HeaderNode left = root;
		// init constraints
		// cells, rows, columns, boxes
		// cells: j,k = r,c
		// rows: j,k = c,v
		// columns: j,k = r,v
		// boxes: j,k = box,v
		
		// boxes(x) = r \ 3
		// boxes(y) = r % 3
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < 9; k++) {
					HeaderNode n = new HeaderNode(left, root, (9 * 9 * i + 9
							* j + k));
					nodeManager.headers.add(n);
					n.left.right = n;
					left = n;
				}
			}
		}
		// complete the linked list
		left.right = root;
		root.left = left;
	}
	
	/**
	 * Adds nodes in rows corresponding to placements of numbers in cells
	 * 
	 * @param nodeManager
	 *            See {@link NodeManager}
	 */
	private static void addRows(NodeManager nodeManager) {
		List<Node> nodes = nodeManager.nodes;
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				for (int v = 0; v < 9; v++) {
					Node node;
					HeaderNode head;
					Node[] arr = new Node[4];
					// i = cells(0), rows(1), columns(2), boxes(3)
					for (int i = 0; i < 4; i++) {
						// Number = node id, used for printing answers
						short number = SolutionPart.getSolutionPart(r, c, v);
						// h is the column header of the new node
						head = nodeManager.headers.get((i * 81) + (9 * r) + c);
						node = new Node(head, number);
						arr[i] = node;
						
						// Link new node vertically
						node.up = head.up;
						node.down = head;
						node.down.up = node;
						node.up.down = node;
					}
					hLinkNodes(arr);
					for (Node n : arr) {
						nodes.add(n);
					}
				}
			}
		}
	}
	
	/**
	 * Links an array of {@link Node nodes} together in a horizontal doubly
	 * circularly linked list
	 * 
	 * @param nodes
	 *            The nodes to be linked
	 */
	private static void hLinkNodes(Node[] nodes) {
		for (int i = 1; i < nodes.length - 2; i++) {
			nodes[i].left = nodes[i - 1];
			nodes[i].right = nodes[i + i];
		}
		nodes[0].left = nodes[nodes.length - 1];
		nodes[nodes.length - 1].right = nodes[0];
	}
	
	// TODO: re-examine and check for completeness
	private static void coverExisting(NodeManager nodeManager, Grid grid) {
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				int v = grid.get(r, c);
				if (v != 0) {
					// get a node in the row corresponding to this RCV placement
					// TODO: double check this returns a correct node by
					// checking node.number
					Node node = nodeManager.nodes.get(9 * 9 * r + 9 * c + v);
					// For each constraint this placement satisfies
					for (Node n = node.right; n != node; n = n.right) {
						// Remove constraint, and any other placements that
						// satisfy it, from consideration, it is already filled
						DLXImpl.cover(n.head);
					}
				}
			}
		}
	}
}