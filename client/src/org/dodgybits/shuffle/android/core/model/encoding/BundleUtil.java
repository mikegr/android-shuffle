package org.dodgybits.shuffle.android.core.model.encoding;

import org.dodgybits.shuffle.android.core.model.Id;

import android.os.Bundle;

public class BundleUtil {

//    public static Long getLong(Bundle icicle, String key) {
//        return icicle.containsKey(key) ? icicle.getLong(key) : null;
//    }
//
//    public static Integer getInteger(Bundle icicle, String key) {
//        return icicle.containsKey(key) ? icicle.getInt(key) : null;
//    }
//
//    public static void putInteger(Bundle icicle, String key, Integer value) {
//        if (value != null) icicle.putInt(key, value);
//    }
//
//    public static void putLong(Bundle icicle, String key, Long value) {
//        if (value != null) icicle.putLong(key, value);
//    }
    
    public static Id getId(Bundle icicle, String key) {
        Id result = Id.NONE;
        if (icicle.containsKey(key)) {
            result = Id.create(icicle.getLong(key));
        }
        return result;
    }
    
    public static void putId(Bundle icicle, String key, Id value) {
        if (value.isInitialised()) {
            icicle.putLong(key, value.getId());
        }
    }
    
    public static String getString(Bundle icicle, String key) {
        String result = null;
        if (icicle.containsKey(key)) {
            result = icicle.getString(key);
        }
        return result;
    }
    
    public static void putString(Bundle icicle, String key, String value) {
        if (value != null) {
            icicle.putString(key, value);
        }
    }
    
    
}
