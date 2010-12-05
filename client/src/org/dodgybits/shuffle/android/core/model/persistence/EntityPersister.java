package org.dodgybits.shuffle.android.core.model.persistence;

import java.util.Collection;

import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.Id;

import android.database.Cursor;
import android.net.Uri;

public interface EntityPersister<E extends Entity> {

    Uri getContentUri();
    
    String[] getFullProjection();
    
    E findById(Id localId);
    
    E read(Cursor cursor);
        
    Uri insert(E e);
    
    void bulkInsert(Collection<E> entities);
    
    void update(E e);
    
    boolean moveToTrash(Id id);
    int moveToTrash(String selection, String[] selectionArgs);

    int emptyTrash();

	boolean deletePermanently(Id id);
    
}
