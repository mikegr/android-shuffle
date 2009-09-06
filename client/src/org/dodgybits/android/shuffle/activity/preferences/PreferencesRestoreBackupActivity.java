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
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.shuffle.dto.ShuffleProtos.Catalogue;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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

			Locator<Context> contextLocator = addContexts(catalogue.getContextList());
			Locator<Project> projectLocator = addProjects(catalogue.getProjectList(), contextLocator);
			addTasks(catalogue.getTaskList(), contextLocator, projectLocator);
			
			String text = getResources().getString(R.string.toast_backup_restored, filename);
	        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	        finish();
		} catch (Exception e) {
			// TODO show alert
			Log.e(cTag, "Failed to restore backup " + e.getMessage());
			return;
		}
    }

	private Locator<Context> addContexts(
				List<org.dodgybits.shuffle.dto.ShuffleProtos.Context> protoContexts) {
		Set<String> contextNameSet = new HashSet<String>();
		for (org.dodgybits.shuffle.dto.ShuffleProtos.Context protoContext : protoContexts)
		{
			contextNameSet.add(protoContext.getName());
		}
		Map<String,Context> existingContexts = BindingUtils.fetchContextsByName(this, contextNameSet);
		
		// build up the locator and list of new contacts
		BaseLocator<Context> contextLocator = new BaseLocator<Context>();
		List<Context> newContexts = new ArrayList<Context>();
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
		}
		BindingUtils.persistNewContexts(this, newContexts);
		return contextLocator;
	}
    
	
	private Locator<Project> addProjects(
			List<org.dodgybits.shuffle.dto.ShuffleProtos.Project> protoProjects,
			Locator<Context> contextLocator) {
		Set<String> projectNameSet = new HashSet<String>();
		for (org.dodgybits.shuffle.dto.ShuffleProtos.Project protoProject : protoProjects)
		{
			projectNameSet.add(protoProject.getName());
		}
		Map<String,Project> existingProjects = BindingUtils.fetchProjectsByName(this, projectNameSet);
		
		// build up the locator and list of new projects
		BaseLocator<Project> projectLocator = new BaseLocator<Project>();
		List<Project> newProjects = new ArrayList<Project>();
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
		}
		BindingUtils.persistNewProjects(this, newProjects);
		return projectLocator;
	}
	
	private void addTasks(
			List<org.dodgybits.shuffle.dto.ShuffleProtos.Task> protoTasks,
			Locator<Context> contextLocator,
			Locator<Project> projectLocator) {
		// add all tasks back, even if they're duplicates
		
		List<Task> newTasks = new ArrayList<Task>();
		for (org.dodgybits.shuffle.dto.ShuffleProtos.Task protoTask : protoTasks)
		{
			Task task = Task.buildFromDto(protoTask, contextLocator, projectLocator);
			newTasks.add(task);
			Log.d(cTag, "Adding task " + task.description);
		}
		BindingUtils.persistNewTasks(this, newTasks);
	}
	

}
