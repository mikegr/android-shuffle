package org.dodgybits.shuffle.android.core.model;

public interface Entity {

    /**
     * @return primary key for entity in local sqlite DB. 0L indicates an unsaved entity.
     */
    Id getLocalId();
    
    /**
     * @return ms since epoch entity was last modified.
     */
    long getModifiedDate();
    

    
}
