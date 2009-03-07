package org.dodgybits.android.shuffle.activity.config;

import android.content.ContextWrapper;
import android.net.Uri;

public interface DrilldownListConfig<T> extends ListConfig<T> {

	public Uri getChildContentUri();
    
	public String getChildName(ContextWrapper context);
			
}
