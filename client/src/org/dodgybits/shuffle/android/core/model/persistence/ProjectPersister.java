package org.dodgybits.shuffle.android.core.model.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Project.Builder;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import roboguice.inject.ContentResolverProvider;
import roboguice.inject.ContextScoped;

import static org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider.ShuffleTable.ACTIVE;
import static org.dodgybits.shuffle.android.persistence.provider.ProjectProvider.Projects.*;

@ContextScoped
public class ProjectPersister extends AbstractEntityPersister<Project> {

    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int DEFAULT_CONTEXT_INDEX = 2;
    private static final int TRACKS_ID_INDEX = 3;
    private static final int MODIFIED_INDEX = 4;
    private static final int PARALLEL_INDEX = 5;
    private static final int ARCHIVED_INDEX = 6;
    private static final int DELETED_INDEX = 7;
    private static final int ACTIVE_INDEX = 8;

    @Inject
    public ProjectPersister(ContentResolverProvider provider, Analytics analytics) {
        super(provider.get(), analytics);
    }
    
    @Override
    public Project read(Cursor cursor) {
        Builder builder = Project.newBuilder();
        builder
            .setLocalId(readId(cursor, ID_INDEX))
            .setModifiedDate(cursor.getLong(MODIFIED_INDEX))
            .setTracksId(readId(cursor, TRACKS_ID_INDEX))
            .setName(readString(cursor, NAME_INDEX))
            .setDefaultContextId(readId(cursor, DEFAULT_CONTEXT_INDEX))
            .setParallel(readBoolean(cursor, PARALLEL_INDEX))
            .setArchived(readBoolean(cursor, ARCHIVED_INDEX))
            .setDeleted(readBoolean(cursor, DELETED_INDEX))
            .setActive(readBoolean(cursor, ACTIVE_INDEX));

        return builder.build();
    }
    
    @Override
    protected void writeContentValues(ContentValues values, Project project) {
        // never write id since it's auto generated
        values.put(MODIFIED_DATE, project.getModifiedDate());
        writeId(values, TRACKS_ID, project.getTracksId());
        writeString(values, NAME, project.getName());
        writeId(values, DEFAULT_CONTEXT_ID, project.getDefaultContextId());
        writeBoolean(values, PARALLEL, project.isParallel());
        writeBoolean(values, ARCHIVED, project.isArchived());
        writeBoolean(values, DELETED, project.isDeleted());
        writeBoolean(values, ACTIVE, project.isActive());
    }
    
    @Override
    protected String getEntityName() {
        return "project";
    }
    
    @Override
    public Uri getContentUri() {
        return ProjectProvider.Projects.CONTENT_URI;
    }
    
    @Override
    public String[] getFullProjection() {
        return ProjectProvider.Projects.FULL_PROJECTION;
    }
    
}
