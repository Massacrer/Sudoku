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
		Initialiser.initHeaders();
		Initialiser.setupNodesFromInput(grid);
		// DLX goes here
	}
}

class Initialiser {
	// Init column headers
	// Columns represent solution parts in the Sudoku grid e.g. (R1C2 = 3)
	// There are a total of 9^3 = 729 headers to create
	public static void initHeaders() {
		Node root = RootNode.get();
		Node left = root;
		for (int r = 1; r <= 9; r++) {
			for (int c = 1; c <= 9; c++) {
				for (int v = 1; v <= 9; v++) {
					// Init header nodes with identifiers and link them into the
					// grid here
				}
			}
		}
	}
	
	public static void setupNodesFromInput(List<List<Integer>> grid) {
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
	// TODO: only implement one constructor, figure out which of these would be
	// more efficient
	public Node(Node head) {
		this.head = head;
		this.point((byte) 0x15, null);
		Solver.nodes.add(this);
	}
	
	public Node(Node head, Node up, Node down, Node left, Node right) {
		this.head = head;
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
		Solver.nodes.add(this);
	}
	
	public void point(byte mask, Node target) {
		if (target == null) {
			target = this;
		}
		// TODO: test bitmasking and comparison
		if ((mask & 0x1) == 1)
			this.up = target;
		if ((mask & 0x2) == 1)
			this.down = target;
		if ((mask & 0x4) == 1)
			this.left = target;
		if ((mask & 0x8) == 1)
			this.right = target;
	}
}

class HeaderNode extends Node {
	public SolutionPart solutionPartIdentifier;
	
	public HeaderNode(Node head, SolutionPart part) {
		super(head);
		this.solutionPartIdentifier = part;
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
		super(null, null);
		this.head = this;
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
	private short info = 0;
	
	public SolutionPart(short r, short c, short v) {
		// Make sure all 3 values are sane for the grid size, and can be stored
		// in 4 bytes.
		if ((r < 1) || (r > 9) || (c < 1) || (c > 9) || (v < 1) || (v > 9)) {
			throw new IllegalArgumentException("Argument out of bounds: r" + r
					+ ",c" + c + ",v" + v);
		}
		
		int i = (r << 8) | (c << 4) | (v);
		info = (short) i;
	}
	
	public int getRow() {
		// this step probably not necessary (can probably just use int i = info)
		// TODO: find out :)
		Integer i = Integer.valueOf(info);
		i = i & 0x00ff0000;
		return i >> 8;
	}
	
	public int getColumn() {
		Integer i = Integer.valueOf(info);
		i = i & 0x0000ff00;
		return i >> 4;
	}
	
	public int getValue() {
		Integer i = Integer.valueOf(info);
		i = i & 0x000000ff;
		return i;
	}
}