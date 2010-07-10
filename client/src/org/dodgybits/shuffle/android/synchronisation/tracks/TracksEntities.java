package org.dodgybits.shuffle.android.synchronisation.tracks;

import java.util.Map;

import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.synchronisation.tracks.model.TracksEntity;

public class TracksEntities<E extends TracksEntity> {
    private Map<Id, E> mEntities;
    private boolean mErrorFree;
    
    public TracksEntities(Map<Id, E> entities, boolean errorFree) {
        mEntities = entities;
        mErrorFree = errorFree;
    }
    
    public Map<Id, E> getEntities() {
        return mEntities;
    }
    
    public boolean isErrorFree() {
        return mErrorFree;
    }
    
}