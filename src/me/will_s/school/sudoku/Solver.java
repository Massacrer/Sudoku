package me.will_s.school.sudoku;

import java.util.ArrayList;
import java.util.List;

public class Solver {
	List<Node> nodes = new ArrayList<Node>();
	
	public Solver() {}
	public Solver(List<List<Integer>> grid) {
		Node root = Initialiser.initialise(grid);
		nodes.add(root);
	}
	
	// Public-facing method. Note: argument subject to change in type, passed in by UI-level code.
	// Settling on List<List<Integer>> for now.
	public void solve (List<List<Integer>> grid) {
		
	}
}

class Initialiser {
	public static Node initialise(List<List<Integer>> grid) {
		Node root = new HeaderNode(null,"root");
		root.head = root;
		root.point(0x15, root);
		
		initHeader(root);
		
		return root;
	}
	
	private static void initHeader(Node root) {
		for (int i = 0; i < 9; i++) {
			
		}
	}
}

class Node {
	public Node head;
	public Node up;
	public Node down;
	public Node left;
	public Node right;
	
	// Constructor time
	public Node() {}
	
	public Node(Node head) {
		this.head = head;
		this.point(0x15, null);
	}
	
	public Node(Node head, Node up, Node down, Node left, Node right) {
		this.head = head;
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
	}
	
	public void point(int mask, Node target) {
		if ((mask & 0x1) == 1) { this.up = target;}
		if ((mask & 0x2) == 1) { this.down = target;}
		if ((mask & 0x4) == 1) { this.left = target;}
		if ((mask & 0x8) == 1) { this.right = target;}
	}
}

class HeaderNode extends Node {
	public String name;
	public int size;
	
	public HeaderNode() {}
	
	public HeaderNode(Node head, String name) {
		super(head);
		this.name = name;
	}
}