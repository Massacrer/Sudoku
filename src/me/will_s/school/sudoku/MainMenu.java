// SudokuSolver by Will Shelver
package me.will_s.school.sudoku;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends Activity {
	private Button gridEntryButton;
	private Button gridManagerButton;
	private Button gridMagicButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.gridEntryButton = (Button) this.findViewById(R.id.main_grid_entry_button);
		this.gridManagerButton = (Button) this.findViewById(R.id.main_grid_manager_button);
		this.gridMagicButton = (Button) this.findViewById(R.id.main_magic_button);
		
		this.gridEntryButton.setOnClickListener(new GridEntryButtonHandler());
		this.gridMagicButton.setOnClickListener(new TemporaryButtonHandler());
		this.gridManagerButton.setOnClickListener(new TemporaryButtonHandler());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_testitem1:
				Toast.makeText(this.getApplicationContext(),
						"Menu item selected: Test Item 1", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_settings:
				// ...
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	// Input handler section
	
	private class GridEntryButtonHandler implements OnClickListener {
		public void onClick(View view) {
			handleGridEntryButton(view);
		}
	}
	
	void handleGridEntryButton(View view) {
		Intent intent = new Intent(this, GridView.class);
		startActivity(intent);
	}
	
	private class TemporaryButtonHandler implements OnClickListener {
		public void onClick(View view) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Button pressed: " + ((Button) view).getText(),
					Toast.LENGTH_SHORT);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();
		}
	}
}
