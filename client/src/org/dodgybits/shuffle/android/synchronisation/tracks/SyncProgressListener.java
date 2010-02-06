package org.dodgybits.shuffle.android.synchronisation.tracks;

import org.dodgybits.shuffle.android.preference.view.Progress;

/**
 * Items that can receive updates about synchronization progress
 * 
 * @author Morten Nielsen
 */
public interface SyncProgressListener {
    void progressUpdate(Progress progress);
}
