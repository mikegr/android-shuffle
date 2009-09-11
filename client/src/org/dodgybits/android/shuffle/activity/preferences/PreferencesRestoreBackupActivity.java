package org.dodgybits.android.shuffle.activity.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.service.BaseLocator;
import org.dodgybits.android.shuffle.service.Locator;
import org.dodgybits.android.shuffle.service.Progress;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.shuffle.dto.ShuffleProtos.Catalogue;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class PreferencesRestoreBackupActivity extends Activity
	implements View.OnClickListener {
    private static final String RESTORE_BACKUP_STATE = "restoreBackupState";
    private static final String cTag = "PreferencesRestoreBackupActivity";
    
    private enum State {SELECTING, IN_PROGRESS, COMPLETE, ERROR};
    
    private State mState = State.SELECTING;
    private Spinner mFileSpinner;
    private Button mRestoreButton;
    private Button mCancelButton;
    private ProgressBar mProgressBar;
    private TextView mProgressText;
    
    private AsyncTask<?, ?, ?> mTask;
    
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        setContentView(R.layout.backup_restore);
        findViewsAndAddListeners();
		onUpdateState();
    }
    
    
    private void findViewsAndAddListeners() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_horizontal);
        mProgressText = (TextView) findViewById(R.id.progress_label);
        mRestoreButton = (Button) findViewById(R.id.saveButton);
        mCancelButton = (Button) findViewById(R.id.discardButton);
        mFileSpinner = (Spinner) findViewById(R.id.filename);
        
        mRestoreButton.setText(R.string.restore_button_title);
        
        mRestoreButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        
        // save progress text when we switch orientation
        mProgressText.setFreezesText(true);
        setupFileSpinner();
    }
    
    private void setupFileSpinner() {
    	String storage_state = Environment.getExternalStorageState();
    	if (! Environment.MEDIA_MOUNTED.equals(storage_state)) {
    		// TODO show alert
    		String message = getString(R.string.warning_media_not_mounted, storage_state);
			Log.e(cTag, message);
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

    
    
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.saveButton:
            	setState(State.IN_PROGRESS);
            	restoreBackup();
                break;

            case R.id.discardButton:
            	finish();
                break;
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putString(RESTORE_BACKUP_STATE, mState.name());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	String stateName = savedInstanceState.getString(RESTORE_BACKUP_STATE);
    	if (stateName == null) {
    		stateName = State.SELECTING.name();
    	}
    	setState(State.valueOf(stateName));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }
    }
    
    private void setState(State value) {
    	if (mState != value) {
    		mState = value;
    		onUpdateState();
    	}
    }
    
    private void onUpdateState() {
    	switch (mState) {
	    	case SELECTING:
	    		setButtonsEnabled(true);
	    		mFileSpinner.setEnabled(true);
	            mProgressBar.setVisibility(View.INVISIBLE);
	            mProgressText.setVisibility(View.INVISIBLE);
	            mCancelButton.setText(R.string.cancel_button_title);
	    		break;
	    		
	    	case IN_PROGRESS:
	    		setButtonsEnabled(false);
	        	mFileSpinner.setEnabled(false);
	        	
	        	mProgressBar.setProgress(0);
		        mProgressBar.setVisibility(View.VISIBLE);
	            mProgressText.setVisibility(View.VISIBLE);
	    		break;
	    		
	    	case COMPLETE:
	    		setButtonsEnabled(true);
	    		mFileSpinner.setEnabled(false);
		        mProgressBar.setVisibility(View.VISIBLE);
	            mProgressText.setVisibility(View.VISIBLE);
	        	mRestoreButton.setVisibility(View.GONE);
	        	mCancelButton.setText(R.string.ok_button_title);
	    		break;
	    		
	    	case ERROR:
	    		setButtonsEnabled(true);
	    		mFileSpinner.setEnabled(true);
		        mProgressBar.setVisibility(View.VISIBLE);
	            mProgressText.setVisibility(View.VISIBLE);
	        	mRestoreButton.setVisibility(View.VISIBLE);
	            mCancelButton.setText(R.string.cancel_button_title);
	    		break;	
    	}
    }

    private void setButtonsEnabled(boolean enabled) {
    	mRestoreButton.setEnabled(enabled);
    	mCancelButton.setEnabled(enabled);
    }    
    
    private void restoreBackup() {
		String filename = mFileSpinner.getSelectedItem().toString();
		mTask = new RestoreBackupTask().execute(filename);
    }
    
    private class RestoreBackupTask extends AsyncTask<String, Progress, Void> {

    	public Void doInBackground(String... filename) {
            try {
            	String message = getString(R.string.status_reading_backup);
				Log.d(cTag, message);
            	updateProgress(5, message, false);
        		File dir = Environment.getExternalStorageDirectory();
        		File backupFile = new File(dir, filename[0]);
    			FileInputStream in = new FileInputStream(backupFile);
    			Catalogue catalogue = Catalogue.parseFrom(in);
    			in.close();
    			
    			Locator<Context> contextLocator = addContexts(catalogue.getContextList(), 10, 20);
    			Locator<Project> projectLocator = addProjects(catalogue.getProjectList(), contextLocator, 20, 30);
    			addTasks(catalogue.getTaskList(), contextLocator, projectLocator, 30, 100);
    			
            	message = getString(R.string.status_backup_complete);
            	updateProgress(100, message, false);
            } catch (Exception e) {
            	String message = getString(R.string.warning_restore_failed, e.getMessage());
        		reportError(message);
            }
            
            return null;
        }
    	
    	private Locator<Context> addContexts(
				List<org.dodgybits.shuffle.dto.ShuffleProtos.Context> protoContexts,
				int progressStart, int progressEnd) {
			Set<String> contextNameSet = new HashSet<String>();
			for (org.dodgybits.shuffle.dto.ShuffleProtos.Context protoContext : protoContexts)
			{
				contextNameSet.add(protoContext.getName());
			}
			Map<String,Context> existingContexts = BindingUtils.fetchContextsByName(
					PreferencesRestoreBackupActivity.this, contextNameSet);
			
			// build up the locator and list of new contacts
			BaseLocator<Context> contextLocator = new BaseLocator<Context>();
			List<Context> newContexts = new ArrayList<Context>();
	        int i = 0;
	        int total = protoContexts.size();
	        String type = getString(R.string.context_name);
			for (org.dodgybits.shuffle.dto.ShuffleProtos.Context protoContext : protoContexts)
			{
				String contextName = protoContext.getName();
				Context context = existingContexts.get(contextName);
				if (context != null) {
					Log.d(cTag, "Context " + contextName + " already exists - skipping.");
				} else {
					Log.d(cTag, "Context " + contextName + " new - adding.");
					context = Context.buildFromDto(protoContext, getResources());
					
					newContexts.add(context);
				}
				contextLocator.addItem(protoContext.getId(), contextName, context);
				String text = getString(R.string.restore_progress, type, contextName);
				int percent = calculatePercent(progressStart, progressEnd, ++i, total);
				updateProgress(percent, text, false);
			}
			BindingUtils.persistNewContexts(PreferencesRestoreBackupActivity.this, newContexts);
			return contextLocator;
		}
	    
		
		private Locator<Project> addProjects(
				List<org.dodgybits.shuffle.dto.ShuffleProtos.Project> protoProjects,
				Locator<Context> contextLocator,
				int progressStart, int progressEnd) {
			Set<String> projectNameSet = new HashSet<String>();
			for (org.dodgybits.shuffle.dto.ShuffleProtos.Project protoProject : protoProjects)
			{
				projectNameSet.add(protoProject.getName());
			}
			Map<String,Project> existingProjects = BindingUtils.fetchProjectsByName(
					PreferencesRestoreBackupActivity.this, projectNameSet);
			
			// build up the locator and list of new projects
			BaseLocator<Project> projectLocator = new BaseLocator<Project>();
			List<Project> newProjects = new ArrayList<Project>();
	        int i = 0;
	        int total = protoProjects.size();
	        String type = getString(R.string.project_name);
			for (org.dodgybits.shuffle.dto.ShuffleProtos.Project protoProject : protoProjects)
			{
				String projectName = protoProject.getName();
				Project project = existingProjects.get(projectName);
				if (project != null) {
					Log.d(cTag, "Project " + projectName + " already exists - skipping.");
				} else {
					Log.d(cTag, "Project " + projectName + " new - adding.");
					project = Project.buildFromDto(protoProject, contextLocator);
					
					newProjects.add(project);
				}
				projectLocator.addItem(protoProject.getId(), projectName, project);
				String text = getString(R.string.restore_progress, type, projectName);
				int percent = calculatePercent(progressStart, progressEnd, ++i, total);
				updateProgress(percent, text, false);
			}
			BindingUtils.persistNewProjects(PreferencesRestoreBackupActivity.this, newProjects);
			return projectLocator;
		}
		
		private void addTasks(
				List<org.dodgybits.shuffle.dto.ShuffleProtos.Task> protoTasks,
				Locator<Context> contextLocator,
				Locator<Project> projectLocator,
				int progressStart, int progressEnd) {
			// add all tasks back, even if they're duplicates
			
	        String type = getString(R.string.task_name);
			List<Task> newTasks = new ArrayList<Task>();
	        int i = 0;
	        int total = protoTasks.size();
			for (org.dodgybits.shuffle.dto.ShuffleProtos.Task protoTask : protoTasks)
			{
				Task task = Task.buildFromDto(protoTask, contextLocator, projectLocator);
				newTasks.add(task);
				Log.d(cTag, "Adding task " + task.description);
				String text = getString(R.string.restore_progress, type, task.description);
				int percent = calculatePercent(progressStart, progressEnd, ++i, total);
				updateProgress(percent, text, false);
			}
			BindingUtils.persistNewTasks(PreferencesRestoreBackupActivity.this, newTasks);
		}
                
        private int calculatePercent(int start, int end, int current, int total) {
        	return start + (end - start) * current / total;
        }
        
        private void reportError(String message) {
			Log.e(cTag, message);
        	updateProgress(0, message, true);
        }
        
        private void updateProgress(int progressPercent, String details, boolean isError) {
        	Progress progress = new Progress(progressPercent, details, false);
        	publishProgress(progress);
        }
        
		@Override
		public void onProgressUpdate (Progress... progresses) {
			Progress progress = progresses[0];
	        mProgressBar.setProgress(progress.getProgressPercent());
	        mProgressText.setText(progress.getDetails());
	        if (progress.isComplete()) {
	        	setState(State.COMPLETE);
	        } else if (progress.isError()) {
	        	setState(State.ERROR);
	        }
		}
		
        public void onPostExecute() {
            mTask = null;
        }
    	
    }    
        

	

}
