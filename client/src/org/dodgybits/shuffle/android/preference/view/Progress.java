package org.dodgybits.shuffle.android.preference.view;

public class Progress {
	private int mProgressPercent;
	private String mDetails;
	private boolean mIsError;
	private Runnable mErrorUIAction;
	
	public static Progress createProgress(int progressPercent, String details) {
		return new Progress(progressPercent, details, false, null);
	}
	
	public static Progress createErrorProgress(String errorMessage) {
		return new Progress(0, errorMessage, true, null);
	}
	
	public static Progress createErrorProgress(String errorMessage, Runnable errorUIAction) {
		return new Progress(0, errorMessage, true, errorUIAction);
	}
	
	private Progress(int progressPercent, String details, boolean isError, Runnable errorUIAction) {
		mProgressPercent = progressPercent;
		mDetails = details;
		mIsError = isError;
		mErrorUIAction = errorUIAction;
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

	public final Runnable getErrorUIAction() {
		return mErrorUIAction;
	}

	public final boolean isComplete() {
		return mProgressPercent == 100;
	}
}
