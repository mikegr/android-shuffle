package org.dodgybits.shuffle.android.synchronisation.tracks;

import java.util.LinkedList;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.preference.view.Progress;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


/**
 * This task synchronizes shuffle with a tracks service.
 *
 * @author Morten Nielsen
 */
public class TracksSynchronizer extends AsyncTask<String, Progress, Void> {
    private static final String cTag = "TracksSynchronizer";
    
    private static TracksSynchronizer synchronizer;
    private static LinkedList<SyncProgressListener> progressListeners = new LinkedList<SyncProgressListener>();
    
    private final Context mContext;
    private final LinkedList<Integer> mMessages;
    private final ContextSynchronizer mContextSynchronizer;
    private final ProjectSynchronizer mProjectSynchronizer;
    private final TaskSynchronizer mTaskSynchronizer;


    public static TracksSynchronizer getActiveSynchronizer(ContextWrapper context) throws WebClient.ApiException {
        TracksSynchronizer synchronizer = getSingletonSynchronizer(context);
        while (synchronizer.getStatus() == Status.FINISHED) {
            synchronizer = getSingletonSynchronizer(context);
        }
        return synchronizer;
    }

    private static TracksSynchronizer getSingletonSynchronizer(Context context) throws WebClient.ApiException {
        if (synchronizer == null || synchronizer.getStatus() == Status.FINISHED) {

            synchronizer = new TracksSynchronizer(
                    context, 
                    new WebClient(context, Preferences.getTracksUser(context), Preferences.getTracksPassword(context)), 
                    Preferences.getTracksUrl(context));
        }
        return synchronizer;
    }


    private TracksSynchronizer(Context context, WebClient client, String tracksUrl) {
        mContextSynchronizer = new ContextSynchronizer(this, client, context, 0, tracksUrl);
        mProjectSynchronizer = new ProjectSynchronizer(this, client, context, 33, tracksUrl);
        mTaskSynchronizer = new TaskSynchronizer(this, client, context, 66, tracksUrl);

        mContext = context;
        mMessages = new LinkedList<Integer>();
    }

    @Override
    protected void onProgressUpdate(Progress... progresses) {
        for (SyncProgressListener listener : progressListeners) {
            listener.progressUpdate(progresses[0]);
        }
    }

    public void registerListener(SyncProgressListener listener) {
        if (!progressListeners.contains(listener))
            progressListeners.add(listener);
    }

    public void unRegisterListener(SyncProgressListener
            listener) {
        progressListeners.remove(listener);
    }


    @Override
    protected Void doInBackground(String... strings) {
        try {
            mContextSynchronizer.synchronize();
            mProjectSynchronizer.synchronize();
            mTaskSynchronizer.synchronize();
            publishProgress(Progress.createProgress(100, "Synchronization Complete"));
        } catch (WebClient.ApiException e) {
            Log.w(cTag, "Tracks call failed", e);
            publishProgress(Progress.createErrorProgress(mContext.getString(R.string.web_error_message)));
        } catch (Exception e) {
            Log.w(cTag, "Synch failed", e);
            publishProgress(Progress.createErrorProgress(mContext.getString(R.string.error_message)));
        }
        return null;

    }

    @Override
    protected void onPostExecute(Void result) {
        if (mMessages.size() > 0) {
            for (Integer textId : mMessages) {
                Toast toast = Toast.makeText(mContext.getApplicationContext(), textId
                        , Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        try {
            synchronizer = getSingletonSynchronizer(mContext);
        } catch (WebClient.ApiException ignored) {

        }
    }

    public void reportProgress(Progress progress) {
        publishProgress(progress);
    }

    public void postSyncMessage(int toastMessage) {
        mMessages.add(toastMessage);
    }
}