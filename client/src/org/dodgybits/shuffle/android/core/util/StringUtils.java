package org.dodgybits.shuffle.android.core.util;

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
}
