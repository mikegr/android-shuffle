package org.dodgybits.shuffle.android.core.model.persistence;

import org.dodgybits.shuffle.android.core.model.Id;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class AbstractEntityPersister {

    protected static Id readId(Cursor cursor, int index) {
        Id result = Id.NONE;
        if (!cursor.isNull(index)) {
            result = Id.create(cursor.getLong(index));
        }
        return result;
    }
    
    protected static String readString(Cursor cursor, int index) {
        return (cursor.isNull(index) ? null : cursor.getString(index));
    }
    
    protected static long readLong(Cursor cursor, int index) {
        return readLong(cursor, index, 0L);
    }
    
    protected static long readLong(Cursor cursor, int index, long defaultValue) {
        long result = defaultValue;
        if (!cursor.isNull(index)) {
            result = cursor.getLong(index);
        }
        return result;
    }
    
    protected static Boolean readBoolean(Cursor cursor, int index) {
        return (cursor.getInt(index) == 1);
    }
    
    protected static void writeId(ContentValues values, String key, Id id) {
        if (id.isInitialised()) {
            values.put(key, id.getId());
        }
    }
    
    protected static void writeBoolean(ContentValues values, String key, boolean value) {
        values.put(key, value ? 1 : 0);
    }
    
    protected static void writeString(ContentValues values, String key, String value) {
        if (value == null) {
            values.putNull(key);
        } else {
            values.put(key, value);
        }
        
    }
    
}
