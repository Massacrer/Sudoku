// SudokuSolver by Will Shelver
package me.will_s.school.sudoku.solver;

import java.util.ArrayList;
import java.util.List;

/**
 * The fundamental component of the doubly circularly linked list, a Node
 * represents a connection between a constraint and a solution part that
 * satisfies it. See the methods of {@link Initialiser} and {@link Solver} for
 * use of nodes
 */
class Node {
	public HeaderNode head;
	public Node up;
	public Node down;
	public Node left;
	public Node right;
	public short number;
	
	// Constructor time
	// TODO: implement as few constructors as possible
	
	// All callers must set this.head
	protected Node() {
		this.head = null;
		this.up = this;
		this.down = this;
		this.left = this;
		this.right = this;
		this.number = 0;
	}
	
	public Node(HeaderNode head, short number) {
		this();
		this.head = head;
		this.number = number;
		head.size++;
	}
	
	// TODO: Possibly unnecessary, review after init code complete
	/*
	 * public Node(NodeSet nodes) { for (Node n : nodes.nodes) { if (n == null)
	 * { n = this; } }
	 * 
	 * this.head = nodes.nodes[0]; this.up = nodes.nodes[1]; this.down =
	 * nodes.nodes[2]; this.left = nodes.nodes[3]; this.right = nodes.nodes[4];
	 * Solver.nodes.add(this); }
	 * 
	 * class NodeSet { public Node[] nodes = new Node[5];
	 * 
	 * public NodeSet(Node head, Node up, Node down, Node left, Node right) {
	 * nodes[0] = head; nodes[1] = up; nodes[2] = down; nodes[3] = left;
	 * nodes[4] = right; } }
	 */
}

/**
 * Extension of {@link Node} that serves as a column header. This type has an
 * identifier in the form of a {@link SolutionPart} that identifies the row,
 * column and value this node's column represents. It also has an integer
 * specifying the current number of nodes in the column, which is a heuristic
 * for improving the speed of the {@link Solver} methods
 */
class HeaderNode extends Node {
	public short size;
	
	protected HeaderNode() {
		super();
	}
	
	public HeaderNode(HeaderNode left, HeaderNode right, int hash) {
		this.head = this;
		this.left = left;
		this.right = right;
		this.up = this;
		this.down = this;
		this.size = 0;
	}
}

/**
 * A special node that represents the entire list structure. It is linked
 * horizontally with the {@link HeaderNode}s, but is the only node in its
 * column. This is the node that is passed to any method that needs to deal with
 * the list structure.
 */
class RootNode extends HeaderNode {
	
	RootNode() {
		this.head = this;
		this.up = this;
		this.down = this;
		this.left = this;
		this.right = this;
		// this.solutionPart = 0;
		this.size = 0;
	}
}

/**
 * Class used to hold strong references to all nodes in the list structure, to
 * prevent them being garbage collected during operation of the {@link Solver}
 * methods
 */
class NodeManager {
	final RootNode root;
	final List<HeaderNode> headers;
	final List<Node> nodes;
	
	public NodeManager() {
		root = new RootNode();
		headers = new ArrayList<HeaderNode>(729);
		nodes = new ArrayList<Node>();
	}
}