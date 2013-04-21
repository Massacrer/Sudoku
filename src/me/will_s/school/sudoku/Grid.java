package me.will_s.school.sudoku;

import java.io.Serializable;

/** Class to describe the placement of numbers in a Sudoku grid */
public class Grid implements Serializable {
	/** Java serial version unique ID, used by JVM IO to determine if class
	 * version is correct for deserialisation */
	private static final long serialVersionUID = 1L;
	/** The array to hold the values of the grid */
	private final int[][] grid;
	
	/** @param size
	 *            The size of the grid to construct */
	public Grid(int size) {
		grid = new int[size][size];
	}
	
	/** Sets a specific cell in the grid. Sets the cell at location {@code [r,c]}
	 * to value v
	 * 
	 * @param r
	 *            The row of the cell to update
	 * @param c
	 *            The column of the cell
	 * @param v
	 *            The value to set this cell to
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if r, c or v are not between 0 and the size of this grid */
	public void set(int r, int c, int v) throws IndexOutOfBoundsException {
		grid[r][c] = Integer.valueOf(v);
	}
	
	/** Gets the value of a cell in the grid
	 * 
	 * @param r
	 *            The row of the cell
	 * @param c
	 *            The column of the cell
	 * @return The value of this cell
	 * @throws IndexOutOfBoundsException
	 *             See {@link #set(int, int, int)} */
	public int get(int r, int c) throws IndexOutOfBoundsException {
		return grid[r][c];
	}
}
