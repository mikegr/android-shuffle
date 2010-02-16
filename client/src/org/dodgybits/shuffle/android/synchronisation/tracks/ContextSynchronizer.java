package org.dodgybits.shuffle.android.synchronisation.tracks;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Context.Builder;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

/**
 * @author Morten Nielsen
 */
public class ContextSynchronizer extends Synchronizer<Context> {
    private final String mTracksUrl;
    
    public ContextSynchronizer(
            TracksSynchronizer tracksSynchronizer, 
            WebClient client, 
            android.content.Context context, 
            int basePercent, 
            String tracksUrl) {
        super(tracksSynchronizer, client, context, basePercent);

        mTracksUrl = tracksUrl;
    }
    
    @Override
    protected EntityPersister<Context> createPersister() {
        return new ContextPersister(mContext.getContentResolver());
    }
    
    @Override
    protected void verifyLocalEntities(Map<Id, Context> localEntities) {
    }

    @Override
    protected String readingRemoteText() {
        return mContext.getString(R.string.readingRemoteContexts);
    }

    @Override
    protected String processingText() {
        return mContext.getString(R.string.processingContexts);
    }

    @Override
    protected String readingLocalText() {
        return mContext.getString(R.string.readingLocalContexts);
    }

    @Override
    protected String stageFinishedText() {
        return mContext.getString(R.string.doneWithContexts);
    }

    @Override
    protected EntityBuilder<Context> createBuilder() {
        return Context.newBuilder();
    }
    
    @Override
    protected Context createMergedLocalEntity(Context localContext, Context newContext) {
        Builder builder = Context.newBuilder();
        builder.mergeFrom(localContext);
        builder
            .setName(newContext.getName())
            .setModifiedDate(newContext.getModifiedDate())
            .setTracksId(newContext.getTracksId());
        return builder.build();
    }
    
    @Override
    protected String createDocumentForEntity(Context localContext) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            //serializer.startDocument("UTF-8", true);


            serializer.startTag("", "context");
            Date date = new Date();
            serializer.startTag("", "created-at").attribute("", "type", "datetime").text(mDateFormat.format(date)).endTag("", "created-at");
            serializer.startTag("", "hide").attribute("", "type", "boolean").text("false").endTag("", "hide");
            serializer.startTag("", "name").text(localContext.getName()).endTag("", "name");
            serializer.startTag("", "position").attribute("", "type", "integer").text("12").endTag("", "position");
            serializer.startTag("", "updated-at").attribute("", "type", "datetime").text(mDateFormat.format(date)).endTag("", "updated-at");
            serializer.endTag("", "context");
            // serializer.endDocument();
            serializer.flush();
        } catch (IOException ignored) {

        }


        return writer.toString();
    }

    @Override
    protected Context parseSingleEntity(XmlPullParser parser) throws ParseException {

        //               <context>
//<created-at type="datetime">2009-12-29T21:14:19+00:00</created-at>
//<hide type="boolean">false</hide>
//<id type="integer">3486</id>
//<name>beta</name>
//<position type="integer">1</position>
//<updated-at type="datetime">2009-12-29T21:14:19+00:00</updated-at>
//</context>
        Context context = null;
        Builder builder = Context.newBuilder();
        
        try {
            int eventType = parser.getEventType();

            final DateFormat format = mDateFormat;
            while (eventType != XmlPullParser.END_DOCUMENT && context == null) {
                String name = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                        
                    case XmlPullParser.START_TAG:
                        if (name.equalsIgnoreCase("name")) {
                            builder.setName(parser.nextText());
                        } else if (name.equalsIgnoreCase("id")) {
                            Id tracksId = Id.create(Long.parseLong(parser.nextText()));
                            builder.setTracksId(tracksId);
                        } else if (name.equalsIgnoreCase("updated-at")) {
                            long date = format.parse(parser.nextText()).getTime();
                            builder.setModifiedDate(date);
                        }
                        break;
                        
                    case XmlPullParser.END_TAG:
                        if (name.equalsIgnoreCase("context")) {
                            context = builder.build();
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (IOException e) {
            throw new ParseException("Unable to parse context", 0);
        } catch (XmlPullParserException e) {
            throw new ParseException("Unable to parse context", 0);
        }
        return context;
    }

    @Override
    protected String createEntityUrl(Context localContext) {
        return mTracksUrl + "/contexts/" + localContext.getTracksId().getId() + ".xml";
    }

    @Override
    protected String endIndexTag() {
        return "contexts";
    }

    @Override
    protected String entityIndexUrl() {
        return mTracksUrl+"/contexts.xml";
    }

}