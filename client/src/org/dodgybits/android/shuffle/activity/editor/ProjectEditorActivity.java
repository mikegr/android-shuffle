/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.android.shuffle.activity.editor;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.State;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ProjectEditorActivity extends AbstractEditorActivity<Project> {

    private static final String cTag = "ProjectEditorActivity";
   
    private EditText mNameWidget;
    private Spinner mDefaultContextSpinner;
    
    private String[] mContextNames;
    private long[] mContextIds;

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        loadCursor();
        findViewsAndAddListeners();
        
        if (mState == State.STATE_EDIT) {
            if (mCursor != null) {
                // Make sure we are at the one and only row in the cursor.
                mCursor.moveToFirst();
                setTitle(R.string.title_edit_project);
                mOriginalItem = BindingUtils.readProject(mCursor);
              	updateUIFromItem(mOriginalItem);
            } else {
                setTitle(getText(R.string.error_title));
                mNameWidget.setText(getText(R.string.error_message));
            }
        } else if (mState == State.STATE_INSERT) {
            setTitle(R.string.title_new_project);
            Bundle extras = getIntent().getExtras();
            updateUIFromExtras(extras);
        }
    }
    
    @Override
    protected boolean isValid() {
        String name = mNameWidget.getText().toString();
        return !TextUtils.isEmpty(name);
    }
    
    @Override
    protected void doDeleteAction() {
    	super.doDeleteAction();
        mNameWidget.setText("");
    }
    
    @Override
    protected Project createItemFromUI() {
        String name = mNameWidget.getText().toString();
    	Long defaultContextId = null;
    	int selectedItemPosition = mDefaultContextSpinner.getSelectedItemPosition();
		if (selectedItemPosition > 0) {
    		defaultContextId = mContextIds[selectedItemPosition];
    	}
    	boolean archived = false;
    	return new Project(name, defaultContextId, archived);
    }
    
    @Override
    protected void updateUIFromExtras(Bundle extras) {
    	// do nothing for now
    }
    
    @Override
    protected void updateUIFromItem(Project project) {
        mNameWidget.setTextKeepState(project.name);
        Long defaultContextId = project.defaultContextId;
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
    }
    
    /**
     * @return id of layout for this view
     */
    @Override
    protected int getContentViewResId() {
    	return R.layout.project_editor;
    }

    @Override
    protected Project restoreItem(Bundle icicle) {
    	return BindingUtils.restoreProject(icicle);
    }
    
    @Override
    protected void saveItem(Bundle outState, Project item) {
    	BindingUtils.saveProject(outState, item);
    }

    @Override
    protected void writeItem(ContentValues values, Project project) {
    	BindingUtils.writeProject(values, project);
    }
    
    @Override
    protected Intent getInsertIntent() {
    	return new Intent(Intent.ACTION_INSERT, Shuffle.Projects.CONTENT_URI);
    }
    
    @Override
    protected CharSequence getItemName() {
    	return getString(R.string.project_name);
    }
    
    private void loadCursor() {
    	if (mUri != null && mState == State.STATE_EDIT)
    	{
            mCursor = managedQuery(mUri, Shuffle.Projects.cFullProjection, null, null, null);
	        if (mCursor == null || mCursor.getCount() == 0) {
	            // The cursor is empty. This can happen if the event was deleted.
	            finish();
	            return;
	        }
    	}
    }
    
    private void findViewsAndAddListeners() {
        // The text view for our project description, identified by its ID in the XML file.
        mNameWidget = (EditText) findViewById(R.id.name);
        mDefaultContextSpinner = (Spinner) findViewById(R.id.default_context);

        Cursor contactCursor = getContentResolver().query(
        		Shuffle.Contexts.CONTENT_URI, 
        		new String[] {Shuffle.Contexts._ID, Shuffle.Contexts.NAME}, null, null, null);
        int size = contactCursor.getCount() + 1;
        mContextIds = new long[size];
        mContextIds[0] = 0;
        mContextNames = new String[size];
        mContextNames[0] = getText(R.string.none_empty).toString();
        for (int i = 1; i < size; i++) {
        	contactCursor.moveToNext();
        	mContextIds[i] = contactCursor.getLong(0);
        	mContextNames[i] = contactCursor.getString(1);
        }
        contactCursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        		this, android.R.layout.simple_list_item_1, mContextNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDefaultContextSpinner.setAdapter(adapter);
    }        

}
