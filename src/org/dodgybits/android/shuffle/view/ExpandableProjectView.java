package org.dodgybits.android.shuffle.view;

import org.dodgybits.android.shuffle.R;

import android.content.Context;

public class ExpandableProjectView extends ProjectView {

	public ExpandableProjectView(Context androidContext) {
		super(androidContext);
	}
	
	protected int getViewResourceId() {
		return R.layout.expandable_project_view;
	}

}
