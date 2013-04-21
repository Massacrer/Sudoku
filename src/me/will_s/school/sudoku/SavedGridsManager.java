package me.will_s.school.sudoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SavedGridsManager extends Activity {
	private StoredFileManager storedFileManager;
	private TextView currentSelectedEntry;
	private View buttonHolder;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_saved_grids_manager);
		ListView listView = (ListView) this.findViewById(R.id.savedGridsList);
		this.currentSelectedEntry = null;
		this.buttonHolder = this.findViewById(R.id.savedGridsButtonHolder);
		
		this.storedFileManager = new StoredFileManager(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				storedFileManager.getSavedPuzzles());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new ListItemClickListener());
		
	}
	
	private void handleSelection(TextView textView) {
		this.currentSelectedEntry = textView;
		this.buttonHolder.setVisibility(View.VISIBLE);
	}
	
	private String getCurrentFilename() {
		if (this.currentSelectedEntry == null) {
			return null;
		} else {
			return this.currentSelectedEntry.getText().toString();
		}
	}
	
	private void openSelectedPuzzle() {
		String filename = this.getCurrentFilename();
		Intent intent = new Intent(this, GridView.class);
		intent.putExtra("filename", filename);
		this.startActivity(intent);
	}
	
	private class ListItemClickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TextView textView = (TextView) view;
			SavedGridsManager.this.handleSelection(textView);
			
		}
	}
	
	private class ResumeButtonHandler implements View.OnClickListener {
		public void onClick(View v) {
			SavedGridsManager.this.openSelectedPuzzle();
		}
	}
	
	private class DeleteButtonHandler implements View.OnClickListener {
		public void onClick(View v) {
			SavedGridsManager.this.storedFileManager.deleteFile(SavedGridsManager.this.getCurrentFilename());
		}
	}
	
	private class CancelButtonhandler implements View.OnClickListener {
		public void onClick(View v) {
			SavedGridsManager.this.buttonHolder.setVisibility(View.GONE);
		}
	}
}
