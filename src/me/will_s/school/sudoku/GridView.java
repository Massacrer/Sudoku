package me.will_s.school.sudoku;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.*;

public class GridView extends Activity {
	private LinearLayout gridHolder;
	private List<List<EditText>> grid;
	
	public GridView() {
		// this.gridHolder = (LinearLayout) this.findViewById(R.id.GridHolder);
		this.grid = new ArrayList<List<EditText>>(9);
		for (List<EditText> l : grid) {
			l = new ArrayList<EditText>(9);
		}
	}
	
	private void createGrid() {
		RelativeLayout rl = new RelativeLayout(this); // TODO
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_view);
		this.createGrid();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_grid_view, menu);
		return true;
	}
}