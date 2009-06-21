package org.dodgybits.android.shuffle.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.dto.ShuffleProtos.Catalogue;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class PreferencesRestoreBackupActivity extends Activity
	implements View.OnClickListener {
    private static final String cTag = "PreferencesRestoreBackupActivity";
    
    private Spinner mFileSpinner;
    
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.backup_restore);
        
        mFileSpinner = (Spinner) findViewById(R.id.filename);
        setupFileSpinner();
        
        addSavePanelListeners();
    }
    
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.saveButton:
            	restoreBackup();
                break;

            case R.id.discardButton:
            	finish();
                break;
        }
    }

    private void addSavePanelListeners() {
        // Setup the bottom buttons
        View view = findViewById(R.id.saveButton);
        view.setOnClickListener(this);
        view = findViewById(R.id.discardButton);
        view.setOnClickListener(this);
    }
    
    private void setupFileSpinner() {
    	String storage_state = Environment.getExternalStorageState();
    	if (! Environment.MEDIA_MOUNTED.equals(storage_state)) {
    		// TODO show alert
			Log.e(cTag, "Media is not mounted: " + storage_state);
			return;
    	}
    	
		File dir = Environment.getExternalStorageDirectory();
    	String[] files = dir.list(new FilenameFilter() {
    		@Override
    		public boolean accept(File dir, String filename) {
    			return filename.endsWith(".bak");
    		}
    	});
    	
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        		this, android.R.layout.simple_list_item_1, files);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFileSpinner.setAdapter(adapter);    	
    }

    private void restoreBackup() {
		File dir = Environment.getExternalStorageDirectory();
		String filename = mFileSpinner.getSelectedItem().toString();
		File backupFile = new File(dir, filename);
		try {
			FileInputStream in = new FileInputStream(backupFile);
			Catalogue catalogue = Catalogue.parseFrom(in);
			// TODO restore data...
			for (org.dodgybits.shuffle.dto.ShuffleProtos.Context context : catalogue.getContextList())
			{
				Log.d(cTag, "Context " + context.toString());
			}

			for (org.dodgybits.shuffle.dto.ShuffleProtos.Project project : catalogue.getProjectList())
			{
				Log.d(cTag, "Project " + project.toString());
			}

			for (org.dodgybits.shuffle.dto.ShuffleProtos.Task task : catalogue.getTaskList())
			{
				Log.d(cTag, "Task " + task.toString());
			}
			
		} catch (IOException ioe) {
			// TODO show alert
			Log.e(cTag, "Failed to restore backup " + ioe.getMessage());
			return;
		}
    }
    
}
