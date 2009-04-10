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
import java.util.Calendar;
import java.util.Date;

import org.dodgybits.android.shuffle.R;

import android.content.Context;

public class DateUtils {
	
	private static DateFormat sDateFormat = null;
		
	public static String formatDate(Date date) {
		if (sDateFormat == null) {
			sDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		}
		return sDateFormat.format(date);
	}
	
	public static boolean isToday(Date date) {
		Calendar now = Calendar.getInstance();
		Calendar then = Calendar.getInstance();
		then.setTime(date);
		return isSameDay(now, then);
	}
	
	public static boolean isSameDay(Calendar calA, Calendar calB) {
		return (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
				calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR));
	}
	
	public static String displayDate(Context context, Date date) {
		Calendar now = Calendar.getInstance();
		Calendar ourDate = Calendar.getInstance();
		ourDate.setTime(date);
		if (isSameDay(now, ourDate)) {
			return context.getString(R.string.today);
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		if (isSameDay(cal, ourDate)) {
			return context.getString(R.string.yesterday);
		}
		cal.add(Calendar.DAY_OF_YEAR, 7);
		if (ourDate.after(now) && ourDate.before(cal)) {
			// date is within a week - show day of week
			int dayOfWeek = ourDate.get(Calendar.DAY_OF_WEEK);
			return getDayOfWeekStr(dayOfWeek);
		}
		return formatDate(date);
	}
	
	private static String getDayOfWeekStr(int dayOfWeek) {
		String result = "";
		switch (dayOfWeek) {
		case Calendar.MONDAY:
			result = "Monday";
			break;
		case Calendar.TUESDAY:
			result = "Tuesday";
			break;
		case Calendar.WEDNESDAY:
			result = "Wednesday";
			break;
		case Calendar.THURSDAY:
			result = "Thursday";
			break;
		case Calendar.FRIDAY:
			result = "Friday";
			break;
		case Calendar.SATURDAY:
			result = "Saturday";
			break;
		case Calendar.SUNDAY:
			result = "Sunday";
			break;
			
		}
		return result;
	}
	
}
