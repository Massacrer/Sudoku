// SudokuSolver by Will Shelver
package me.will_s.school.sudoku;

import java.util.ArrayList;
import java.util.List;

//TODO: javadoc comments

public class Solver {
	// List of nodes, used as strong reference to nodes to prevent garbage
	// collection
	static List<Node> nodes = new ArrayList<Node>();
	
	// Public-facing method. Note: argument subject to change in type, passed in
	// by UI-level code.
	// Settling on List<List<Integer>> for now.
	public static void solve(List<List<Integer>> grid) {
		Initialiser.initialise(grid);
		// DLX goes here
	}
}

class Initialiser {
	
	public static void initialise(List<List<Integer>> grid) {
		initHeaders();
		initConstraints();
		Initialiser.coverNodesFromInput(grid);
	}
	
	// Init column headers
	// Columns represent solution parts in the Sudoku grid e.g. (R1C2 = 3)
	// There are a total of 9^3 = 729 headers to create
	public static void initHeaders() {
		Node root = RootNode.get();
		Node left = root;
		Node right;
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
	public static void initConstraints() {
		
	}
	
	// Called from initConstraints(), sets up a single mapping of an RCV (header
	// node) to the constraints it covers
	static void link(int row, int column, int value) {
		
	}
	
	// Given the full 729x324 sparse grid, remove given numbers as possibilities
	public static void coverNodesFromInput(List<List<Integer>> grid) {
		// Have fun with this :)
	}
}

class Node {
	public Node head;
	public Node up;
	public Node down;
	public Node left;
	public Node right;
	
	// Constructor time
	// TODO: implement as few constructors as possible
	
	protected Node() {
		this.head = this;
		this.up = this;
		this.down = this;
		this.left = this;
		this.right = this;
		Solver.nodes.add(this);
	}
	
	public Node(Node head) {
		this();
		this.head = head;
	}
	
	// TODO: Possibly unnecessary, review after init code complete
	public Node(NodeSet nodes) {
		for (Node n : nodes.nodes) {
			if (n == null) {
				n = this;
			}
		}
		
		this.head = nodes.nodes[0];
		this.up = nodes.nodes[1];
		this.down = nodes.nodes[2];
		this.left = nodes.nodes[3];
		this.right = nodes.nodes[4];
		Solver.nodes.add(this);
	}
	
	class NodeSet {
		public Node[] nodes = new Node[5];
		
		public NodeSet(Node head, Node up, Node down, Node left, Node right) {
			nodes[0] = head;
			nodes[1] = up;
			nodes[2] = down;
			nodes[3] = left;
			nodes[4] = right;
		}
	}
}

class HeaderNode extends Node {
	public SolutionPart solutionPartId;
	
	protected HeaderNode() {
		super();
	}
	
	public HeaderNode(Node left, Node right, SolutionPart part) {
		super();
		this.left = left;
		this.right = right;
		this.solutionPartId = part;
	}
}

class RootNode extends HeaderNode {
	private static Node root;
	
	// Creates the One True RootNode
	static {
		root = new RootNode();
	}
	
	// Private constructor ensures no rogue RootNodes are floating around for
	// whatever reason
	private RootNode() {
		// this.head should never be accessed, so setting it to null will help
		// uncover some types of bugs in the node walker code
		super();
		RootNode.root = this;
	}
	
	public static Node get() {
		return RootNode.root;
	}
}

// Take note: this class will need reworking slightly if this code is being
// reused to solve grids larger that 9x9
class SolutionPart {
	// Using short to save space, using bit shifting to store 3 values (row,
	// column, value, each in range 1 <= x <= 9)
	// Assigning 4 bytes to each int, so max unsigned value of each variable =
	// 2^4 = 16
	// Order of storage (msb -> lsb) is row, column, value
	// So data bytes in short looks like 0000rrrrccccvvvv
	private short info = 0;
	
	public SolutionPart(int r, int c, int v) {
		// Make sure all 3 values are sane for the grid size, and can be stored
		// in 4 bytes.
		if ((r < 1) || (r > 9) || (c < 1) || (c > 9) || (v < 1) || (v > 9)) {
			throw new IllegalArgumentException("Argument out of bounds: r" + r
					+ ",c" + c + ",v" + v);
		}
		
		// TODO: probably not necessary, implement check and confirm at some
		// point
		r = r & 0x0000000f;
		c = c & 0x0000000f;
		v = v & 0x0000000f;
		
		short i = (short) ((r << 8) | (c << 4) | (v));
		info = i;
	}
	
	public int getRow() {
		int i = info;
		i = i & 0x00000f00;
		return i >> 8;
	}
	
	public int getColumn() {
		int i = info;
		i = i & 0x000000f0;
		return i >> 4;
	}
	
	public int getValue() {
		int i = info;
		i = i & 0x0000000f;
		return i;
	}
}