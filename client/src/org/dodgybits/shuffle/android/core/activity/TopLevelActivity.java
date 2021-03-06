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

package org.dodgybits.shuffle.android.core.activity;

import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledListActivity;
import org.dodgybits.shuffle.android.core.model.persistence.selector.EntitySelector;
import org.dodgybits.shuffle.android.core.util.Constants;
import org.dodgybits.shuffle.android.core.view.IconArrayAdapter;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.annotation.*;
import org.dodgybits.shuffle.android.list.config.DueActionsListConfig;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.list.config.TaskListConfig;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AndroidException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays a list of the main activities.
 */
public class TopLevelActivity extends FlurryEnabledListActivity {
    private static final String cTag = "TopLevelActivity";

    private static final int INBOX = 0;
    private static final int DUE_TASKS = 1;
    private static final int TOP_TASKS = 2;
    private static final int PROJECTS = 3;
    private static final int CONTEXTS = 4;
    private static final int TICKLER = 5;

    private static final int ITEM_COUNT = 6;
    
    private static final String[] cProjection = new String[]{"_id"};

    private final static int WHATS_NEW_DIALOG = 0;
    
    private Integer[] mIconIds = new Integer[ITEM_COUNT];
    private AsyncTask<?, ?, ?> mTask;

    @Inject @Inbox private TaskListConfig mInboxConfig;
    @Inject @DueTasks private DueActionsListConfig mDueTasksConfig;
    @Inject @TopTasks private TaskListConfig mTopTasksConfig;
    @Inject @Tickler private TaskListConfig mTicklerConfig;
    @Inject @Projects private ListConfig mProjectsConfig;
    @Inject @Contexts private ListConfig mContextsConfig;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.top_level);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        addVersionToTitle();
        checkLastVersion();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.findItem(MenuUtils.SYNC_ID);
        if (item != null) {
            item.setVisible(Preferences.validateTracksSettings(this));
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuUtils.addPrefsHelpMenuItems(this, menu);
        MenuUtils.addSearchMenuItem(this, menu);
        MenuUtils.addSyncMenuItem(this, menu);
        
        return true;
    }

    @Override
    protected void onResume() {
        Log.d(cTag, "onResume+");
        super.onResume();

        CursorGenerator[] generators = new CursorGenerator[ITEM_COUNT];
        generators[INBOX] = new EntityCursorGenerator(mInboxConfig);
        generators[DUE_TASKS] = new EntityCursorGenerator(mDueTasksConfig);
        generators[TOP_TASKS] = new EntityCursorGenerator(mTopTasksConfig);
        generators[PROJECTS] = new EntityCursorGenerator(mProjectsConfig);
        generators[CONTEXTS] = new EntityCursorGenerator(mContextsConfig);
        generators[TICKLER] = new EntityCursorGenerator(mTicklerConfig);

        mIconIds[INBOX] = R.drawable.inbox;
        mIconIds[DUE_TASKS] = R.drawable.due_actions;
        mIconIds[TOP_TASKS] = R.drawable.next_actions;
        mIconIds[PROJECTS] = R.drawable.projects;
        mIconIds[CONTEXTS] = R.drawable.contexts;
        mIconIds[TICKLER] = R.drawable.ic_media_pause;

        mTask = new CalculateCountTask().execute(generators);

        String[] perspectives = getResources().getStringArray(R.array.perspectives).clone();
        ArrayAdapter<CharSequence> adapter = new IconArrayAdapter(
                this, R.layout.list_item_view, R.id.name, perspectives, mIconIds);
        setListAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (MenuUtils.checkCommonItemsSelected(item, this, -1)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MenuUtils.checkCommonItemsSelected(position + MenuUtils.INBOX_ID, this, -1, false);
    }
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        if (id == WHATS_NEW_DIALOG) {
            dialog = new AlertDialog.Builder(this)
            .setTitle(R.string.whats_new_dialog_title)
            .setPositiveButton(R.string.ok_button_title, null)
            .setMessage(R.string.whats_new_dialog_message)
            .create();
        } else {
            dialog = super.onCreateDialog(id);
        }
        return dialog;
    }
    
    private interface CursorGenerator {
        Cursor generate();
    }
    
    private class EntityCursorGenerator implements CursorGenerator {
        private EntitySelector mEntitySelector;

        public EntityCursorGenerator(ListConfig config) {
            mEntitySelector = config.getEntitySelector();
            mEntitySelector = mEntitySelector.builderFrom()
                .applyListPreferences(TopLevelActivity.this, config.getListPreferenceSettings())
                .build();
        }

        public Cursor generate() {
            return getContentResolver().query(
                    mEntitySelector.getContentUri(),
                    cProjection, 
                    mEntitySelector.getSelection(TopLevelActivity.this),
                    mEntitySelector.getSelectionArgs(),
                    mEntitySelector.getSortOrder());
        }
    }
     
    private class UriCursorGenerator implements CursorGenerator {
        private Uri mUri;
        
        public UriCursorGenerator(Uri uri) {
            mUri = uri;
        }
        
        public Cursor generate() {
            return getContentResolver().query(
                    mUri, cProjection, null, null, null);
        }
    }
    

    private class CalculateCountTask extends AsyncTask<CursorGenerator, CharSequence[], Void> {

        public Void doInBackground(CursorGenerator... params) {
            String[] perspectives = getResources().getStringArray(R.array.perspectives);
            int colour = getResources().getColor(R.drawable.pale_blue);
            ForegroundColorSpan span = new ForegroundColorSpan(colour);
            CharSequence[] labels = new CharSequence[perspectives.length];
            int length = perspectives.length;

            for (int i = 0; i < length; i++) {
                labels[i] = "  " + perspectives[i];
            }

            int[] cachedCounts = Preferences.getTopLevelCounts(TopLevelActivity.this);
            if (cachedCounts != null && cachedCounts.length == length) {
                for (int i = 0; i < length; i++) {
                    CharSequence label = labels[i] + "  (" + cachedCounts[i] + ")";
                    SpannableString spannable = new SpannableString(label);
                    spannable.setSpan(span, labels[i].length(),
                            label.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    labels[i] = spannable;
                }
            }
            publishProgress(labels);

            String cachedCountStr = "";
            for (int i = 0; i < length; i++) {
                CursorGenerator generator = params[i];
                Cursor cursor = generator.generate();
                int count = cursor.getCount();
                cursor.close();
                CharSequence label = "  " + perspectives[i] + "  (" + count + ")";
                SpannableString spannable = new SpannableString(label);
                spannable.setSpan(span, perspectives[i].length() + 2,
                        label.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                labels[i] = spannable;
                publishProgress(labels);

                cachedCountStr += count;
                if (i < length - 1) {
                    cachedCountStr += ",";
                }
            }

            // updated cached counts
            SharedPreferences.Editor editor = Preferences.getEditor(TopLevelActivity.this);
            editor.putString(Preferences.TOP_LEVEL_COUNTS_KEY, cachedCountStr);
            editor.commit();

            return null;
        }

        @Override
        public void onProgressUpdate(CharSequence[]... progress) {
            CharSequence[] labels = progress[0];
            ArrayAdapter<CharSequence> adapter = new IconArrayAdapter(
                    TopLevelActivity.this, R.layout.list_item_view, R.id.name, labels, mIconIds);
            int position = getSelectedItemPosition();
            setListAdapter(adapter);
            setSelection(position);
        }

        @SuppressWarnings("unused")
        public void onPostExecute() {
            mTask = null;
        }

    }


    private void addVersionToTitle() {
        String title = getTitle().toString();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            title += " " + info.versionName;
            setTitle(title);
        } catch (AndroidException e) {
            Log.e(cTag, "Failed to add version to title: " + e.getMessage());
        }
    }
    
    private void checkLastVersion() {
        final int lastVersion = Preferences.getLastVersion(this);
        if (Math.abs(lastVersion) < Math.abs(Constants.cVersion)) {
            // This is a new install or an upgrade.
            
            // show what's new message
            SharedPreferences.Editor editor = Preferences.getEditor(this);
            editor.putInt(Preferences.LAST_VERSION, Constants.cVersion);
            editor.commit();
            
            showDialog(WHATS_NEW_DIALOG);
        }        
    }

}
