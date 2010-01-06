package org.dodgybits.android.shuffle.server.tracks;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.ModelUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Xml;

/**
 * @author Morten Nielsen
 */
public class ContextSynchronizer extends Synchronizer<Context> {
    private final String tracksUrl;

    public ContextSynchronizer(ContentResolver contentResolver, Resources resources, WebClient client, ContextWrapper activity, TracksSynchronizer tracksSynchronizer, String tracksUrl, int basePercent) {
        super(contentResolver, tracksSynchronizer, client, resources, activity, basePercent);

        this.tracksUrl = tracksUrl;
    }

        protected Context FindContextByName(Collection<Context> remoteContexts, Context localContext) {
        Context foundContext = null;
        for (Context context : remoteContexts)
            if (context.name.equals(localContext.name)) {
                foundContext = context;
            }
        return foundContext;
    }

    @Override
    protected String readingRemoteText() {
        return resources.getString(R.string.readingRemoteContexts);
    }

    @Override
    protected String processingText() {
        return resources.getString(R.string.processingContexts);
    }

    @Override
    protected String readingLocalText() {
        return resources.getString(R.string.readingLocalContexts);
    }

    @Override
    protected String stageFinishedText() {
        return resources.getString(R.string.doneWithContexts);
    }

    @Override
    protected void saveLocalEntityFromRemote(Context remoteContext) {
        ModelUtils.insertContext(activity, remoteContext);
    }

    protected Context createMergedLocalEntity(Context localContext, Context newContext) {
        return new Context(localContext.id, newContext.name,
                localContext.colourIndex, localContext.icon,
                newContext.tracksId, newContext.modified);
    }
    protected String createDocumentForEntity(org.dodgybits.android.shuffle.model.Context localContext) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            //serializer.startDocument("UTF-8", true);


            serializer.startTag("", "context");
            Date date = new Date();
            serializer.startTag("", "created-at").attribute("", "type", "datetime").text(simpleDateFormat.format(date)).endTag("", "created-at");
            serializer.startTag("", "hide").attribute("", "type", "boolean").text("false").endTag("", "hide");
            serializer.startTag("", "name").text(localContext.name).endTag("", "name");
            serializer.startTag("", "position").attribute("", "type", "integer").text("12").endTag("", "position");
            serializer.startTag("", "updated-at").attribute("", "type", "datetime").text(simpleDateFormat.format(date)).endTag("", "updated-at");
            serializer.endTag("", "context");
            // serializer.endDocument();
            serializer.flush();
        } catch (IOException ignored) {

        }


        return writer.toString();
    }

    protected org.dodgybits.android.shuffle.model.Context parseSingleEntity(XmlPullParser parser) throws ParseException {

        //               <context>
//<created-at type="datetime">2009-12-29T21:14:19+00:00</created-at>
//<hide type="boolean">false</hide>
//<id type="integer">3486</id>
//<name>beta</name>
//<position type="integer">1</position>
//<updated-at type="datetime">2009-12-29T21:14:19+00:00</updated-at>
//</context>
        try {
            int eventType = parser.getEventType();
            String contextName = null;
            Long id = null;
            Long date = null;
            boolean done = false;

            SimpleDateFormat format = simpleDateFormat;
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:


                        if (name.equalsIgnoreCase("name")) {
                            contextName = parser.nextText();
                        } else if (name.equalsIgnoreCase("id")) {
                            id = Long.parseLong(parser.nextText());
                        } else if (name.equalsIgnoreCase("updated-at")) {
                            date = format.parse(parser.nextText()).getTime();
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equalsIgnoreCase("context") && contextName != null) {
                            return new org.dodgybits.android.shuffle.model.Context(null, contextName, 1, org.dodgybits.android.shuffle.model.Context.Icon.createIcon("network_wireless", resources), id, date);
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
        return null;
    }

    @Override
    protected String createEntityUrl(Context localContext) {
        return tracksUrl + "/contexts/" + localContext.tracksId + ".xml";
    }


           @Override
    protected String endIndexTag() {
        return "contexts";
    }

            @Override
    protected String entityIndexUrl() {
        return tracksUrl+"/contexts.xml";
    }

    protected boolean removeLocalEntity(org.dodgybits.android.shuffle.model.Context context) {
        return 1 == contentResolver.delete(
                Shuffle.Contexts.CONTENT_URI, Shuffle.Contexts._ID + " = " + context.id,
                null);
    }

    protected void saveLocalEntity(org.dodgybits.android.shuffle.model.Context context) {
        ContentValues values = new ContentValues();
        BindingUtils.writeContext(values, context);
        contentResolver.update(Shuffle.Contexts.CONTENT_URI, values, Shuffle.Contexts._ID + "=?", new String[]{String.valueOf(context.id)});
    }

    protected Map<String, org.dodgybits.android.shuffle.model.Context> getShuffleEntities(ContentResolver contentResolver, Resources resources) {


        Cursor cursor = contentResolver.query(
                Shuffle.Contexts.CONTENT_URI, Shuffle.Contexts.cFullProjection,
                null, null, null);


        Map<String, org.dodgybits.android.shuffle.model.Context> list = new HashMap<String, org.dodgybits.android.shuffle.model.Context>();

        while (cursor.moveToNext()) {
            org.dodgybits.android.shuffle.model.Context context = BindingUtils.readContext(cursor, resources);

            list.put(context.name, context);


        }
        cursor.close();
        return list;
    }
}