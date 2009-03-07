package org.dodgybits.android.shuffle.view;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class ItemView<T> extends LinearLayout {

	public ItemView(Context androidContext) {
		super(androidContext);
	}
	
	public abstract void updateView(T item);

}
