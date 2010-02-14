package org.dodgybits.shuffle.android.core.model.persistence;

import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.Id;

import android.database.Cursor;

public interface EntityLocator<E extends Entity> {

    E findById(Id localId);
    
    Cursor findAll();
    
    Cursor findByQuery(Query query);
    
}
