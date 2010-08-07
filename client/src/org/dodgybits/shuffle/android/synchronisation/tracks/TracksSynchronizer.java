package org.dodgybits.shuffle.android.synchronisation.tracks;

import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryTracksSyncCompletedEvent;
import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryTracksSyncError;
import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryTracksSyncStartedEvent;

import java.util.LinkedList;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.preference.view.Progress;

import roboguice.inject.ContentResolverProvider;
import android.content.ContentResolver;
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
    private Analytics mAnalytics;
    
// TODO inject sync classes (BIG job)
    
    public static TracksSynchronizer getActiveSynchronizer(
            ContextWrapper context, Analytics analytics) throws ApiException {
        TracksSynchronizer synchronizer = getSingletonSynchronizer(context, analytics);
        while (synchronizer.getStatus() == Status.FINISHED) {
            synchronizer = getSingletonSynchronizer(context, analytics);
        }
        return synchronizer;
    }

    private static TracksSynchronizer getSingletonSynchronizer(Context context, Analytics analytics) throws ApiException {
        if (synchronizer == null || synchronizer.getStatus() == Status.FINISHED) {

            synchronizer = new TracksSynchronizer(
                    context, analytics,
                    new WebClient(context, Preferences.getTracksUser(context), 
                            Preferences.getTracksPassword(context)), 
                    Preferences.getTracksUrl(context)
                    );
        }
        return synchronizer;
    }

    private TracksSynchronizer(
            Context context,
            Analytics analytics,
            WebClient client, 
            String tracksUrl) {
        mContext = context;
        
        //TODO inject this
        ContentResolverProvider provider = new ContentResolverProvider() {
            @Override
            public ContentResolver get() {
                return mContext.getContentResolver();
            }
        };
        
        mAnalytics = analytics;
        
        EntityPersister<Task> taskPersister = new TaskPersister(provider, analytics);
        EntityPersister<org.dodgybits.shuffle.android.core.model.Context> contextPersister = new ContextPersister(provider, analytics);
        EntityPersister<Project> projectPersister = new ProjectPersister(provider, analytics);
        
        mContextSynchronizer = new ContextSynchronizer(contextPersister, this, client, context, mAnalytics, 0, tracksUrl);
        mProjectSynchronizer = new ProjectSynchronizer(projectPersister, this, client, context, mAnalytics, 33, tracksUrl);
        mTaskSynchronizer = new TaskSynchronizer(taskPersister, this, client, context, mAnalytics, 66, tracksUrl);

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
            mAnalytics.onEvent(cFlurryTracksSyncStartedEvent);
            mContextSynchronizer.synchronize();
            mProjectSynchronizer.synchronize();
            mTaskSynchronizer.synchronize();
            publishProgress(Progress.createProgress(100, "Synchronization Complete"));
            mAnalytics.onEvent(cFlurryTracksSyncCompletedEvent);
        } catch (ApiException e) {
            Log.w(cTag, "Tracks call failed", e);
            publishProgress(Progress.createErrorProgress(mContext.getString(R.string.web_error_message)));
            mAnalytics.onError(cFlurryTracksSyncError, e.getMessage(), getClass().getName());
        } catch (Exception e) {
            Log.w(cTag, "Synch failed", e);
            publishProgress(Progress.createErrorProgress(mContext.getString(R.string.error_message)));
            mAnalytics.onError(cFlurryTracksSyncError, e.getMessage(), getClass().getName());
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
            synchronizer = getSingletonSynchronizer(mContext, mAnalytics);
        } catch (ApiException ignored) {

        }
    }

    public void reportProgress(Progress progress) {
        publishProgress(progress);
    }

    public void postSyncMessage(int toastMessage) {
        mMessages.add(toastMessage);
    }
}