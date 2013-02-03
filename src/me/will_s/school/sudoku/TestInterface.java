// SudokuSolver by Will Shelver
package me.will_s.school.sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import me.will_s.school.sudoku.solver.Solver;

public class TestInterface {
	
	private static File debug;
	private static FileWriter debugWriter;
	
	private BufferedReader reader;
	private List<Grid> grids;
	private Solver solver;
	
	public TestInterface() {
		reader = new BufferedReader(new InputStreamReader(System.in));
		grids = new ArrayList<Grid>();
		grids.add(new Grid(9));
		solver = null;
		TestInterface.debug = new File("C:\\users\\william\\desktop\\debug.txt");
		try {
			debugWriter = new FileWriter(debug);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:m:s");
			debugWriter.write("Starting new solve at "
					+ sdf.format(new Date(System.currentTimeMillis())) + "\n");
			debugWriter.flush();
		} catch (IOException e) {
			System.out.println("Could not create FileWriter");
			e.printStackTrace();
		}
	}
	
	public static void dbgout(String string) {
		if (debugWriter != null) {
			try {
				debugWriter.write(string + "\n");
				debugWriter.flush();
			} catch (IOException e) {
				System.out.println("Debug output error");
				e.printStackTrace();
			}
		}
	}
	
	public static void divide() {
		dbgout("-----------------------------------------------------------------");
	}
	
	public static void main(String[] args) {
		dbgout("Init main");
		boolean finished = false;
		TestInterface ti = new TestInterface();
		do {
			finished = ti.uiLoop();
		} while (!finished);
	}
	
	boolean uiLoop() {
		System.out.print(">");
		String[] in = readInput().split(" ");
		
		if (in[0].equalsIgnoreCase("E")) {
			enterValue(in);
			printGrid(0);
			return false;
		}
		if (in[0].equalsIgnoreCase("Q")) {
			return true;
		}
		if (in[0].equalsIgnoreCase("V")) {
			if (in.length == 2) {
				int i = Integer.parseInt(in[1]);
				if (i >= grids.size()) {
					System.out.println("Invalid grid number, " + grids.size()
							+ " grids available");
					return false;
				}
				printGrid(i);
			} else {
				System.out.println("Grids available: " + grids.size());
				printGrid(0);
			}
			return false;
		}
		if (in[0].equalsIgnoreCase("S")) {
			this.solver = Solver.startSolve(grids.get(0), new Callback());
			System.out.println("Solve started");
			return false;
		}
		if (in[0].equalsIgnoreCase("F")) {
			this.solver.abortSolve();
			return false;
		}
		if (in[0].equalsIgnoreCase("R")) {
			this.solver.abortSolve();
			this.solver = null;
			return false;
		}
		if (in[0].equalsIgnoreCase("C")) {
			// clear grid
			this.grids.clear();
			grids.add(new Grid(9));
			return false;
		}
		if (in[0].equalsIgnoreCase("T")) {
			// quickly supply test grid
			this.grids.clear();
			grids.add(this.getTestGrid());
			printGrid(0);
		}
		/*
		 * if (in[0].equalsIgnoreCase("slnpt")) { if (in.length == 4) { int r =
		 * Integer.parseInt(in[1]); int c = Integer.parseInt(in[2]); int v =
		 * Integer.parseInt(in[3]); short s = SolutionPart.getSolutionPart(r, c,
		 * v);
		 * 
		 * SolutionPart sln = new SolutionPart(s);
		 * System.out.println("Solution part [" + sln.getRow() + ":" +
		 * sln.getColumn() + ":" + sln.getValue() + "]"); } return false; }
		 */
		return false;
	}
	
	@SuppressWarnings("unused")
	private void intuitiveEntry() {
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				this.grids.get(0).set(r, c, '*');
			}
		}
	}
	
	class Callback implements Runnable {
		public void run() {
			System.out.println();
			System.out.println("Result ready");
			System.out.println();
			grids = solver.getResult();
			System.out.println("Available results: " + grids.size());
			if (grids.size() == 0) {
				grids = new ArrayList<Grid>();
				grids.add(new Grid(9));
				System.out.println("Resetting grids\nAvailable grids: 1");
			}
			System.out.println(">");
		}
	}
	
	void enterValue(String[] in) {
		if (in.length == 4) {
			try {
				int r = Integer.parseInt(in[1]);
				int c = Integer.parseInt(in[2]);
				int v = Integer.parseInt(in[3]);
				grids.get(0).set(r - 1, c - 1, v);
				return;
			} catch (NumberFormatException e) {
				System.out.println("Invalid input format");
			}
		}
		System.out.println("Syntax: e row col val");
		
	}
	
	String readInput() {
		String in = null;
		try {
			in = reader.readLine();
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		return in;
	}
	
	public List<Grid> getGrid() {
		// code here
		return null;
	}
	
	public static void printGrid(Grid grid) {
		String out = new String();
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				if (c == 0) {
					// System.out.print('|');
					out += '|';
				}
				
				int v = grid.get(r, c);
				if (v > 0) {
					out += " " + v + " ";
					// System.out.print(" " + v + " ");
				} else {
					out += "   ";
					// System.out.print("   ");
				}
				
				if (((c + 1) % 3) == 0) {
					out += '|';
					// System.out.print('|');
				}
				if (c == 8) {
					out += "\n";
					// System.out.println();
				}
			}
			if (((r + 1) % 3) == 0) {
				out += "- - - - - - - - - - - - - - - -\n";
				// System.out.println("- - - - - - - - - - - - - - - -");
			}
		}
		out += "\n";
		// System.out.println();
		System.out.print(out);
	}
	
	void printGrid(int i) {
		printGrid(grids.get(i));
	}
	
	Grid getTestGrid() {
		Grid grid = new Grid(9);
		for (int i = 0; i < 9; i++) {
			grid.set(i, i, i);
			grid.set((i + 1) % 9, (i + 2) % 9, (i + 7) % 9);
		}
		return grid;
	}
}
