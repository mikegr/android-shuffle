package org.dodgybits.shuffle.android.core.model.protocol;

import org.dodgybits.shuffle.dto.ShuffleProtos.Date;

public final class ProtocolUtil {
    
    private ProtocolUtil() {
        // deny
    }
    
    public static Date toDate(long millis) {
        return Date.newBuilder()
            .setMillis(millis)
            .build();
    }

    public static long fromDate(Date date) {
        long millis = 0L;
        if (date != null) {
            millis = date.getMillis();
        }
        return millis;
    }
    
    
}
