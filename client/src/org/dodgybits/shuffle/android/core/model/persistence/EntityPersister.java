package org.dodgybits.shuffle.android.core.model.persistence;

import android.content.ContentValues;
import android.database.Cursor;

public interface EntityPersister<Entity> {

    Entity read(Cursor cursor);
    void write(ContentValues values, Entity e);
    
}
