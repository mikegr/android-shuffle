package org.dodgybits.android.shuffle.server.tracks;

import org.dodgybits.android.shuffle.service.Progress;

/**
 * Items that can receive updates about synchronization progress
 * 
 * @author Morten Nielsen
 */
public interface SyncProgressListener {
    void progressUpdate(Progress progress);
}
