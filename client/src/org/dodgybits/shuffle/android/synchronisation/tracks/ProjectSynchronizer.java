package org.dodgybits.shuffle.android.synchronisation.tracks;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Map;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Project.Builder;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.util.DateUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

/**
 * @author Morten Nielsen
 */
public final class ProjectSynchronizer extends Synchronizer<Project> {
    private static final String cTag = "ProjectSynchronizer";
    
    private final String mTracksUrl;

    public ProjectSynchronizer(
            EntityPersister<Project> persister,
            TracksSynchronizer tracksSynchronizer, 
            WebClient client, 
            android.content.Context context,
            Analytics analytics,
            int basePercent,
            String tracksUrl) {
        super(persister, tracksSynchronizer, client, context, analytics, basePercent);

        mTracksUrl = tracksUrl;
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

    protected Project createMergedLocalEntity(Project localProject, Project remoteProject) {
        Builder builder = Project.newBuilder();
        builder.mergeFrom(localProject);
        builder
            .setName(remoteProject.getName())
            .setModifiedDate(remoteProject.getModifiedDate())
            .setArchived(remoteProject.isArchived())
            .setDefaultContextId(remoteProject.getDefaultContextId())
            .setTracksId(remoteProject.getTracksId());
        return builder.build();
    }

    protected String createDocumentForEntity(Project project) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);

            String now = DateUtils.formatIso8601Date(System.currentTimeMillis());
            serializer.startTag("", "project");
            serializer.startTag("", "created-at").attribute("", "type", "datetime").text(now).endTag("", "created-at");
            Id contextId = findTracksIdByContextId(project.getDefaultContextId());
            if(contextId.isInitialised()) {
                serializer.startTag("", "default-context-id").attribute("", "type", "integer").text(contextId.toString()).endTag("", "default-context-id");
            }
            serializer.startTag("", "name").text(project.getName()).endTag("", "name");
            serializer.startTag("", "state").text(project.isArchived() ? "hidden": "active").endTag("", "state");
            serializer.startTag("", "updated-at").attribute("", "type", "datetime").text(now).endTag("", "updated-at");
            serializer.endTag("", "project");

            serializer.flush();
        } catch (IOException ignored) {
            Log.d(cTag, "Failed to serialize project", ignored);
        }


        return writer.toString();
    }

    protected Project parseSingleEntity(XmlPullParser parser) throws ParseException {
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
                            String dateStr = parser.nextText();
                            long modifiedDate = DateUtils.parseIso8601Date(dateStr);
                            builder.setModifiedDate(modifiedDate);
                        } else if (name.equalsIgnoreCase("default-context-id")) {
                            String tokenValue = parser.nextText();
                            if (!TextUtils.isEmpty(tokenValue)) {
                                Id tracksId = Id.create(Long.parseLong(tokenValue));
                                Id defaultContextId = findContextIdByTracksId(tracksId);
                                if (defaultContextId.isInitialised()) {
                                    builder.setDefaultContextId(defaultContextId);
                                }
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
            throw new ParseException("Unable to parse project:" + e.getMessage(), 0);
        } catch (XmlPullParserException e) {
            throw new ParseException("Unable to parse project:" + e.getMessage(), 0);
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