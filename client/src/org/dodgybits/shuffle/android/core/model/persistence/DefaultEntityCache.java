package org.dodgybits.shuffle.android.core.model.persistence;

import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.util.ItemCache;
import org.dodgybits.shuffle.android.core.util.ItemCache.ValueBuilder;

import android.util.Log;

import com.google.inject.Inject;

public class DefaultEntityCache<E extends Entity> implements EntityCache<E> {
    private static final String cTag = "DefaultEntityCache";

    private EntityPersister<E> mPersister;
    private Builder mBuilder;
    private ItemCache<Id, E> mCache;
    
    @Inject
    public DefaultEntityCache(EntityPersister<E> persister) {
        Log.d(cTag, "Created entity cache");
        
        mPersister = persister;
        mBuilder = new Builder();
        mCache = new ItemCache<Id, E>(mBuilder);
    }
    
    public E findById(Id localId) {
        E entity = null;
        if (localId.isInitialised()) {
            entity = mCache.get(localId); 
        }
        return entity;
    }
    
    private class Builder implements ValueBuilder<Id, E> {
    
        @Override
        public E build(Id key) {
            return mPersister.findById(key);
        }
    }
    
}
