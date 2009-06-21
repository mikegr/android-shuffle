package org.dodgybits.android.shuffle.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.shuffle.dto.ShuffleProtos.Catalogue;
import org.dodgybits.shuffle.dto.ShuffleProtos.Catalogue.Builder;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PreferencesCreateBackupActivity extends Activity 
	implements View.OnClickListener {
    private static final String cTag = "PreferencesCreateBackupActivity";
    
    private EditText mFilenameWidget;

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.backup_create);
        
        mFilenameWidget = (EditText) findViewById(R.id.filename);
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String defaultText = "shuffle-" + formatter.format(today) + ".bak";
        mFilenameWidget.setText(defaultText);
        
        addSavePanelListeners();
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.saveButton:
            	createBackup();
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
    
    private void createBackup() {
    	String filename = mFilenameWidget.getText().toString();
    	if (TextUtils.isEmpty(filename)) {
    		// TODO show alert
			Log.e(cTag, "Filename was empty");
			return;
    	} 
    	
    	String storage_state = Environment.getExternalStorageState();
    	if (! Environment.MEDIA_MOUNTED.equals(storage_state)) {
    		// TODO show alert
			Log.e(cTag, "Media is not mounted: " + storage_state);
			return;
    	}
    	
		File dir = Environment.getExternalStorageDirectory();
		File backupFile = new File(dir, filename);
		try {
			backupFile.createNewFile();
			FileOutputStream out = new FileOutputStream(backupFile);
			writeBackup(out);
		} catch (IOException ioe) {
			// TODO show alert
			Log.e(cTag, "Failed to create backup " + ioe.getMessage());
			return;
		}
    	
		String text = getResources().getString(R.string.toast_backup_saved, filename);
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        finish();
    }
    
    private void writeBackup(OutputStream out) throws IOException {
    	// TODO display progress
    	Builder builder = Catalogue.newBuilder();
    	
    	// write contexts
        Cursor contextCursor = getContentResolver().query(
        		Shuffle.Contexts.CONTENT_URI, Shuffle.Contexts.cFullProjection, 
        		null, null, null);
    	while (contextCursor.moveToNext()) {
    		Context context = BindingUtils.readContext(contextCursor, getResources());
        	builder.addContext(context.toDto());
    	}
    	contextCursor.close();
    	
    	// write projects
        Cursor projectCursor = getContentResolver().query(
        		Shuffle.Projects.CONTENT_URI, Shuffle.Projects.cFullProjection, 
        		null, null, null);
    	while (projectCursor.moveToNext()) {
    		Project project = BindingUtils.readProject(projectCursor);
        	builder.addProject(project.toDto());
    	}
    	projectCursor.close();
    	
    	// write tasks
        Cursor taskCursor = getContentResolver().query(
        		Shuffle.Tasks.CONTENT_URI, Shuffle.Tasks.cFullProjection, 
        		null, null, null);
    	while (taskCursor.moveToNext()) {
    		Task task = BindingUtils.readTask(taskCursor, getResources());
        	builder.addTask(task.toDto());
    	}
    	taskCursor.close();
    	
    	builder.build().writeTo(out);
    	out.close();
    }

}
