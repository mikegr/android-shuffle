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

package org.dodgybits.android.shuffle.util;

import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.text.format.Time;

import static android.text.format.DateUtils.*;

public class DateUtils {
	
	public static final long HOUR_IN_MILLIS = 3600000; 
	
	private static DateFormat sDateFormat = null;
		
	public static String formatDate(long timeInMs) {
		if (sDateFormat == null) {
			sDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		}
		return sDateFormat.format(new Date(timeInMs));
	}
	
	public static boolean isSameDay(long millisX, long millisY) {
		return Time.getJulianDay(millisX, 0) == Time.getJulianDay(millisY, 0);
	}
		
	public static CharSequence displayDate(Context context, long timeInMs) {
		int flags = FORMAT_SHOW_TIME;
		return android.text.format.DateUtils.getRelativeDateTimeString(
				context, timeInMs, MINUTE_IN_MILLIS, WEEK_IN_MILLIS, flags);
	}
	
}
