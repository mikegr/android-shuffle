package org.dodgybits.android.shuffle.activity.config;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;

public interface ExpandableListConfig<G,C> {


    /**
     * @return id of layout for this view
     */
    abstract int getContentViewResId();


    /**
     * Content type of top level list items.
     */
    abstract Uri getGroupContentUri();


    /**
     * Content type of child items.
     */
    abstract Uri getChildContentUri();

    abstract int getCurrentViewMenuId();

    abstract String getGroupName(ContextWrapper context);
    abstract String getChildName(ContextWrapper context);
    
    /**
     * @return the name of the database column holding the key from the child to the parent
     */
    abstract String getGroupIdColumnName();
    /**
     * Generate a model object for the group item at the current cursor position.
     */
    abstract G readGroup(Cursor cursor, Resources res);

    /**
     * Generate a model object for the child item at the current cursor position.
     */
    abstract C readChild(Cursor cursor, Resources res);

    
}
