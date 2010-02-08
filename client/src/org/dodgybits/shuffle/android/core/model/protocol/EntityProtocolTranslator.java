package org.dodgybits.shuffle.android.core.model.protocol;

import com.google.protobuf.MessageLite;

public interface EntityProtocolTranslator<E,M extends MessageLite> {
    
    E fromMessage(M message);
    
    M toMessage(E entity);

}
