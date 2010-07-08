package org.dodgybits.shuffle.android.synchronisation.tracks.parsing;

import java.io.IOException;
import java.util.HashMap;

import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public abstract class Parser<E> {

	protected HashMap<String, Applier> appliers;
	
	private String mEntityName;

	public Parser(String entityName) {
		appliers = new HashMap<String, Applier>();
		mEntityName = entityName;
	}

	public E parseSingle(XmlPullParser parser) {
		EntityBuilder<E> builder = createBuilder();
		 E entity = null;
		 boolean success = true;
	        try {
	            int eventType = parser.getEventType();
	
	            while (eventType != XmlPullParser.END_DOCUMENT && entity == null) {
	                String name = parser.getName();
	
	                switch (eventType) {
	                    case XmlPullParser.START_DOCUMENT:
	                        break;
	                        
	                    case XmlPullParser.START_TAG:
	                    	Applier applier = appliers.get(name);
							if(applier != null) {
								success &= applier.apply(parser.nextText());
							}
	                        break;
	                        
	                    case XmlPullParser.END_TAG:
	                    	
	                        if (name.equalsIgnoreCase(mEntityName)) {
	                            entity = builder.build();
	                        }
	                        break;
	                }
	                
	                eventType = parser.next();
	            }
	        } catch (IOException e) {
	        	Log.d("Exception", "IO EXception", e);
	            return null;
	        } catch (XmlPullParserException e) {
	        	Log.d("Exception", "pullparser exception", e);
	            return null;
	        }
	        return success ? entity : null;
	}

	protected abstract EntityBuilder<E> createBuilder();

}
