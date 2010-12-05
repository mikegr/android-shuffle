package org.dodgybits.shuffle.android.synchronisation.tracks.parsing;

import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryTracksSyncError;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.synchronisation.tracks.TracksEntities;
import org.dodgybits.shuffle.android.synchronisation.tracks.model.TracksEntity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public abstract class Parser<E extends TracksEntity> {
	private static final String cTag = "Parser";
	protected HashMap<String, Applier> appliers;
	
	private String mEntityName;

	private Analytics mAnalytics;

	public Parser(String entityName, Analytics analytics) {
		appliers = new HashMap<String, Applier>();
		mEntityName = entityName;
		mAnalytics = analytics;
	}
	public TracksEntities<E> parseDocument(String tracksEntityXml) {
		Map<Id, E> entities = new HashMap<Id, E>();
        boolean errorFree = true;
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(tracksEntityXml));

            int eventType = parser.getEventType();
            boolean done = false;
            
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                ParseResult<E> result = null;
                try {
                    result = parseSingle(parser);
                } catch (Exception e) {
                    logTracksError(e);
                    errorFree = false;
                }
                if(!result.IsSuccess()) {
                	errorFree = false;
                }
                
                E entity = result.getResult();
                
                
                if (entity != null && entity.isValid()) {
                    entities.put(entity.getTracksId(), entity);
                }
                
                eventType = parser.getEventType();
                String name = parser.getName();
                if (eventType == XmlPullParser.END_TAG &&
                   name.equalsIgnoreCase(endIndexTag())) {
                   done = true;
                }
            }
        } catch (XmlPullParserException e) {
            logTracksError(e);
            errorFree = false;
        }
        
        return new TracksEntities<E>(entities, errorFree);
	}
	
	private void logTracksError(Exception e) {
        Log.e(cTag, "Failed to parse " + endIndexTag() + " " + e.getMessage());
        mAnalytics.onError(cFlurryTracksSyncError, e.getMessage(), getClass().getName());
    }

	

	private String endIndexTag() {
		return this.mEntityName + "s"; 
	}
	public ParseResult<E> parseSingle(XmlPullParser parser) {
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
	            return new ParseResult<E>();
	        } catch (XmlPullParserException e) {
	        	Log.d("Exception", "pullparser exception", e);
	            return new ParseResult<E>();
	        }
	        return  new ParseResult<E>(entity, success);
	}

	protected abstract EntityBuilder<E> createBuilder();

}
