package org.dodgybits.shuffle.android.synchronisation.tracks.parsing;

public class ParseResult<T> {
	private boolean mSuccess;
	private T mResult;
	
	public ParseResult(T result, boolean success) {
		mSuccess = success;
		mResult = result;
	}
	
	public ParseResult() {
		mSuccess = false;
		mResult = null;
	}

	public T getResult() {
		return mResult;
	}
	public boolean IsSuccess(){
		return mSuccess;
	}
	
}
