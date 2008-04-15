package org.dodgybits.android.shuffle.view;

import org.dodgybits.android.shuffle.R;

import android.content.Context;

public class ExpandableContextView extends ContextView {

	public ExpandableContextView(Context androidContext) {
		super(androidContext);
	}
	
	protected int getViewResourceId() {
		return R.layout.expandable_context_view;
	}

}
