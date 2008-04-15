package org.dodgybits.android.shuffle.view;

import org.dodgybits.android.shuffle.R;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

public class ExpandableTaskView extends TaskView {
    private static final String cTag = "ExpandableTaskView";

	public ExpandableTaskView(Context androidContext, boolean showContext) {
		super(androidContext, showContext);
		
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.relLayout);
		// 36 is the current value of ?android:attr/expandableListPreferredItemPaddingLeft
		// TODO get that value programatically
		layout.setPadding(36, 0, 0, 0);
	}
	
}
