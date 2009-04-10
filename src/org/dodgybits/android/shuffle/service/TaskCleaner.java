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

package org.dodgybits.android.shuffle.service;

import java.util.Timer;
import java.util.TimerTask;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.util.ModelUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Periodically delete completed tasks and remove tasks with projects from Inbox. 
 */
public class TaskCleaner
{
	private static final String cTag = "TaskCleaner";
	private static TaskCleaner instance;
	private Context mContext;
    private Timer mTimer;

    public static TaskCleaner getInstance(Context context) {
    	if (instance == null) {
    		instance = new TaskCleaner(context);
    	}
    	return instance;
    }
    
    private TaskCleaner(Context context) {
    	mContext = context;	
    }
    
    public void schedule() {
    	String deletePeriodStr = Preferences.getDeleteCompletedPeriod(mContext);
    	Preferences.DeleteCompletedPeriod deletePeriod = Preferences.DeleteCompletedPeriod.valueOf(deletePeriodStr);
    	long lastClean = Preferences.getLastDeleteCompleted(mContext);
    	long timeSinceLastClean = System.currentTimeMillis() - lastClean;
    	long period;
    	switch (deletePeriod) {
    	case hourly:
    		period = 1000L * 60L * 60L;
    		break;
    	case daily:
    		period = 1000L * 60L * 60L * 24L;
    		break;
    	case weekly:
    		period = 1000L * 60L * 60L * 24L * 7L;
    		break;
    	case never:
    		// nothing to do
    		return;
    	default:
    		Log.e(cTag, "Unknown clean frequency: " + deletePeriod);
    		return;
    	}
    	if (mTimer != null) {
    		mTimer.cancel();
    	}
    	mTimer = new Timer();
    	long nextClean = Math.max(0L, period - timeSinceLastClean);
    	Log.d(cTag, "Next clean in " + nextClean + "ms"); 
    	mTimer.scheduleAtFixedRate(
    			new TimerTask() {
    				public void run() {doClean();}
    			},
    			nextClean, period);
    }
    
    protected void onDestroy()
    {
    	mTimer.cancel();
    	mTimer.purge();
    	mTimer = null;
    }
    
    public void doClean() {
    	int deletedRows = ModelUtils.deleteCompletedTasks(mContext);
		String message = mContext.getString(R.string.clean_task_message, new Object[] {deletedRows});
		Log.d(cTag, message);

		// update last clean timestamp
		SharedPreferences.Editor editor = Preferences.getEditor(mContext);
		editor.putLong(Preferences.LAST_DELETE_COMPLETED_KEY, System.currentTimeMillis());
		editor.commit();
	}
    
}

