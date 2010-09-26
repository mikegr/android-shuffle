package org.dodgybits.shuffle.android.synchronisation.tracks;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Context.Builder;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.util.DateUtils;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.ContextParser;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.Parser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

/**
 * @author Morten Nielsen
 */
public class ContextSynchronizer extends Synchronizer<Context> {
    private final String mTracksUrl;
	private Parser<Context> mParser;
    
    public ContextSynchronizer(
            EntityPersister<Context> persister,
            TracksSynchronizer tracksSynchronizer, 
            WebClient client, 
            android.content.Context context, 
            Analytics analytics,
            int basePercent, 
            String tracksUrl) {
        super(persister, tracksSynchronizer, client, context, basePercent);
        mParser = new ContextParser(analytics);
        mTracksUrl = tracksUrl;
    }
    
    @Override
    protected void verifyEntitiesForSynchronization(Map<Id, Context> localEntities) {
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
            .setDeleted(newContext.isDeleted())
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


            String now = DateUtils.formatIso8601Date(System.currentTimeMillis());
            serializer.startTag("", "context");
            serializer.startTag("", "created-at").attribute("", "type", "datetime").text(now).endTag("", "created-at");
            serializer.startTag("", "hide").attribute("", "type", "boolean").text("false").endTag("", "hide");
            serializer.startTag("", "name").text(localContext.getName()).endTag("", "name");
            serializer.startTag("", "position").attribute("", "type", "integer").text("12").endTag("", "position");
            serializer.startTag("", "state").text(localContext.isDeleted() ? "hidden" : "active").endTag("", "state");
            serializer.startTag("", "updated-at").attribute("", "type", "datetime").text(now).endTag("", "updated-at");
            serializer.endTag("", "context");
            // serializer.endDocument();
            serializer.flush();
        } catch (IOException ignored) {

        }


        return writer.toString();
    }


    @Override
    protected String createEntityUrl(Context localContext) {
        return mTracksUrl + "/contexts/" + localContext.getTracksId().getId() + ".xml";
    }

    @Override
    protected String entityIndexUrl() {
        return mTracksUrl+"/contexts.xml";
    }

	@Override
	protected Parser<Context> getEntityParser() {
		return mParser;
	}



}