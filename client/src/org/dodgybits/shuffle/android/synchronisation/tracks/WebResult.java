package org.dodgybits.shuffle.android.synchronisation.tracks;

import org.apache.http.StatusLine;



public class WebResult {
	private String mContent;
	private StatusLine mStatus;
	
	public WebResult(StatusLine status, String content) {
		mStatus = status;
		mContent = content;
	}
	
	public String getContent() {
		return mContent;
	}
	
	public StatusLine getStatus() {
		return mStatus;
	}
}
