package org.dodgybits.shuffle.android.synchronisation.tracks;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Project.Builder;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

/**
 * @author Morten Nielsen
 */
public final class ProjectSynchronizer extends Synchronizer<Project> {
    private static final String cTag = "ProjectSynchronizer";
    
    private final String mTracksUrl;

    public ProjectSynchronizer(
            TracksSynchronizer tracksSynchronizer, 
            WebClient client, 
            android.content.Context context,
            int basePercent,
            String tracksUrl) {
        super(tracksSynchronizer, client, context, basePercent);

        mTracksUrl = tracksUrl;
    }
    
    @Override
    protected EntityPersister<Project> createPersister() {
        return new ProjectPersister(mContext.getContentResolver());
    }


    @Override
    protected void verifyLocalEntities(Map<Id, Project> localEntities) {
        
    }
    
    @Override
    protected EntityBuilder<Project> createBuilder() {
        return Project.newBuilder();
    }

    @Override
    protected String readingRemoteText() {
        return mContext.getString(R.string.readingRemoteContexts);
    }

    @Override
    protected String processingText() {
        return mContext.getString(R.string.processingProjects);
    }

    @Override
    protected String readingLocalText() {
        return mContext.getString(R.string.readingLocalProjects);
    }

    @Override
    protected String stageFinishedText() {
        return mContext.getString(R.string.doneWithProjects);
    }

    protected Project createMergedLocalEntity(Project localProject, Project newContext) {
        Builder builder = Project.newBuilder();
        builder.mergeFrom(newContext);
        builder
            .setLocalId(localProject.getLocalId())
            .setParallel(localProject.isParallel());
        return builder.build();
    }

    protected String createDocumentForEntity(Project project) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);

            serializer.startTag("", "project");
            Date date = new Date();
            serializer.startTag("", "created-at").attribute("", "type", "datetime").text(mDateFormat.format(date)).endTag("", "created-at");
            Id contextId = findTracksIdByContextId(project.getDefaultContextId());
            if(contextId.isInitialised()) {
                serializer.startTag("", "default-context-id").attribute("", "type", "integer").text(contextId.toString()).endTag("", "default-context-id");
            }
            serializer.startTag("", "name").text(project.getName()).endTag("", "name");
            serializer.startTag("", "state").text(project.isArchived() ? "hidden": "active").endTag("", "state");
            serializer.startTag("", "updated-at").attribute("", "type", "datetime").text(mDateFormat.format(date)).endTag("", "updated-at");
            serializer.endTag("", "project");

            serializer.flush();
        } catch (IOException ignored) {
            Log.d(cTag, "Failed to serialize project", ignored);
        }


        return writer.toString();
    }

    protected Project parseSingleEntity(XmlPullParser parser) throws ParseException {
        final DateFormat format = mDateFormat;
        Builder builder = Project.newBuilder();
        Project project = null;
        
        try {
            int eventType = parser.getEventType();
            
            while (eventType != XmlPullParser.END_DOCUMENT && project == null) {
                String name = parser.getName();
                
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (name.equalsIgnoreCase("name")) {
                            builder.setName(parser.nextText());
                        } else if (name.equalsIgnoreCase("id")) {
                            Id tracksId = Id.create(Long.parseLong(parser.nextText()));
                            builder.setTracksId(tracksId);
                        } else if (name.equalsIgnoreCase("updated-at")) {
                            long modifiedDate = format.parse(parser.nextText()).getTime();
                            builder.setModifiedDate(modifiedDate);
                        } else if (name.equalsIgnoreCase("default-context-trackId")) {
                            Id tracksId = Id.create(Long.parseLong(parser.nextText()));
                            Id defaultContextId = findContextIdByTracksId(tracksId);
                            if (defaultContextId.isInitialised()) {
                                builder.setDefaultContextId(defaultContextId);
                            }
                        } else if (name.equalsIgnoreCase("state")) {
                            boolean archived = !parser.nextText().equalsIgnoreCase("active");
                            builder.setArchived(archived);
                        }
                        break;
                        
                    case XmlPullParser.END_TAG:
                        if (name.equalsIgnoreCase("project")) {
                            project = builder.build();
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
        return project;
    }

    @Override
    protected String createEntityUrl(Project project) {
        return mTracksUrl+ "/projects/" + project.getTracksId().getId() + ".xml";
    }

    @Override
    protected String endIndexTag() {
        return "projects";
    }

    @Override
    protected String entityIndexUrl() {
        return mTracksUrl+ "/projects.xml";
    }

}