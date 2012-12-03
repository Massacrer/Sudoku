package me.will_s.school.sudoku;

// TODO: check for initialisation of things here
public class Grid {
	private final int size;
	private final int[][] grid;
	
	/**
	 * @param size
	 *            The size of the grid to construct
	 */
	public Grid(int size) {
		this.size = size;
		grid = new int[size][size];
	}
	
	public void set(int r, int c, int v) throws IndexOutOfBoundsException {
		grid[r][c] = Integer.valueOf(v);
	}
	
	public int get(int r, int c) throws IndexOutOfBoundsException {
		return grid[r][c];
	}
}
