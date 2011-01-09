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

package org.dodgybits.shuffle.android.list.activity.task;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.Flag;
import org.dodgybits.shuffle.android.core.view.AlertUtils;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.config.AbstractTaskListConfig;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.list.config.StandardTaskQueries;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

public class TrashActivity extends AbstractTaskListActivity {
    private static final String cTag = "TrashActivity";

    @Inject private TaskPersister mTaskPersister;
    @Inject private ProjectPersister mProjectPersister;
    @Inject private ContextPersister mContextPersister;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        mOtherButton.setText(R.string.permanently_delete_button_title);
        Drawable cleanIcon = getResources().getDrawable(R.drawable.edit_clear);
        cleanIcon.setBounds(0, 0, 24, 24);
        mOtherButton.setCompoundDrawables(cleanIcon, null, null, null);
        mOtherButton.setVisibility(View.VISIBLE);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuUtils.addPermanentlyDeleteMenuItem(menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MenuUtils.PERMANENTLY_DELETE_ID:
            deletePermanently();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        // ... add put back command.
        MenuUtils.addPutBackMenuItem(menu);
    }
    

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(cTag, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
            case MenuUtils.PUT_BACK_ID:
                putBack(info.position);
                return true;
        }
        return super.onContextItemSelected(item);
    }   

    @Override
    protected ListConfig<Task> createListConfig()
    {
        ListPreferenceSettings settings = new ListPreferenceSettings("trash").setDefaultDeleted(Flag.yes);
        return new AbstractTaskListConfig(
                StandardTaskQueries.getQuery(StandardTaskQueries.cTrash),
                mTaskPersister, settings) {

            public int getCurrentViewMenuId() {
                return MenuUtils.TRASH_ID;
            }
            
            public String createTitle(ContextWrapper context)
            {
                return context.getString(R.string.title_trash);
            }
            
        };
    }

    @Override
    protected void onOtherButtonClicked() {
        deletePermanently();
    }
    
    private void deletePermanently() {
        OnClickListener buttonListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON1) {
                    int count = mTaskPersister.emptyTrash() +
                        mContextPersister.emptyTrash() + 
                        mProjectPersister.emptyTrash();
                    
                    Log.d(cTag, "Permanently deleted " + count + " entities.");
                    CharSequence message = getString(R.string.emptyTrashToast, new Object[] {count});
                    Toast.makeText(TrashActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(cTag, "Hit Cancel button. Do nothing.");
                }
            }
        };
        AlertUtils.showEmptyTrashWarning(this, buttonListener);            
    }
    
    protected final void putBack(int position) {
        if (position >= 0 && position < getItemCount())
        {
            Id id = Id.create(getListAdapter().getItemId(position));
            getTaskPersister().putBack(id);
        }
    }
    
}
