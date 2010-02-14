package org.dodgybits.shuffle.android.synchronisation.tracks.service;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.preference.view.Progress;
import org.dodgybits.shuffle.android.synchronisation.tracks.SyncProgressListener;
import org.dodgybits.shuffle.android.synchronisation.tracks.TracksSynchronizer;
import org.dodgybits.shuffle.android.synchronisation.tracks.WebClient;
import org.dodgybits.shuffle.android.synchronisation.tracks.activity.SynchronizeActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * This service handles synchronization in the background.
 * 
 * @author Morten Nielsen
 */
public class SynchronizationService extends Service implements SyncProgressListener {
    private static final String cTag = "SynchronizationService";
    
    private NotificationManager mNotificationManager;
    private Handler mHandler;
    private CheckTimer mCheckTimer;
    private PerformSynch mPerformSynch;
    private long interval = 0;
    private TracksSynchronizer synchronizer = null;
    private RemoteViews contentView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);

        contentView = new RemoteViews(getPackageName(), R.layout.synchronize_notification_layout);

        contentView.setProgressBar(R.id.progress_horizontal, 100, 50, true);
        contentView.setImageViewResource(R.id.image, R.drawable.shuffle_icon);

        mHandler = new Handler();
        mCheckTimer = new CheckTimer();
        mPerformSynch = new PerformSynch();
        
        mHandler.post(mCheckTimer);
    }
    
    private class CheckTimer implements Runnable {
        @Override
        public void run() {
            Log.d(cTag, "Checking preferences");
            
            long newInterval = calculateIntervalInMilliseconds(
                    Preferences.getTracksInterval(SynchronizationService.this));
            if (interval != newInterval) {
                interval = newInterval;
                scheduleSynchronization();
            }
            
            mHandler.postDelayed(this, 5L * DateUtils.MINUTE_IN_MILLIS);
        }

        private long calculateIntervalInMilliseconds(int selected) {
            long result = 0L;
            switch (selected) {
                case 1: // 30min
                    result = 30L * DateUtils.MINUTE_IN_MILLIS;
                    break;
                case 2: // 1hr
                    result = DateUtils.HOUR_IN_MILLIS;
                    break;
                case 3:
                    result = 2L * DateUtils.HOUR_IN_MILLIS;
                    break;
                case 4:
                    result = 3L * DateUtils.HOUR_IN_MILLIS;
                    break;
            }
            return result;
        }
    }

    private class PerformSynch implements Runnable
    {
        @Override
        public void run() {
            synchronize();
        }
        
    }

    private void scheduleSynchronization() {
        mHandler.removeCallbacks(mPerformSynch);
        if (interval != 0L) {
            mHandler.post(mPerformSynch);
        }
    }

    private void synchronize() {
        Log.d(cTag, "Starting synch");
        
        try {
            synchronizer = TracksSynchronizer.getActiveSynchronizer(this);
        } catch (WebClient.ApiException ignored) {

        }  
        if (synchronizer != null) {
            synchronizer.registerListener(this);

        }


        if (synchronizer != null && Preferences.validateTracksSettings(this)) {
            if (synchronizer.getStatus() != AsyncTask.Status.RUNNING)
                synchronizer.execute();

        }

        mHandler.postDelayed(mPerformSynch, interval);
    }
    

    private static final int NOTIFICATION_ID = 1;

    private void createNotification() {

        int icon = R.drawable.shuffle_icon;
        CharSequence tickerText = this.getText(R.string.app_name);
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);

        Intent notificationIntent = new Intent(this, SynchronizeActivity.class);

        notification.contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentView = contentView;

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void clearNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mCheckTimer);
        mHandler.removeCallbacks(mPerformSynch);
        if (synchronizer != null) {
            synchronizer.unRegisterListener(this);
        }
    }

    @Override
    public void progressUpdate(Progress progress) {
        if (progress.getProgressPercent() == 100) clearNotification();
        if (progress.getProgressPercent() == 0) createNotification();


    }
}
