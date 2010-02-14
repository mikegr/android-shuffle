package org.dodgybits.shuffle.android.core.model.persistence;

import java.util.Collection;

import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.Id;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public abstract class AbstractEntityPersister<E extends Entity> implements EntityPersister<E> {

    protected ContentResolver mResolver;
    
    public AbstractEntityPersister(ContentResolver resolver) {
        mResolver = resolver;
    }
    
    
    @Override
    public Uri insert(E e) {
        validate(e);
        Uri uri = mResolver.insert(getContentUri(), null);
        update(uri, e);
        return uri;
    }

    @Override
    public void bulkInsert(Collection<E> entities) {
        int numEntities = entities.size();
        if (numEntities > 0) {
            ContentValues[] valuesArray = new ContentValues[numEntities];
            int i = 0;
            for(E entity : entities) {
                validate(entity);
                ContentValues values = new ContentValues();
                writeContentValues(values, entity);
                valuesArray[i++] = values;
            }
            mResolver.bulkInsert(getContentUri(), valuesArray);
        }
    }

    @Override
    public void update(E e) {
        validate(e);
        Uri uri = getUri(e);
        update(uri, e);
    }

    @Override
    public boolean delete(Id id) {
        Uri uri = getUri(id);
        return mResolver.delete(uri, null, null) == 1;
    }
    
    abstract public Uri getContentUri();
    
    abstract protected void writeContentValues(ContentValues values, E e);
    
    private void validate(E e) {
        if (e == null || !e.isInitialized()) {
            throw new IllegalArgumentException("Cannot persist uninitialised entity " + e);
        }
    }
    
    private Uri getUri(E e) {
        return getUri(e.getLocalId());
    }

    private Uri getUri(Id localId) {
        return ContentUris.appendId(
                getContentUri().buildUpon(), localId.getId()).build();
    }
    
    private void update(Uri uri, E e) {
        ContentValues values = new ContentValues();
        writeContentValues(values, e);
        mResolver.update(uri, values, null, null);
    }
    
    
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
