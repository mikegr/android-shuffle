package org.dodgybits.android.shuffle.activity.preferences;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PreferencesCreateBackupActivity extends Activity 
	implements View.OnClickListener {
    private static final String cTag = "PreferencesCreateBackupActivity";
    
    private EditText mFilenameWidget;
    private Button mSaveButton;
    private Button mCancelButton;
    private ProgressBar mProgressBar;
    private TextView mProgressText;
    
    private AsyncTask<?, ?, ?> mTask;
    
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.backup_create);
        
        mProgressBar = (ProgressBar) findViewById(R.id.progress_horizontal);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText = (TextView) findViewById(R.id.progress_label);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mCancelButton = (Button) findViewById(R.id.discardButton);
        mCancelButton.setText(R.string.cancel_button_title);
        
        mFilenameWidget = (EditText) findViewById(R.id.filename);
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String defaultText = "shuffle-" + formatter.format(today) + ".bak";
        mFilenameWidget.setText(defaultText);
        mFilenameWidget.setSelection(0, defaultText.length() - 4);
        
        addSavePanelListeners();
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.saveButton:
            	setControlsEnabled(false);
            	createBackup();
                break;

            case R.id.discardButton:
            	finish();
                break;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }
    }

    private void addSavePanelListeners() {
        // Setup the bottom buttons
        View view = findViewById(R.id.saveButton);
        view.setOnClickListener(this);
        view = findViewById(R.id.discardButton);
        view.setOnClickListener(this);
    }
    
    private void setControlsEnabled(boolean enabled) {
    	mFilenameWidget.setEnabled(enabled);
    	mSaveButton.setEnabled(enabled);
    	mCancelButton.setEnabled(enabled);
    }
    
    private void showOkButton() {
    	mSaveButton.setVisibility(View.GONE);
    	mCancelButton.setText(R.string.ok_button_title);
    	mCancelButton.setEnabled(true);
    }
    
    private void createBackup() {
    	String filename = mFilenameWidget.getText().toString();
    	if (TextUtils.isEmpty(filename)) {
    		// TODO show alert
			Log.e(cTag, "Filename was empty");
			return;
    	} 
    	
    	Log.d(cTag, "Checking media state");
    	String storage_state = Environment.getExternalStorageState();
    	if (! Environment.MEDIA_MOUNTED.equals(storage_state)) {
    		// TODO show alert
			Log.e(cTag, "Media is not mounted: " + storage_state);
			return;
    	}
    	
		File dir = Environment.getExternalStorageDirectory();
		File backupFile = new File(dir, filename);
		try {
	    	Log.d(cTag, "Creating backup file");
			backupFile.createNewFile();
			FileOutputStream out = new FileOutputStream(backupFile);
			mTask = new CreateBackupTask().execute(out);
		} catch (IOException ioe) {
			// TODO show alert
			Log.e(cTag, "Failed to create backup " + ioe.getMessage());
			return;
		}
    }
    
    private class CreateBackupTask extends AsyncTask<OutputStream, CreateBackupProgress, Void> {

    	public Void doInBackground(OutputStream... out) {
            try {
            	writeBackup(out[0]);
            } catch (Exception e) {
            	String message = "Backup failed " + e.getMessage();
            	updateProgress(100, message, true);
            }
            
            return null;
        }
    	
        private void writeBackup(OutputStream out) throws IOException {
        	Builder builder = Catalogue.newBuilder();
        	
        	writeContexts(builder, 0, 15);
        	writeProjects(builder, 15, 30);
        	writeTasks(builder, 30, 100);

        	builder.build().writeTo(out);
        	out.close();

        	String message = "Backup complete.";
        	updateProgress(100, message, false);
        }
        
        private void writeContexts(Builder builder, int progressStart, int progressEnd)
        {
	    	Log.d(cTag, "Writing contexts");
            Cursor cursor = getContentResolver().query(
            		Shuffle.Contexts.CONTENT_URI, Shuffle.Contexts.cFullProjection, 
            		null, null, null);
            int i = 0;
            int total = cursor.getCount();
            String type = getString(R.string.context_name);
        	while (cursor.moveToNext()) {
        		Context context = BindingUtils.readContext(cursor, getResources());
            	builder.addContext(context.toDto());
    			String text = getString(R.string.backup_progress, type, context.name);
    			int percent = calculatePercent(progressStart, progressEnd, ++i, total);
            	updateProgress(percent, text, false);
        	}
        	cursor.close();
        }

        private void writeProjects(Builder builder, int progressStart, int progressEnd)
        {
	    	Log.d(cTag, "Writing projects");
            Cursor cursor = getContentResolver().query(
            		Shuffle.Projects.CONTENT_URI, Shuffle.Projects.cFullProjection, 
            		null, null, null);
            int i = 0;
            int total = cursor.getCount();
            String type = getString(R.string.project_name);
        	while (cursor.moveToNext()) {
        		Project project = BindingUtils.readProject(cursor);
            	builder.addProject(project.toDto());
    			String text = getString(R.string.backup_progress, type, project.name);
    			int percent = calculatePercent(progressStart, progressEnd, ++i, total);
            	updateProgress(percent, text, false);
        	}
        	cursor.close();
        }
        
        private void writeTasks(Builder builder, int progressStart, int progressEnd)
        {
	    	Log.d(cTag, "Writing tasks");
            Cursor cursor = getContentResolver().query(
            		Shuffle.Tasks.CONTENT_URI, Shuffle.Tasks.cExpandedProjection, 
            		null, null, null);
            int i = 0;
            int total = cursor.getCount();
            String type = getString(R.string.task_name);
        	while (cursor.moveToNext()) {
        		Task task = BindingUtils.readTask(cursor, getResources());
            	builder.addTask(task.toDto());
    			String text = getString(R.string.backup_progress, type, task.description);
    			int percent = calculatePercent(progressStart, progressEnd, ++i, total);
            	updateProgress(percent, text, false);
        	}
        	cursor.close();
        }
        
        private int calculatePercent(int start, int end, int current, int total) {
        	return start + (end - start) * current / total;
        }
        
        private void updateProgress(int progressPercent, String details, boolean isError) {
        	CreateBackupProgress progress = new CreateBackupProgress();
        	progress.progressPercent = progressPercent;
        	progress.details = details;
        	progress.isError = isError;
        	publishProgress(progress);
        }
        
		@Override
		public void onProgressUpdate (CreateBackupProgress... progresses) {
			CreateBackupProgress progress = progresses[0];
	        mProgressBar.setVisibility(View.VISIBLE);
	        mProgressBar.setProgress(progress.progressPercent);
	        mProgressText.setText(progress.details);
	        if (progress.progressPercent == 100) {
	            showOkButton();
	        }
		}
		
        public void onPostExecute() {
            mTask = null;
        }
    	
    }
    
    private class CreateBackupProgress {
    	public int progressPercent;
    	public String details;
    	public boolean isError;
    }

}
