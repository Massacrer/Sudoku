// SudokuSolver by Will Shelver
package me.will_s.school.sudoku;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/** Main menu activity, containing code to manage the presentation and
 * functionality of this activity */
public class MainMenu extends Activity {
	/** Button to generate a new grid */
	private Button gridGenerateButton;
	/** Button for entering a new grid */
	private Button gridEntryButton;
	/** Button to load the saved puzzle management activity */
	private Button gridManagerButton;
	/** Button to open the Settings menu */
	private Button gridOptionsButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.gridGenerateButton = (Button) this.findViewById(R.id.main_generate_button);
		this.gridEntryButton = (Button) this.findViewById(R.id.main_grid_entry_button);
		this.gridManagerButton = (Button) this.findViewById(R.id.main_grid_manager_button);
		this.gridOptionsButton = (Button) this.findViewById(R.id.main_options_button);
		
		this.gridGenerateButton.setOnClickListener(new UnimplementedButtonHandler());
		this.gridEntryButton.setOnClickListener(new GridEntryButtonHandler());
		this.gridOptionsButton.setOnClickListener(new SettingsButtonHandler());
		this.gridManagerButton.setOnClickListener(new SavedGridsManagerButtonHandler());
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
				Toast.makeText(this, "Menu item selected: Test Item 1",
						Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_settings:
				Intent intent = new Intent(this, Preferences.class);
				this.startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	// Input handler section
	/** Callback handler for {@code gridEntryButton} */
	private class GridEntryButtonHandler implements OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(MainMenu.this, GridView.class);
			MainMenu.this.startActivity(intent);
		}
	}
	
	/** Callback handler for {@code gridOptionsButton} */
	private class SettingsButtonHandler implements OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(MainMenu.this, Preferences.class);
			MainMenu.this.startActivity(intent);
		}
	}
	
	/** Callback handler for {@code gridManagerButton} */
	private class SavedGridsManagerButtonHandler implements OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(MainMenu.this, SavedGridsManager.class);
			MainMenu.this.startActivity(intent);
		}
	}
	
	/** Temporary callback handler for buttons whose functionality has not yet
	 * been implemented, currently {@code gridGenerateButton} and
	 * {@code gridManagerButton} */
	private class UnimplementedButtonHandler implements OnClickListener {
		public void onClick(View view) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
			builder.setCancelable(false);
			builder.setMessage("This page is not yet implemented");
			builder.setTitle("Unimplemented Feature");
			builder.setPositiveButton("Ok", null);
			builder.show();
		}
	}
}
