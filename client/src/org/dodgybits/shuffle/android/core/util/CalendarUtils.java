package org.dodgybits.shuffle.android.core.util;

import android.net.Uri;

public class CalendarUtils {


    // We can't use the constants from the provider since it's not a public portion of the SDK.

    private static final Uri CALENDAR_CONTENT_URI =
        Uri.parse("content://calendar/calendars"); // Calendars.CONTENT_URI
    private static final Uri CALENDAR_CONTENT_URI_FROYO_PLUS =
        Uri.parse("content://com.android.calendar/calendars"); // Calendars.CONTENT_URI

    private static final Uri EVENT_CONTENT_URI =
        Uri.parse("content://calendar/events"); // Calendars.CONTENT_URI
    private static final Uri EVENT_CONTENT_URI_FROYO_PLUS =
        Uri.parse("content://com.android.calendar/events"); // Calendars.CONTENT_URI

    public static Uri getCalendarContentUri() {
        Uri uri;
        if(OSUtils.osAtLeastFroyo()) {
            uri = CALENDAR_CONTENT_URI_FROYO_PLUS;
        } else {
            uri = CALENDAR_CONTENT_URI;
        }
        return uri;
    }

    public static Uri getEventContentUri() {
        Uri uri;
        if(OSUtils.osAtLeastFroyo()) {
            uri = EVENT_CONTENT_URI_FROYO_PLUS;
        } else {
            uri = EVENT_CONTENT_URI;
        }
        return uri;
    }

}
