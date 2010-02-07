package org.dodgybits.shuffle.android.core.model.protocol;

public interface Locator<T> {
	
	public T findById(long id);
	public T findByName(String name);

}
