package org.dodgybits.shuffle.android.core.model.persistence.selector;

import android.content.Context;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

public interface EntitySelector {

    Flag getActive();
    Flag getDeleted();

    String getSelection(Context context); 
    String[] getSelectionArgs();
    String getSortOrder(); 
    
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
