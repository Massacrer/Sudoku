package me.will_s.school.sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;
import android.app.Activity;
import android.content.Context;

/** Class used to provide simple access to saved puzzles stored on the device's
 * internal storage */
public class StoredFileManager {
	/** The folder used to store the puzzle files on the device filesystem */
	private File folder;
	
	/** Creates a new {@code StoredFileManager}, setting {@link #folder} to the
	 * folder named "puzzles" under {@code activity}'s custom data file folder
	 * 
	 * @param activity
	 *            The calling activity, used to get the root folder to store
	 *            data in */
	StoredFileManager(Activity activity) {
		this.folder = activity.getDir("puzzles", Context.MODE_PRIVATE);
	}
	
	/** @return An array of strings containing the names of all files in
	 *         {@code #folder} */
	String[] getSavedPuzzles() {
		return this.folder.list();
	}
	
	/** Takes a {@link StoredPuzzle} and saves it to the puzzles folder, with the
	 * filename specified by {@code fileName}
	 * 
	 * @param puzzle
	 *            The {@link StoredPuzzle} to save to storage
	 * @param fileName
	 *            The filename to save this puzzle as
	 * @return {@code true} if the file was saved successfully, {@code false}
	 *         otherwise */
	boolean savePuzzle(StoredPuzzle puzzle, String fileName) {
		File puzzleFile = new File(this.folder, fileName);
		if (puzzleFile.exists()) {
			return savePuzzleToFile(puzzleFile, puzzle);
		} else {
			return false;
		}
	}
	
	/** Gets the {@link StoredPuzzle} saved in the file with the filename
	 * specified in {@code fileName}
	 * 
	 * @param fileName
	 *            The name of the file to read from
	 * @return The {@code StoredPuzzle} read from the file, if successful,
	 *         {@code null} otherwise */
	StoredPuzzle getSavedPuzzle(String fileName) {
		File puzzleFile = new File(this.folder, fileName);
		if (puzzleFile.exists()) {
			return getStoredPuzzleFromFile(puzzleFile);
		} else {
			return null;
		}
	}
	
	/** Deleted a given save file
	 * 
	 * @param fileName
	 *            The name of the file to delete
	 * @return {@code true} if the file existed and was deleted, {@code false}
	 *         otherwise */
	boolean deleteFile(String fileName) {
		File file = new File(this.folder, fileName);
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}
	
	/** Handles the serialisation of the {@code StoredPuzzle} and writing it out
	 * to the file
	 * 
	 * @param file
	 *            The {@link File} to write to
	 * @param sp
	 *            The {@link StoredPuzzle} to write
	 * @return {@code true} if the puzzle was written successfully,
	 *         {@code false} otherwise */
	private static boolean savePuzzleToFile(File file, StoredPuzzle sp) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeUnshared(sp);
			oos.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/** Handles the deserialisation of a puzzle from a file
	 * 
	 * @param file
	 *            The {@link File} to read from
	 * @return The {@link StoredPuzzle} read from the file, if successful,
	 *         {@code null} otherwise */
	private static StoredPuzzle getStoredPuzzleFromFile(File file) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					file));
			StoredPuzzle output = (StoredPuzzle) ois.readUnshared();
			ois.close();
			return output;
		} catch (Exception e) {
			return null;
		}
	}
	
	/** Represents a puzzle saved to, or retrieved from, the device's internal
	 * storage */
	static class StoredPuzzle implements java.io.Serializable {
		/** Java serial version unique ID, used by JVM IO to determine if class
		 * version is correct for deserialisation */
		private static final long serialVersionUID = 1L;
		/** The {@code Grid} that is part of this puzzle, in its solved state if
		 * possible */
		private Grid grid;
		/** The note attached to this puzzle by the user */
		private String note;
		/** A {@code BitSet} used to indicate which cells in the grid are visible
		 * to the user.<br />
		 * <br />
		 * Implementation: cell [r,v] should be visible to the user if
		 * {@code visibleCells.get(9*r + c) == true}, otherwise it should be
		 * hidden */
		private BitSet visibleCells;
		
		/** Constructs a new {@code StoredPuzzle}, with the fields set to the
		 * arguments
		 * 
		 * @param grid
		 *            The grid to store
		 * @param note
		 *            The note to store
		 * @param visibleCells
		 *            The set of visible cells to store */
		public StoredPuzzle(Grid grid, String note, BitSet visibleCells) {
			this.grid = grid;
			this.note = note;
			this.visibleCells = visibleCells;
		}
		
		/** @return The {@code Grid} of this puzzle */
		public Grid getGrid() {
			return this.grid;
		}
		
		/** @return The string containing the note attached to this puzzle */
		public String getNote() {
			return this.note;
		}
		
		/** @return The {@code BitSet} specifying which cells should be visible to
		 *         the user */
		public BitSet getVisibleCells() {
			return this.visibleCells;
		}
	}
}