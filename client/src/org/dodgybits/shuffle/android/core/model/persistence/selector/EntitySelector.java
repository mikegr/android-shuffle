package org.dodgybits.shuffle.android.core.model.persistence.selector;

import android.content.Context;

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
    }
}
