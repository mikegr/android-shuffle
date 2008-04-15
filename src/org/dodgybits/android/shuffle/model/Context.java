package org.dodgybits.android.shuffle.model;

public class Context {
	public Integer id;
	public final String name;
	public final int colourIndex;
	// resource id to icon resource (may be null)
	public final Integer iconResource;

	public Context(Integer id, String name, int colourIndex, Integer iconResource) {
		this.id = id;
		this.name = name;
		this.colourIndex = colourIndex;
		this.iconResource = iconResource;
	}
	
	public Context(String name, int colour, Integer iconResource) {
		this(null, name, colour, iconResource);
	}
	
}
