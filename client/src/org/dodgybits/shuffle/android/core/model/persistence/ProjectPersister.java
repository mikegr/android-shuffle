package org.dodgybits.shuffle.android.core.model.persistence;

import static org.dodgybits.shuffle.android.persistence.provider.ProjectProvider.Projects.ARCHIVED;
import static org.dodgybits.shuffle.android.persistence.provider.ProjectProvider.Projects.DEFAULT_CONTEXT_ID;
import static org.dodgybits.shuffle.android.persistence.provider.ProjectProvider.Projects.MODIFIED_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.ProjectProvider.Projects.NAME;
import static org.dodgybits.shuffle.android.persistence.provider.ProjectProvider.Projects.PARALLEL;
import static org.dodgybits.shuffle.android.persistence.provider.ProjectProvider.Projects.TRACKS_ID;

import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Project.Builder;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class ProjectPersister extends AbstractEntityPersister<Project> {

    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int DEFAULT_CONTEXT_INDEX = 2;
    private static final int TRACKS_ID_INDEX = 3;
    private static final int MODIFIED_INDEX = 4;
    private static final int PARALLEL_INDEX = 5;
    private static final int ARCHIVED_INDEX = 6;
    
    public ProjectPersister(ContentResolver resolver) {
        super(resolver);
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
            .setArchived(readBoolean(cursor, ARCHIVED_INDEX));
        
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
        return ProjectProvider.Projects.cFullProjection;
    }
    
}
