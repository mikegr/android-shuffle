package org.dodgybits.android.shuffle.service;

import java.util.Timer;
import java.util.TimerTask;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.util.ModelUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
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
    	int deletePeriod = Preferences.getDeleteCompletedPeriod(mContext);
    	long lastClean = Preferences.getLastDeleteCompleted(mContext);
    	long timeSinceLastClean = System.currentTimeMillis() - lastClean;
    	long period;
    	switch (deletePeriod) {
    	case Preferences.HOURLY:
    		period = 1000L * 60L * 60L;
    		break;
    	case Preferences.DAILY:
    		period = 1000L * 60L * 60L * 24L;
    		break;
    	case Preferences.WEEKLY:
    		period = 1000L * 60L * 60L * 24L * 7L;
    		break;
    	case Preferences.NEVER:
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

