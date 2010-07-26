package org.dodgybits.shuffle.android.synchronisation.tracks;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Project.Builder;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.util.DateUtils;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.Parser;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.ProjectParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

/**
 * @author Morten Nielsen
 */
public final class ProjectSynchronizer extends Synchronizer<Project> {
    private static final String cTag = "ProjectSynchronizer";
    
    private final String mTracksUrl;

	private Parser<Project> mParser;

    public ProjectSynchronizer(
            EntityPersister<Project> persister,
            TracksSynchronizer tracksSynchronizer, 
            WebClient client, 
            android.content.Context context,
            Analytics analytics,
            int basePercent,
            String tracksUrl) {
        super(persister, tracksSynchronizer, client, context, basePercent);
        mParser = new ProjectParser(this, analytics);
        mTracksUrl = tracksUrl;
    }
    
    @Override
    protected void verifyEntitiesForSynchronization(Map<Id, Project> localEntities) {
    }
    
    @Override
    protected EntityBuilder<Project> createBuilder() {
        return Project.newBuilder();
    }

    @Override
    protected String readingRemoteText() {
        return mContext.getString(R.string.readingRemoteProjects);
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
            .setHidden(remoteProject.getHidden())
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
            serializer.startTag("", "state").text(project.getHidden() ? "hidden": "active").endTag("", "state");
            serializer.startTag("", "updated-at").attribute("", "type", "datetime").text(now).endTag("", "updated-at");
            serializer.endTag("", "project");

            serializer.flush();
        } catch (IOException ignored) {
            Log.d(cTag, "Failed to serialize project", ignored);
        }


        return writer.toString();
    }

    @Override
    protected String createEntityUrl(Project project) {
        return mTracksUrl+ "/projects/" + project.getTracksId().getId() + ".xml";
    }


    @Override
    protected String entityIndexUrl() {
        return mTracksUrl+ "/projects.xml";
    }

	@Override
	protected Parser<Project> getEntityParser() {
		return mParser;
	}

}