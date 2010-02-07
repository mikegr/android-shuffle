package org.dodgybits.shuffle.android.core.model.encoding;

import android.os.Bundle;

public interface Encoder<Entity> {
    
    Bundle save(Entity e);
    Entity restore(Bundle icicle);

}
