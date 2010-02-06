package org.dodgybits.shuffle.android.core.model.encoding;

import java.util.HashMap;
import java.util.Map;

public class BaseLocator<T> implements Locator<T> {

	private Map<String,T> mItemsByName;
	private Map<Long, T> mItemsById;
	
	public BaseLocator() {
		mItemsByName = new HashMap<String,T>();
		mItemsById = new HashMap<Long,T>();
	}
	
	public void addItem(long id, String name, T item) {
		mItemsById.put(id, item);
		mItemsByName.put(name, item);
	}
	
	@Override
	public T findById(long id) {
		return mItemsById.get(id);
	}
	
	@Override
	public T findByName(String name) {
		return mItemsByName.get(name);
	}
}
