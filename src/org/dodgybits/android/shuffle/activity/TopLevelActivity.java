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

package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays a list of the main activities.
 */
public class TopLevelActivity extends ListActivity {
    private static final String cTag = "TopLevelActivity";

    private static final int INBOX = 0;
    private static final int DUE_TASKS = 1;
    private static final int TOP_TASKS = 2;
    private static final int PROJECTS = 3;
    private static final int CONTEXTS = 4;
    
	private static final String[] cProjection = new String[] {"_id"};
    
    private AsyncTask<?, ?, ?> mTask;
    
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.top_level);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuUtils.addPrefsHelpMenuItems(menu);
        return true;
    }

	@Override
	protected void onResume() {
        Log.d(cTag, "onResume+");
		super.onResume();

		Uri[] countUris = new Uri[5];
		countUris[INBOX] = Shuffle.Tasks.cInboxTasksContentURI;
		countUris[DUE_TASKS] = Shuffle.Tasks.cDueTasksContentURI.buildUpon().appendPath(String.valueOf(Shuffle.Tasks.DAY_MODE)).build();
		countUris[TOP_TASKS] = Shuffle.Tasks.cTopTasksContentURI;
		countUris[PROJECTS] = Shuffle.Projects.CONTENT_URI;
		countUris[CONTEXTS] = Shuffle.Contexts.CONTENT_URI;
		
		mTask = new CalculateCountTask().execute(countUris);

        String[] perspectives = getResources().getStringArray(R.array.perspectives).clone();
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
        		this, R.layout.list_item_view, R.id.name, perspectives);
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

    private class CalculateCountTask extends AsyncTask<Uri, CharSequence[], Void> {

    	public Void doInBackground(Uri... params) {
            String[] perspectives = getResources().getStringArray(R.array.perspectives);
			int colour = getResources().getColor(R.drawable.pale_blue);
			ForegroundColorSpan span = new ForegroundColorSpan(colour);
            CharSequence[] labels = new CharSequence[perspectives.length];
            int length = perspectives.length;
            
            for (int i = 0; i < length; i++) {
            	CharSequence label = perspectives[i] + " (.)";
    			SpannableString spannable = new SpannableString(label);
    			spannable.setSpan(span, perspectives[i].length(), 
    					label.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            	labels[i] = spannable;
            }
			publishProgress(labels);
            
            for (int i = 0; i < length; i++) {
            	Uri uri = params[i];
    			Cursor cursor = getContentResolver().query(uri, cProjection, null, null, null);
    			int count = cursor.getCount();
    			cursor.close();
            	CharSequence label = perspectives[i] + " (" + count + ")";
    			SpannableString spannable = new SpannableString(label);
    			spannable.setSpan(span, perspectives[i].length(), 
    					label.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    			labels[i] = spannable;
    			publishProgress(labels);
    		}
            return null;
        }

		@Override
		public void onProgressUpdate (CharSequence[]... progress) {
			CharSequence[] labels = progress[0];
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
            		TopLevelActivity.this, R.layout.list_item_view, R.id.name, labels) {
            	
            	@Override
                public View getView(int position, View convertView, ViewGroup parent) {
                	View view = super.getView(position, convertView, parent);
                	TextView nameView = (TextView) view.findViewById(R.id.name);
                	// don't use toString in order to preserve colour change
                	nameView.setText(getItem(position));
                	return view;
                }
            };
            
            int position = getSelectedItemPosition();
            setListAdapter(adapter);
            setSelection(position);
		}
		
        public void onPostExecute() {
            mTask = null;
        }
    	
    }
}
