package org.dodgybits.android.shuffle.model;

import android.content.res.Resources;
import android.text.TextUtils;

public class Context {
	public Integer id;
	public final String name;
	public final int colourIndex;
	// resource id to icon resource (may be null)
	public final Icon icon;

	public Context(Integer id, String name, int colourIndex, Icon icon) {
		this.id = id;
		this.name = name;
		this.colourIndex = colourIndex;
		this.icon = icon;
	}
	
	public Context(String name, int colour, Icon icon) {
		this(null, name, colour, icon);
	}
	
	public static class Icon {
		private static final String cPackage = "org.dodgybits.android.shuffle"; 
		private static final String cType = "drawable";
		
		public static final Icon NONE = new Icon(null, 0, 0);
		
		public final String iconName;
		public final int largeIconId;
		public final int smallIconId;
		
		private Icon(String iconName, int largeIconId, int smallIconId) {
			this.iconName = iconName;
			this.largeIconId = largeIconId;
			this.smallIconId = smallIconId;
		}
		
		public static Icon createIcon(String iconName, Resources res) {
			if (TextUtils.isEmpty(iconName)) return NONE;
			int largeId = res.getIdentifier(iconName, cType, cPackage);
			int smallId = res.getIdentifier(iconName + "_small", cType, cPackage);
			return new Icon(iconName, largeId, smallId);
		}
	}
}
