package me.will_s.school.sudoku.solver;

import java.util.ArrayList;
import java.util.List;

class Node {
	public static List<Node> nodes = new ArrayList<Node>();
	public HeaderNode head;
	public Node up;
	public Node down;
	public Node left;
	public Node right;
	
	// Constructor time
	// TODO: implement as few constructors as possible
	
	protected Node() {
		this.head = RootNode.get();
		this.up = this;
		this.down = this;
		this.left = this;
		this.right = this;
		Node.nodes.add(this);
	}
	
	public Node(HeaderNode head) {
		this();
		this.head = head;
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
	public static List<HeaderNode> nodes = new ArrayList<HeaderNode>(730);
	public SolutionPart solutionPartId;
	
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
		HeaderNode.nodes.add(this);
	}
}

class RootNode extends HeaderNode {
	private static RootNode root;
	
	// Creates the One True RootNode
	static {
		root = new RootNode();
	}
	
	// Private constructor ensures no rogue RootNodes are floating around for
	// whatever reason
	private RootNode() {
		this.head = this;
		this.up = this;
		this.down = this;
		this.left = this;
		this.right = this;
		this.solutionPartId = null;
	}
	
	public static RootNode get() {
		return RootNode.root;
	}
}