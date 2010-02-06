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

package org.dodgybits.shuffle.android.preference.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class PreferencesActivity extends PreferenceActivity {
    private static final String cTag = "PreferencesActivity";

    // We can't use the constants from the provider since it's not a public portion of the SDK.
    // Also need to gracefully handle case when calendars are not available (e.g. emulators)
    
    private static final Uri CALENDAR_CONTENT_URI =
        Uri.parse("content://calendar/calendars"); // Calendars.CONTENT_URI
    
    private static final String[] CALENDARS_PROJECTION = new String[] {
        "_id", // Calendars._ID,
        "displayName" //Calendars.DISPLAY_NAME
    };
    
    // only show calendars that the user can modify and that are synced
    private static final String CALENDARS_WHERE = 
        "access_level>=500 AND sync_events=1";
//        Calendars.ACCESS_LEVEL + ">=" +
//        Calendars.CONTRIBUTOR_ACCESS + " AND " + Calendars.SYNC_EVENTS + "=1";
    
    private static final String CALENDARS_SORT = "displayName ASC";
    
    private AsyncQueryHandler mQueryHandler;
    private ListPreference mPreference;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        setCalendarPreferenceEntries();
    }

    private void setCalendarPreferenceEntries() {
        mPreference = (ListPreference)findPreference(Preferences.CALENDAR_ID_KEY);
        // disable the pref until we load the values (if at all)
        mPreference.setEnabled(false);
        
        // Start a query in the background to read the list of calendars
        mQueryHandler = new QueryHandler(getContentResolver());
        mQueryHandler.startQuery(0, null, CALENDAR_CONTENT_URI, CALENDARS_PROJECTION,
                CALENDARS_WHERE, null /* selection args */, CALENDARS_SORT);
    }
    
    private class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null) {
                int selectedIndex = -1;
                final String currentValue = String.valueOf(
                        Preferences.getCalendarId(PreferencesActivity.this));
                
                final int numCalendars = cursor.getCount();
                final String[] values = new String[numCalendars];
                final String[] names = new String[numCalendars];
                for(int i = 0; i < numCalendars; i++) {
                    cursor.moveToPosition(i);
                    values[i] = cursor.getString(0);
                    names[i] = cursor.getString(1);
                    
                    if (currentValue.equals(values[i])) {
                        selectedIndex = i;
                    }
                }
                cursor.close();
                
                mPreference.setEntryValues(values);
                mPreference.setEntries(names);
                if (selectedIndex >= 0) {
                    mPreference.setValueIndex(selectedIndex);
                }
                mPreference.setEnabled(true);                
            } else {
                Log.e(cTag, "Failed to fetch calendars - setting disabled.");
            }
        }
    }
}
