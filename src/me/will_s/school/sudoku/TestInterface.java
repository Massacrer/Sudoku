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

/** Class to provide command line based access to the functionality of the
 * solver, used for debugging purposes. Not intended for end user use, methods
 * in this class may cause crashes on invalid input. */
public class TestInterface {
	
	/** {@code File} used to output debug and logging information for review */
	private static File debug;
	/** {@code FileWriter} used to write text to {@link #debug} */
	private static FileWriter debugWriter;
	
	/** Reads user input from the command line */
	private BufferedReader reader;
	/** Current list of {@code Grids} being handled, created either manually
	 * (through the interface) or by the solving algorithm. */
	private List<Grid> grids;
	/** The currently running {@code Solver}, or null if none are running */
	private Solver solver;
	/** Used to enable debug output across the code */
	// TODO: set false for release
	public static final boolean DEBUG = false;
	
	/** Constructs a new {@code TestInterface}, setting {@link #reader} to the
	 * current terminal input and {@link #debugWriter} to the properly opened
	 * file */
	public TestInterface() {
		reader = new BufferedReader(new InputStreamReader(System.in));
		grids = new ArrayList<Grid>();
		grids.add(new Grid(9));
		solver = null;
		if (DEBUG) {
			TestInterface.debug = new File(
					"C:\\users\\william\\desktop\\debug.txt");
			try {
				debugWriter = new FileWriter(debug);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:m:s");
				debugWriter.write("Starting new solve at "
						+ sdf.format(new Date(System.currentTimeMillis()))
						+ "\n");
				debugWriter.flush();
			} catch (IOException e) {
				System.out.println("Could not create FileWriter");
				e.printStackTrace();
			}
		}
	}
	
	/** Method for any code in the project to output to the current testing
	 * console, if one is being used. If not, this method does nothing.
	 * 
	 * @param string
	 *            The string to output */
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
	
	/** Method to print a convenient division to the console (a screen's width of
	 * dashes(-)) */
	public static void divide() {
		dbgout("-----------------------------------------------------------------");
	}
	
	/** Main method called by the Java VM on startup. Creates a new
	 * {@code TestInterface} and enters its {@link #uiLoop()}
	 * 
	 * @param args
	 *            Any arguments given on the command line */
	public static void main(String[] args) {
		dbgout("Init main");
		boolean finished = false;
		TestInterface ti = new TestInterface();
		do {
			finished = ti.uiLoop();
		} while (!finished);
	}
	
	/** Presents a prompt that accepts certain commands (see source for details)
	 * 
	 * @return True if the program should exit, false otherwise */
	private boolean uiLoop() {
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
			if (this.solver != null) {
				this.solver.abortSolve();
				// this.solver = null;
			}
			return false;
		}
		/*		if (in[0].equalsIgnoreCase("R")) {
					this.solver.abortSolve();
					this.solver = null;
					return false;
				}*/
		if (in[0].equalsIgnoreCase("C")) {
			// clear grid
			this.grids.clear();
			grids.add(new Grid(9));
			printGrid(0);
			return false;
		}
		if (in[0].equalsIgnoreCase("T")) {
			// quickly supply test grid
			this.grids.clear();
			Grid grid;
			if (in.length > 1) {
				grid = this.getTestGrid2(Integer.parseInt(in[2]),
						Integer.parseInt(in[3]));
			} else {
				grid = this.getTestGrid();
			}
			grids.add(grid);
			printGrid(0);
			return false;
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
	
	/** {@code Runnable} passed to the {@code Solver}, to inform the user of
	 * completion and set the grid to the result returned */
	class Callback implements Runnable {
		public void run() {
			System.out.println();
			System.out.println("Result ready");
			System.out.println();
			grids = solver.getResult();
			
			if (grids == null) {
				System.out.println("An error occured while solving; solver returned null");
			} else {
				System.out.println("Available results: " + grids.size());
			}
			if (grids == null || grids.size() == 0) {
				grids = new ArrayList<Grid>();
				grids.add(new Grid(9));
				System.out.println("Resetting grids\nAvailable grids: 1");
			}
			TestInterface.this.solver = null;
			System.out.print(">");
		}
	}
	
	/** Takes the string entered in the interface and parses it, adding the
	 * specified value to the current grid
	 * 
	 * @param in
	 *            The command strings to be parsed */
	void enterValue(String[] in) {
		// syntax: "e r c v"
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
		// syntax: "e rcv"
		if (in.length == 2 && in[1].length() == 3) {
			char[] chars = in[1].toCharArray();
			// Minor code reuse time, better than method call overhead (?)
			int r = Integer.parseInt(String.valueOf(chars[0]));
			int c = Integer.parseInt(String.valueOf(chars[1]));
			int v = Integer.parseInt(String.valueOf(chars[2]));
			grids.get(0).set(r - 1, c - 1, v);
			return;
		}
		System.out.println("Syntax: \"e row col val\" or \"e rcv\"");
		
	}
	
	/** @return The line of text entered on the command line, when the user
	 *         presses enter */
	String readInput() {
		String in = null;
		try {
			in = reader.readLine();
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		return in;
	}
	
	/** Prints a grid to the console in a human-readable format, including lines
	 * around boxes
	 * 
	 * @param grid
	 *            The grid to print */
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
	
	/** If there is more than one grid in {@link #grids}, any grid can be printed
	 * by passing its list index to this method
	 * 
	 * @param i
	 *            The index of the grid to print in {@link #grids} */
	void printGrid(int i) {
		printGrid(grids.get(i));
	}
	
	/** Generates a grid with specific values pre-filled, used for testing
	 * specific functionality of the solving algorithm
	 * 
	 * @return The grid generated */
	Grid getTestGrid() {
		Grid grid = new Grid(9);
		for (int i = 0; i < 9; i++) {
			grid.set(i, i, i);
			grid.set((i + 1) % 9, (i + 2) % 9, (i + 7) % 9);
		}
		return grid;
	}
	
	/** Similar in function to {@link #getTestGrid()}, but returns a grid with
	 * different values, used as another test
	 * 
	 * @return The grid generated */
	Grid getTestGrid2(int x, int y) {
		Grid grid = new Grid(9);
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				int v = (c + 3 * r + r / 3 + 1) % 9;
				grid.set(r, c, v + 1);
			}
		}
		/*for (int r = 3; r < 6; r++) {
			for (int c = 3; c < 6; c++) {
				grid.set(r, c, 0);
			}
		}*/
		for (int i = 0; i < 9; i++) {
			grid.set((x + i) % 9, (y + i) % 9, 0);
			grid.set((x + i + 3) % 9, (y + i) % 9, 0);
		}
		
		return grid;
	}
}
