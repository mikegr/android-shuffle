package org.dodgybits.shuffle.android.core.util;

import java.util.List;

public class StringUtils {

	public static String repeat(int count, String token) {
		return repeat(count, token, "");
	}
	
	public static String repeat(int count, String token, String delim) {
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i <= count; i++) {
			builder.append(token);
			if (i < count) {
				builder.append(delim);
			}
		}
		return builder.toString();
	}
	
	public static String join(List<String> items, String delim) {
        StringBuilder result = new StringBuilder();
        final int len = items.size();
        for(int i = 0; i < len; i++) {
            result.append(items.get(i));
            if (i < len - 1) {
                result.append(delim);
            }
        }
        return result.toString();
	    
	}
}
