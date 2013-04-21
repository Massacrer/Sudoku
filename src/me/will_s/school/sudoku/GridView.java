package me.will_s.school.sudoku;

import java.util.BitSet;
import java.util.List;
import me.will_s.school.sudoku.solver.Solver;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.*;
import android.widget.RelativeLayout.LayoutParams;

/** Activity to display a grid to the user, and allow them to manipulate and
 * solve it */
public class GridView extends Activity {
	/** The set of {@link EditText} boxes used to display the grid to the user */
	private EditText[][] grid;
	/** The {@link Grid} containing the solved grid relating to the grid the user
	 * is currently editing */
	private Grid solvedGrid;
	/** Note attached to this puzzle */
	private String note;
	/** Text box in file save dialog. Temporary fix. */
	private EditText dialogTextBox;
	
	public GridView() {
		this.solvedGrid = null;
		this.note = null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_view);
		this.grid = LinearGridManager.createGrid(this);
		ButtonManager.createButtons(this);
		// Adjust layout for horizontal screen orientation
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			((LinearLayout) (this.findViewById(R.id.outer))).setOrientation(LinearLayout.HORIZONTAL);
			((LinearLayout) (this.findViewById(R.id.buttonHolder))).setOrientation(LinearLayout.VERTICAL);
		}
		String filename = this.getIntent().getStringExtra("filename");
		if (filename != null) {
			this.populateFromStoredPuzzle(this.getStoredPuzzle(filename));
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_grid_view, menu);
		
		MenuItem abortSolveItem = menu.add(Menu.NONE, 44, Menu.NONE,
				"Abort solve");
		MenuItem savePuzzleItem = menu.add(Menu.NONE, 45, Menu.NONE,
				"Save grid");
		// DEBUG
		MenuItem refreshLayoutItem = menu.add(Menu.NONE, 42, Menu.NONE,
				"Refresh layout");
		MenuItem assignListenerItem = menu.add(Menu.NONE, 43, Menu.NONE,
				"Temp assign key listener");
		
		// Only executed on capable Android versions
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			refreshLayoutItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			assignListenerItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			abortSolveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			savePuzzleItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 42: // Debug
				LinearGridManager.createGrid(this);
				Toast.makeText(this, "Layout reloaded", Toast.LENGTH_LONG).show();
				System.out.println("Reloaded GridView layout");
				break;
			case 43: // Debug
				for (EditText[] arr : this.grid) {
					for (EditText edit : arr) {
						edit.setOnKeyListener(LinearGridManager.cellKeyListener);
					}
				}
				break;
			case 44:
				SolveManager.abortSolve();
				break;
			case 45:
				this.showSavePuzzleNameDialog();
		}
		
		return true;
	}
	
	/** @return The current state of the user's grid, as a {@link Grid} */
	private Grid getCurrentGrid() {
		Grid grid = new Grid(9);
		for (int r = 0; r < 9; r++) {
			EditText[] arr = this.grid[r];
			for (int c = 0; c < 9; r++) {
				EditText box = arr[c];
				String s = box.getText().toString();
				grid.set(r, c, (s == "" ? 0 : Integer.parseInt(s)));
			}
		}
		return grid;
	}
	
	/** Called by {@link SolveManager} when the full solution grid has been
	 * found. If there is more than one solution, it informs the user, otherwise
	 * it sets {@link #solvedGrid} to the result
	 * 
	 * @param list
	 *            The list of solution grids */
	private void informSolveComplete(List<Grid> list) {
		if (list.size() > 1) {
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					GridView.this.finish();
				}
			};
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setMessage("The current puzzle does not have a single unique solution");
			builder.setPositiveButton("Continue editing", null);
			builder.setNegativeButton("Back", listener);
			builder.show();
		} else {
			this.solvedGrid = list.get(0);
		}
	}
	
	/** @param fileName
	 *            The filename to read from
	 * @return The puzzle saved under {@code FileName} */
	private StoredFileManager.StoredPuzzle getStoredPuzzle(String fileName) {
		return (new StoredFileManager(this)).getSavedPuzzle(fileName);
	}
	
	/** Loads a previously saved puzzle into the editor. Sets the cells in the
	 * grid to the appropriate numbers and sets {@link #solvedGrid} to the full
	 * solved grid
	 * 
	 * @param puzzle
	 *            The puzzle to load. */
	private void
			populateFromStoredPuzzle(StoredFileManager.StoredPuzzle puzzle) {
		if (puzzle == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setPositiveButton("Ok", null);
			builder.setTitle("Load Failed");
			builder.setMessage("The puzzle you selected could not be loaded");
			builder.show();
			return;
		}
		this.solvedGrid = puzzle.getGrid();
		this.note = puzzle.getNote();
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				if (puzzle.getVisibleCells().get(9 * r + c) == true) {
					this.grid[r][c].setText(puzzle.getGrid().get(r, c));
				}
			}
		}
	}
	
	private void showSavePuzzleNameDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		EditText textView = new EditText(this);
		builder.setView(textView);
		this.dialogTextBox = textView;
		builder.setTitle("Enter filename");
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("Save", new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String filename = GridView.this.dialogTextBox.getText().toString();
				Boolean saveWorked = ((new StoredFileManager(GridView.this))).savePuzzle(
						GridView.this.createPuzzle(), filename);
				Toast.makeText(getApplicationContext(),
						(saveWorked ? "File saved" : "Save failed"),
						Toast.LENGTH_SHORT).show();
			}
		});
		builder.show();
	}
	
	private StoredFileManager.StoredPuzzle createPuzzle() {
		BitSet visible = new BitSet();
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				if (this.grid[r][c].getText().toString() != "") {
					visible.set(9 * r + c);
				}
			}
		}
		return new StoredFileManager.StoredPuzzle(this.solvedGrid, this.note,
				visible);
	}
	
	// Well, it's still just about better that creating 81 textboxes manually in
	// XML...
	
	// TODO: Redo this to minimise number of layouts used, and amount of nesting
	// - rendering them is expensive
	
	/** Class to manage the creation and laying out of the grid of
	 * {@link EditText} boxes in the {@link GridView} activity */
	private static class LinearGridManager {
		/** An arbitrary number that forms the base resource ID, to prevent ID
		 * conflicts */
		private static int baseId;
		/** Used by Android to validate input to the {@link EditText} boxes */
		private static InputFilter[] lengthLimit;
		/** The layout is formed of nested {@link LinearLayout}s, references to
		 * which are stored here */
		private static LinearLayout[] innerRows;
		/** The outer {@link LinearLayout}, used to contain the whole grid */
		private static LinearLayout gridHolder;
		/** Used to align the {@link EditText} boxes properly in their layouts */
		private static LinearLayout.LayoutParams params;
		/** Key listener to assign to the {@link EditText} boxes */
		private static View.OnKeyListener cellKeyListener;
		
		static {
			innerRows = new LinearLayout[27];
			baseId = 0x42;
			lengthLimit = new InputFilter[] { new InputFilter.LengthFilter(1) };
			// cellClickListener
			params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.width = LinearLayout.LayoutParams.MATCH_PARENT;
			params.height = LinearLayout.LayoutParams.MATCH_PARENT;
			params.weight = 1.0f;
		}
		
		/** Creates and displays the grid of {@link EditText} boxes in the
		 * activity
		 * 
		 * @param activity
		 *            The activity to create the grid in
		 * @return the array of {@link EditText} boxes displayed. Boxes are
		 *         placed in the arrays at indices corresponding to their x and
		 *         y offsets from the top left */
		static EditText[][] createGrid(Activity activity) {
			gridHolder = (LinearLayout) activity.findViewById(R.id.linearGridHolder);
			
			EditText[][] output = new EditText[9][9];
			
			// Iterate over outerRow<i>
			for (int i = 0; i < 3; i++) {
				LinearLayout currentOuterRow = (LinearLayout) gridHolder.getChildAt(i);
				
				// And over outerCol<i><j>
				for (int j = 0; j < 3; j++) {
					LinearLayout currentOuterCol = (LinearLayout) currentOuterRow.getChildAt(j);
					// Clear out pre-existing views
					currentOuterCol.removeAllViews();
					
					// Set padding
					currentOuterCol.setPadding(2, 2, 2, 2);
					
					// Add 3 inner rows (LinearLayout - vertical)
					// innerRow<i><j><k> (normalised layout - fill row(9), drop
					// col)
					for (int k = 0; k < 3; k++) {
						LinearLayout layout = new LinearLayout(activity);
						int offset = 3 * 3 * i + 3 * k + j;
						layout.setId(baseId + offset);
						innerRows[offset] = layout;
						currentOuterCol.addView(layout, params);
						// LinearLayout[] layouts = new LinearLayout[27];
					}
				}
			}
			
			cellKeyListener = createCellKeyListener();
			
			// Create boxes
			// Done after main loop to ensure all innerRows are created
			for (int i = 0; i < 27; i++) {
				// create individual edittext boxes
				for (int j = 0; j < 3; j++) {
					EditText box = new EditText(activity);
					
					box.setInputType(InputType.TYPE_CLASS_NUMBER);
					// box.setOnClickListener(cellClickListener);
					// box.setOnKeyListener(cellKeyListener);
					box.setFilters(lengthLimit);
					
					innerRows[i].addView(box, params);
					
					box.setWidth((int) box.getPaint().measureText("0")
							+ box.getPaddingLeft() + box.getPaddingRight());
					
					int offset = (3 * i) + j;
					output[offset % 9][offset / 9] = box;
					
					// DEBUG
					/*
					System.out.println("i = " + i + ", j = " + j);
					System.out.println("Assigning grid[" + (i % 9) + "]["
							+ (i / 9) + "]");
					box.setText("" + offset);
					System.out.println("index = "
							+ (offset)
							+ ", value = "
							+ output[offset % 9][offset / 9].getText().toString());
					*/
				}
			}
			return output;
		}
		
		/** Creates a key listener, used for debugging purposes
		 * 
		 * @return The key listener created */
		private static OnKeyListener createCellKeyListener() {
			return new OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// DEBUG
					System.out.println("View " + v.getId() + " cleared");
					((EditText) v).setText(event.getUnicodeChar());
					return false;
				}
			};
		}
	}
	
	/** Class used to manage the creation and functionality of buttons in the
	 * {@link GridView} activity */
	private static class ButtonManager {
		/** The activity to manage buttons in */
		private static GridView activity;
		/** Callback handler for the "hint" button */
		private static View.OnClickListener hintButtonListener;
		/** Callback handler for the "solve" button */
		private static View.OnClickListener solveButtonListener;
		/** Callback handler for the "reset" button */
		private static View.OnClickListener resetButtonListener;
		
		static {
			hintButtonListener = new View.OnClickListener() {
				public void onClick(View v) {
					// TODO: implement hint functionality
				}
			};
			solveButtonListener = new View.OnClickListener() {
				public void onClick(View v) {
					GridView.SolveManager.startSolve(activity);
				}
			};
			resetButtonListener = new View.OnClickListener() {
				public void onClick(View v) {
					for (EditText[] array : activity.grid) {
						for (EditText edit : array) {
							edit.setText("");
						}
					}
				}
			};
		}
		
		/** Assigns button callback listeners
		 * 
		 * @param gridView
		 *            The activity to add the buttons to */
		static void createButtons(GridView gridView) {
			ButtonManager.activity = gridView;
			
			Button hintButton = (Button) activity.findViewById(R.id.hintButton);
			Button solveButton = (Button) activity.findViewById(R.id.solveButton);
			Button resetButton = (Button) activity.findViewById(R.id.resetButton);
			
			hintButton.setText("Hint");
			solveButton.setText("Solve");
			resetButton.setText("Reset");
			
			hintButton.setOnClickListener(hintButtonListener);
			solveButton.setOnClickListener(solveButtonListener);
			resetButton.setOnClickListener(resetButtonListener);
		}
	}
	
	/** Class to manage interaction between the {@link GridView} activity and the
	 * {@link Solver} */
	private static class SolveManager {
		/** The activity that is using the {@link Solver} */
		private static GridView gridView;
		/** The Solver that is currently running */
		private static Solver solver;
		/** This is run when the solver has finished */
		private static Runnable callback;
		
		static {
			SolveManager.callback = new Runnable() {
				public void run() {
					SolveManager.gridView.runOnUiThread(new Runnable() {
						public void run() {
							SolveManager.gridView.informSolveComplete(SolveManager.solver.getResult());
						}
					});
				}
			};
		}
		
		/** Try to solve the current grid */
		static void startSolve(GridView gridView) {
			SolveManager.gridView = gridView;
			Grid grid = SolveManager.gridView.getCurrentGrid();
			// TODO: implement minimum-size check for grids
			if (solver != null) {
				abortSolve();
				solver = Solver.startSolve(grid, callback);
			}
		}
		
		public static void abortSolve() {
			if (solver instanceof Solver) {
				solver.abortSolve();
				solver = null;
			}
		}
	}
}