// SudokuSolver by Will Shelver
package me.will_s.school.sudoku.solver;

import java.util.ArrayList;
import java.util.List;

class Node {
	public HeaderNode head;
	public Node up;
	public Node down;
	public Node left;
	public Node right;
	
	// Constructor time
	// TODO: implement as few constructors as possible
	
	// Ensure all callers set this.head
	protected Node() {
		this.head = null;
		this.up = this;
		this.down = this;
		this.left = this;
		this.right = this;
	}
	
	public Node(HeaderNode head) {
		this();
		this.head = head;
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

class HeaderNode extends Node {
	public SolutionPart solutionPartId;
	public int size;
	
	protected HeaderNode() {
		super();
	}
	
	public HeaderNode(HeaderNode left, HeaderNode right, SolutionPart part) {
		this.head = this;
		this.left = left;
		this.right = right;
		this.up = this;
		this.down = this;
		this.solutionPartId = part;
		this.size = 0;
	}
}

class RootNode extends HeaderNode {
	
	RootNode() {
		this.head = this;
		this.up = this;
		this.down = this;
		this.left = this;
		this.right = this;
		this.solutionPartId = null;
		this.size = 0;
	}
}

class NodeManager {
	RootNode root;
	List<HeaderNode> headers;
	List<Node> nodes;
	
	public NodeManager() {
		root = new RootNode();
		headers = new ArrayList<HeaderNode>(729);
		nodes = new ArrayList<Node>();
	}
}