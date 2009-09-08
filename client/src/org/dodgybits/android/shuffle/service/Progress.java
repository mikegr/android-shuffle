package org.dodgybits.android.shuffle.service;

public class Progress {
	private int mProgressPercent;
	private String mDetails;
	private boolean mIsError;
	
	public Progress(int progressPercent, String details, boolean isError) {
		super();
		mProgressPercent = progressPercent;
		mDetails = details;
		mIsError = isError;
	}

	public final int getProgressPercent() {
		return mProgressPercent;
	}

	public final String getDetails() {
		return mDetails;
	}

	public final boolean isError() {
		return mIsError;
	}

	public final boolean isComplete() {
		return mProgressPercent == 100;
	}
}
