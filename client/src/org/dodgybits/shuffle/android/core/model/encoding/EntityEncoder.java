package org.dodgybits.shuffle.android.core.model.encoding;

import android.os.Bundle;

public interface EntityEncoder<Entity> {
    
    void save(Bundle icicle, Entity e);
    Entity restore(Bundle icicle);

}
