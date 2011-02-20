package org.dodgybits.shuffle.android.core.model.persistence.selector;

import android.content.Context;
import android.net.Uri;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

public interface EntitySelector<E extends EntitySelector> {

    Uri getContentUri();

    Flag getActive();
    Flag getDeleted();

    String getSelection(Context context); 
    String[] getSelectionArgs();
    String getSortOrder();

    Builder<E> builderFrom();
    
    public interface Builder<E extends EntitySelector> {


        Flag getActive();
        Builder<E> setActive(Flag value);
        
        Flag getDeleted();
        Builder<E> setDeleted(Flag value);
        
        String getSortOrder();
        Builder<E> setSortOrder(String value);
        
        E build();

        Builder<E> mergeFrom(E selector);

        Builder<E> applyListPreferences(android.content.Context context, ListPreferenceSettings settings);

    }
}
