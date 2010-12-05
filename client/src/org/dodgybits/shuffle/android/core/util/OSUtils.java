package org.dodgybits.shuffle.android.core.util;

import java.lang.reflect.Field;

import android.os.Build;

public class OSUtils {

    public static boolean osAtLeastFroyo() {
        boolean isFroyoOrAbove = false;
        try {
            Field field = Build.VERSION.class.getDeclaredField("SDK_INT");
            int version = field.getInt(null);
            isFroyoOrAbove = version >= Build.VERSION_CODES.FROYO;
        } catch (Exception e) {
            // ignore exception - field not available
        }
        return isFroyoOrAbove;
    }
    
    
}
