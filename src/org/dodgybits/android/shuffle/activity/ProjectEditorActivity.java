package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.State;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ProjectEditorActivity extends AbstractEditorActivity<Project> {

    private static final String cTag = "ProjectEditorActivity";
   
    private EditText mNameWidget;
    private Spinner mDefaultContextSpinner;
    
    private String[] mContextNames;
    private int[] mContextIds;

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        // The text view for our project description, identified by its ID in the XML file.
        mNameWidget = (EditText) findViewById(R.id.name);
        mDefaultContextSpinner = (Spinner) findViewById(R.id.default_context);

        Cursor contactCursor = getContentResolver().query(Shuffle.Contexts.CONTENT_URI, new String[] {Shuffle.Contexts._ID, Shuffle.Contexts.NAME}, null, null, null);
        int size = contactCursor.getCount() + 1;
        mContextIds = new int[size];
        mContextIds[0] = 0;
        mContextNames = new String[size];
        mContextNames[0] = "None";
        for (int i = 1; i < size; i++) {
        	contactCursor.moveToNext();
        	mContextIds[i] = contactCursor.getInt(0);
        	mContextNames[i] = contactCursor.getString(1);
        }
        contactCursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mContextNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDefaultContextSpinner.setAdapter(adapter);

        // Get the project!
        mCursor = managedQuery(mURI, Shuffle.Projects.cFullProjection, null, null, null);
    }
    
    /**
     * @return id of layout for this view
     */
    protected int getContentViewResId() {
    	return R.layout.project_editor;
    }

    protected Project restoreItem(Bundle icicle) {
    	return BindingUtils.restoreProject(icicle);
    }
    
    protected void saveItem(Bundle outState, Project item) {
    	BindingUtils.saveProject(outState, item);
    }
    
    @Override
    protected void onResume() {
        Log.d(cTag, "onResume+");
        super.onResume();

        // If we didn't have any trouble retrieving the data, it is now
        // time to get at the stuff.
        if (mCursor != null) {
            // Make sure we are at the one and only row in the cursor.
            mCursor.moveToFirst();

            // Modify our overall title depending on the mode we are running in.
            if (mState == State.STATE_EDIT) {
                setTitle(R.string.title_edit_project);
            } else if (mState == State.STATE_INSERT) {
                setTitle(R.string.title_new_project);
            }

            // This is a little tricky: we may be resumed after previously being
            // paused/stopped.  We want to put the new text in the text view,
            // but leave the user where they were (retain the cursor position
            // etc).  This version of setText does that for us.
            Project project = BindingUtils.readProject(mCursor);
            mNameWidget.setTextKeepState(project.name);
            Integer defaultContextId = project.defaultContextId;
            if (defaultContextId == null) {
            	mDefaultContextSpinner.setSelection(0);
            } else {
            	for (int i = 1; i < mContextIds.length; i++) {
            		if (mContextIds[i] == defaultContextId) {
            			mDefaultContextSpinner.setSelection(i);
            			break;
            		}
            	}
            }

            // If we hadn't previously retrieved the original project, do so
            // now.  This allows the user to revert their changes.
            if (mOriginalItem == null) {
            	mOriginalItem = project;
            }
        } else {
            setTitle(getText(R.string.error_title));
            mNameWidget.setText(getText(R.string.error_message));
        }
        
        // select the description
        mNameWidget.selectAll();
    }

    @Override
    protected void onPause() {
        Log.d(cTag, "onPause+");
        super.onPause();

        // The user is going somewhere else, so make sure their current
        // changes are safely saved away in the provider.  We don't need
        // to do this if only viewing.
        if (mCursor != null) {
            String description = mNameWidget.getText().toString();
            int length = description.length();

            // If this activity is finished, and there is no text, then we
            // do something a little special: simply delete the project entry.
            // Note that we do this both for editing and inserting...  it
            // would be reasonable to only do it when inserting.
            if (isFinishing() && (length == 0)) {
                setResult(RESULT_CANCELED);
                deleteItem();

            // Get out updates into the provider.
            } else {
            	String name = mNameWidget.getText().toString();
            	Integer defaultContextId = null;
            	int selectedItemPosition = mDefaultContextSpinner.getSelectedItemPosition();
				if (selectedItemPosition > 0) {
            		defaultContextId = mContextIds[selectedItemPosition];
            	}
            	boolean archived = false;
            	Project project  = new Project(name, defaultContextId, archived);
            	writeItem(mCursor, project);

                // Commit all of our changes to persistent storage.  Note the
                // use of managedCommitUpdates() instead of
                // mCursor.commitUpdates() -- this lets Activity take care of
                // requerying the new data if needed.
                managedCommitUpdates(mCursor);
            }
        }
    }
    
    protected void writeItem(Cursor cursor, Project project) {
    	BindingUtils.writeProject(cursor, project);
    }

    /**
     * Take care of deleting a project.  Simply deletes the entry.
     */
    protected void deleteItem() {
    	super.deleteItem();
        mNameWidget.setText("");
    }
}
