/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.shuffle.android.core.util;

import static android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
import static android.text.format.DateUtils.FORMAT_ABBREV_TIME;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.format.Time;

public class DateUtils {
		
    /**
     * Lazily create date format objects, one per thread. Use soft references so format
     * may be collected when low on memory.
     */
    private static final ThreadLocal<SoftReference<DateFormat>> cDateFormat = 
        new ThreadLocal<SoftReference<DateFormat>>() {
        
            private SoftReference<DateFormat> createValue() {  
                return new SoftReference<DateFormat>(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));  
            }  
            
            @Override
            public SoftReference<DateFormat> get() {
                SoftReference<DateFormat> value = super.get();
                if (value == null || value.get() == null) {
                    value = createValue();
                    set(value);
                }
                return value;
            }
    };  

    /**
     * Accepts strings in ISO 8601 format. This includes the following cases:
     * <ul>
     * <li>2009-08-15T12:50:03+01:00</li>
     * <li>2009-08-15T12:50:03+0200</li>
     * <li>2010-04-07T04:00:00Z</li>
     * </ul>
     */
    public static long parseIso8601Date(String dateStr) throws ParseException {
        // normalize timezone first
        String timezone = dateStr.substring(19);
        timezone = timezone.replaceAll(":", "");
        if ("Z".equals(timezone)) {
            // Z indicates UTC, so convert to standard representation
            timezone = "+0000";
        }
        String cleanedDateStr = dateStr.substring(0, 19) + timezone;
        DateFormat f = cDateFormat.get().get();
        Date d = f.parse(cleanedDateStr);
        return d.getTime();
    }
    
    public static String formatIso8601Date(long ms) {
        DateFormat f = cDateFormat.get().get();
        String dateStr = f.format(new Date(ms));
        if (dateStr.length() == 24) {
            dateStr = dateStr.substring(0, 22) + ":" + dateStr.substring(22);
        }
        return dateStr;
    }
    
	public static boolean isSameDay(long millisX, long millisY) {
		return Time.getJulianDay(millisX, 0) == Time.getJulianDay(millisY, 0);
	}
		
	public static CharSequence displayDateRange(Context context, long startMs, long endMs, boolean includeTime) {
		CharSequence result = "";
		final boolean includeStart = startMs > 0L;
		final boolean includeEnd = endMs > 0L;
		
		if (includeStart) {
		    if (includeEnd) {
	            int flags = FORMAT_SHOW_DATE | FORMAT_ABBREV_MONTH;
	            if (includeTime) {
	                flags |= FORMAT_SHOW_TIME | FORMAT_ABBREV_TIME; 
	            }
	            result = android.text.format.DateUtils.formatDateRange(
	                    context, startMs, endMs, flags);
		    } else {
		        result = displayShortDateTime(context, startMs);
		    }
		} else if (includeEnd) {
            result = displayShortDateTime(context, endMs);
		}
		
		return result;
	}
	
    /**
     * Display date time in short format using the user's date format settings
     * as a guideline.
     * 
     * For epoch, display nothing.
     * For today, only show the time.
     * Otherwise, only show the day and month.
     * 
     * @param context
     * @param timeInMs datetime to display
     * @return locale specific representation
     */
    public static CharSequence displayShortDateTime(Context context, long timeInMs) {
        long now = System.currentTimeMillis();
        CharSequence result;
        if (timeInMs == 0L) {
            result = "";
        } else {
            int flags;
            if (isSameDay(timeInMs, now)) {
                flags = FORMAT_SHOW_TIME | FORMAT_ABBREV_TIME;
            } else {
                flags = FORMAT_SHOW_DATE | FORMAT_ABBREV_MONTH;
            }
            result = android.text.format.DateUtils.formatDateRange(
                    context, timeInMs, timeInMs, flags);
        }
        return result;
    }
	
	
}
