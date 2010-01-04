package org.dodgybits.android.shuffle.server.tracks;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import org.dodgybits.android.shuffle.service.Progress;

/**
 * Created by IntelliJ IDEA.
 * User: Morten
 * Date: 2009-dec-30
 * Time: 08:27:04
 * This task synchronizes shuffle with a tracks service
 */
public class TracksSynchronizer extends AsyncTask<String, Progress, Void> {
    private final TextView infoText;
    private final ProgressBar progress;
    private final ContextSynchronizer contextSynchronizer;
    private ProjectSynchronizer projectSynchronizer;
    private Synchronizer taskSynchronizer;

    public TracksSynchronizer(ContentResolver contentResolver, Resources resources, WebClient client, Activity activity, TextView infoText, ProgressBar progress, String tracksUrl) {
        this.contextSynchronizer = new ContextSynchronizer(contentResolver, resources, client, activity, this, tracksUrl, 0);
        this.projectSynchronizer = new ProjectSynchronizer(contentResolver, resources, client, activity, this, tracksUrl, 33);
        this.taskSynchronizer = new TaskSynchronizer(contentResolver, resources, client, activity, this, tracksUrl, 66);
        this.infoText = infoText;
        this.progress = progress;

    }

    @Override
    protected void onProgressUpdate(Progress... progresses) {
         infoText.setText(progresses[0].getDetails());
         progress.setProgress(progresses[0].getProgressPercent());
    }



    @Override
    protected Void doInBackground(String... strings) {
        contextSynchronizer.synchronize();
        projectSynchronizer.synchronize();
        taskSynchronizer.synchronize();
         publishProgress( Progress.createProgress(100, "Synchronization Complete"));
        return null;
    }


    public void reportProgress(Progress progress) {
        publishProgress(progress);
    }
}
