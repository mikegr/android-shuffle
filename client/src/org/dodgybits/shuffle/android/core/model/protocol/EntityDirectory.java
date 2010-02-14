package org.dodgybits.shuffle.android.core.model.protocol;

import org.dodgybits.shuffle.android.core.model.Id;

/**
 * A lookup service for entities. Useful when matching up entities from different
 * sources that may have conflicting ids (e.g. backup or remote synching).
 */
public interface EntityDirectory<Entity> {
	
	public Entity findById(Id id);
	public Entity findByName(String name);

}
