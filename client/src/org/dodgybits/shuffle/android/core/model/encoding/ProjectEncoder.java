package org.dodgybits.shuffle.android.core.model.encoding;

import static android.provider.BaseColumns._ID;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Projects.DEFAULT_CONTEXT_ID;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Projects.MODIFIED_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Projects.NAME;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Projects.PARALLEL;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Projects.TRACKS_ID;

import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Project.Builder;

import android.os.Bundle;

public class ProjectEncoder extends AbstractEntityEncoder implements EntityEncoder<Project> {

    @Override
    public Bundle save(Project project) {
        Bundle icicle = new Bundle();
        putId(icicle, _ID, project.getLocalId());
        putId(icicle, TRACKS_ID, project.getTracksId());
        icicle.putLong(MODIFIED_DATE, project.getModifiedDate());

        putString(icicle, NAME, project.getName());
        putId(icicle, DEFAULT_CONTEXT_ID, project.getDefaultContextId());
        icicle.putBoolean(PARALLEL, project.isParallel());
        
        return icicle;
    }
    
    @Override
    public Project restore(Bundle icicle) {
        if (icicle == null) return null;

        Builder builder = Project.newBuilder();
        builder.setLocalId(getId(icicle, _ID));
        builder.setModifiedDate(icicle.getLong(MODIFIED_DATE, 0L));
        builder.setTracksId(getId(icicle, TRACKS_ID));

        builder.setName(getString(icicle, NAME));
        builder.setDefaultContextId(getId(icicle, DEFAULT_CONTEXT_ID));
        builder.setParallel(icicle.getBoolean(PARALLEL));
        
        return builder.build();
    }
    
}
