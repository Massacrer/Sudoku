package me.will_s.school.sudoku.solver;

// Take note: this class will need reworking slightly if this code is being
// reused to solve grids larger that 9x9

// Note: this class deals with 0 <= [rvc] <= 8, not 1 <= [rcv} <= 9
class SolutionPart {
	// Using short to save space, using bit shifting to store 3 values (row,
	// column, value, each in range 1 <= x <= 9)
	// Assigning 4 bytes to each int, so max unsigned value of each variable =
	// 2^4 - 1 = 15
	// Order of storage (msb -> lsb) is row, column, value
	// So data bytes in short looks like 0000rrrrccccvvvv
	private short data = 0;
	
	public SolutionPart(int a, int b, int c) {
		this.data = getSolutionPart(a, b, c);
	}
	
	public SolutionPart(short number) {
		this.data = number;
	}
	
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
	
	public int getA() {
		return getA(data);
	}
	
	public static int getA(int i) {
		i = i & 0x00000f00;
		return i >> 8;
	}
	
	public int getB() {
		return getB(data);
	}
	
	public static int getB(int i) {
		i = i & 0x000000f0;
		return i >> 4;
	}
	
	public int getC() {
		return getC(data);
	}
	
	public static int getC(int i) {
		i = i & 0x0000000f;
		return i;
	}
}