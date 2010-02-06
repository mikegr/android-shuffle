package org.dodgybits.shuffle.android.core.model;

import org.dodgybits.shuffle.dto.ShuffleProtos.Date;

public class AbstractEntity {

    protected static Date toDate(long millis) {
        return Date.newBuilder()
            .setMillis(millis)
            .build();
    }

    protected static long fromDate(Date date) {
        long millis = 0L;
        if (date != null) {
            millis = date.getMillis();
        }
        return millis;
    }
    
}
