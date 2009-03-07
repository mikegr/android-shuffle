package org.dodgybits.android.shuffle.activity.config;

import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;

public interface ListConfig<T> {

    public String createTitle(ContextWrapper context);

	public String getItemName(ContextWrapper context);

	/**
	 * @return id of layout for this view
	 */
	public int getContentViewResId();

	/**
	 * Content type of list itself.
	 */
	public Uri getListContentUri();

	/**
	 * Content type of list items.
	 */
	public Uri getContentUri();

	/**
	 * Generate a model object for the item at the current cursor position.
	 */
	public T readItem(Cursor cursor);

	public int getCurrentViewMenuId();
	
	public boolean supportsViewAction();
	
	public boolean isTaskList();
		
}
