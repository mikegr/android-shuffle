package org.dodgybits.shuffle.android.core.model.protocol;

import java.util.HashMap;
import java.util.Map;

import org.dodgybits.shuffle.android.core.model.Id;

public class HashEntityDirectory<Entity> implements EntityDirectory<Entity> {

	private Map<String,Entity> mItemsByName;
	private Map<Id, Entity> mItemsById;
	
	public HashEntityDirectory() {
		mItemsByName = new HashMap<String,Entity>();
		mItemsById = new HashMap<Id,Entity>();
	}
	
	public void addItem(Id id, String name, Entity item) {
		mItemsById.put(id, item);
		mItemsByName.put(name, item);
	}
	
	@Override
	public Entity findById(Id id) {
		return mItemsById.get(id);
	}
	
	@Override
	public Entity findByName(String name) {
		return mItemsByName.get(name);
	}
}
