package org.dodgybits.shuffle.android.core.model;

public interface EntityBuilder<E> {

    EntityBuilder<E> mergeFrom(E e);
    EntityBuilder<E> setLocalId(Id id);
    EntityBuilder<E> setModifiedDate(long ms);
    EntityBuilder<E> setTracksId(Id id);
    EntityBuilder<E> setActive(boolean value);
    EntityBuilder<E> setDeleted(boolean value);
    E build();
    
}
