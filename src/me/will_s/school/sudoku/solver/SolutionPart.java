package me.will_s.school.sudoku.solver;

// Take note: this class will need reworking slightly if this code is being
// reused to solve grids larger that 9x9

// Note: this class accepts 0 <= [rvc] <= 8, not 1 <= [rcv] <= 9
/**
 * A class to manage the solution parts represented by rows in the grid.
 * Provides methods for storing and retrieving solution part IDs in a
 * space-efficient way, and converting between a row number and its solution
 * part number
 */
class SolutionPart {
	// Using short to save space, using bit shifting to store 3 values (row,
	// column, value, each in range 0 <= x <= 8)
	// Assigning 4 bits to each int, so max unsigned value of each variable =
	// 2^4 - 1 = 15
	// Order of storage (msb -> lsb) is row, column, value
	// So data bytes in short looks like 0000rrrrccccvvvv
	/**
	 * The solution part number representing the row, column and value, packed
	 * into 16 bytes as detailed in code comments
	 */
	private short data = 0;
	
	/**
	 * Creates a new {@link SolutionPart} for a given row, column and value
	 * combination
	 * 
	 * @param a
	 *            Row
	 * @param b
	 *            Column
	 * @param c
	 *            Value
	 */
	public SolutionPart(int a, int b, int c) {
		this.data = getSolutionPart(a, b, c);
	}
	
	/**
	 * Creates a new {@link SolutionPart} with its data field set so that
	 * subsequent calls to this object's Get*() methods will return a specific
	 * row, column and value
	 * 
	 * @param number
	 */
	public SolutionPart(short number) {
		this.data = number;
	}
	
	/**
	 * @param a
	 *            The row value to compress
	 * @param b
	 *            The column value
	 * @param c
	 *            The cell value
	 * @return The compressed representation of the row, column and value
	 *         numbers (see code comments for details of compression).
	 *         Compression is performed for space efficiency.
	 */
	public static short getSolutionPart(int a, int b, int c) {
		// Make sure all 3 values are sane for the grid size, and can be stored
		// in 4 bytes.
		if ((a < 0) || (a > 8) || (b < 0) || (b > 8) || (c < 0) || (c > 8)) {
			throw new IllegalArgumentException("Argument out of bounds: r" + a
					+ ",c" + b + ",v" + c);
		}
		
		// TODO: probably not necessary, implement check and confirm at some
		// point
		a = a & 0x0000000f;
		b = b & 0x0000000f;
		c = c & 0x0000000f;
		
		short i = (short) ((a << 8) | (b << 4) | (c));
		return i;
	}
	
	/**
	 * Instance-specific method.
	 * 
	 * @return The row value of this {@link SolutionPart}, as an integer. Calls
	 *         {@link #getA(int)}, where int == this.data.
	 */
	public int getA() {
		return getA(data);
	}
	
	/**
	 * Static method
	 * 
	 * @param i
	 *            the 16-bit data to extract the row value from
	 * @return The row value compressed in the data
	 */
	public static int getA(int i) {
		i = i & 0x00000f00;
		return i >> 8;
	}
	
	/**
	 * See {@link #getA()}
	 * 
	 * @return The column value
	 */
	public int getB() {
		return getB(data);
	}
	
	/**
	 * See {@link #getA(int)}
	 * 
	 * @param i
	 *            The data to read from
	 * @return the column value
	 */
	public static int getB(int i) {
		i = i & 0x000000f0;
		return i >> 4;
	}
	
	/**
	 * See {@link #getA()}
	 * 
	 * @return The cell value
	 */
	public int getC() {
		return getC(data);
	}
	
	/**
	 * See {@link #getA(int)}
	 * 
	 * @param i
	 *            The data to read from
	 * @return the cell value
	 */
	public static int getC(int i) {
		i = i & 0x0000000f;
		return i;
	}
	
	/**
	 * Returns a string representation of this object, used for debugging
	 * purposes. This string is in the form "r:c:v", where these letters stand
	 * for the row, column and cell values of this object.
	 */
	public String toString() {
		return getA(data) + ":" + getB(data) + ":" + getC(data);
	}
}