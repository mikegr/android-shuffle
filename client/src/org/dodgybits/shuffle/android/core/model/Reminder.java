package org.dodgybits.shuffle.android.core.model;

public class Reminder {
	public Integer id;
	public final int minutes;
	public final Method method;

	public Reminder(Integer id, int minutes, Method method) {
		this.id = id;
		this.minutes = minutes;
		this.method = method;
	}

	public Reminder(int minutes, Method method) {
		this(null, minutes, method);
	}
	
	public static enum Method {
		DEFAULT, ALERT;
	}
}
