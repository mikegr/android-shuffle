package org.dodgybits.shuffle.android.core.model.encoding;

import org.dodgybits.shuffle.android.core.model.Id;

import android.os.Bundle;

public abstract class AbstractEntityEncoder {

    protected static Id getId(Bundle icicle, String key) {
        Id result = Id.NONE;
        if (icicle.containsKey(key)) {
            result = Id.create(icicle.getLong(key));
        }
        return result;
    }
    
    protected static void putId(Bundle icicle, String key, Id value) {
        if (value.isInitialised()) {
            icicle.putLong(key, value.getId());
        }
    }
    
    protected static String getString(Bundle icicle, String key) {
        String result = null;
        if (icicle.containsKey(key)) {
            result = icicle.getString(key);
        }
        return result;
    }
    
    protected static void putString(Bundle icicle, String key, String value) {
        if (value != null) {
            icicle.putString(key, value);
        }
    }
    
    
}
