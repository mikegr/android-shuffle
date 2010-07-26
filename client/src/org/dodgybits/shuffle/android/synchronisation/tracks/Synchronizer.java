package org.dodgybits.shuffle.android.synchronisation.tracks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.preference.view.Progress;
import org.dodgybits.shuffle.android.synchronisation.tracks.model.TracksEntity;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.IContextLookup;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.IProjectLookup;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.Parser;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * Base class for handling synchronization, template method object.
 *
 * @author Morten Nielsen
 */
public abstract class Synchronizer<Entity extends TracksEntity> implements IProjectLookup,IContextLookup {
    private static final String cTag = "Synchronizer";
    
    protected EntityPersister<Entity> mPersister;
    protected WebClient mWebClient;
    protected android.content.Context mContext;
    protected final TracksSynchronizer mTracksSynchronizer;
    
    private int mBasePercent;

    public Synchronizer(
            EntityPersister<Entity> persister,
            TracksSynchronizer tracksSynchronizer, 
            WebClient client,
            android.content.Context context,
            int basePercent) {
        mPersister = persister;
        mTracksSynchronizer = tracksSynchronizer;
        mWebClient = client;
        mContext = context;
        mBasePercent = basePercent;
    }
    
    public void synchronize() throws WebClient.ApiException {
        mTracksSynchronizer.reportProgress(Progress.createProgress(mBasePercent,
                readingLocalText()));
        
        Map<Id, Entity> localEntities = getShuffleEntities();
        verifyEntitiesForSynchronization(localEntities);
        
        mTracksSynchronizer.reportProgress(Progress.createProgress(mBasePercent,
                readingRemoteText()));
        
        TracksEntities<Entity> tracksEntities = getTrackEntities();
        mergeAlreadySynchronizedEntities(localEntities, tracksEntities);

        addNewEntitiesToShuffle(tracksEntities);

        mTracksSynchronizer.reportProgress(Progress.createProgress(
                mBasePercent + 33, stageFinishedText()));
    }

	private void mergeAlreadySynchronizedEntities(
			Map<Id, Entity> localEntities, TracksEntities<Entity> tracksEntities) {
		int startCounter = localEntities.size() + 1;
        int count = 0;
        for (Entity localEntity : localEntities.values()) {
            count++;
            mTracksSynchronizer.reportProgress(Progress.createProgress(calculatePercent(startCounter, count),
                    processingText()));

            mergeSingle(tracksEntities, localEntity);
        }
	}

	private int calculatePercent(int startCounter, int count) {
		int percent = mBasePercent
		        + Math.round(((count * 100) / startCounter) * 0.33f);
		return percent;
	}

	private void addNewEntitiesToShuffle(TracksEntities<Entity> tracksEntities) {
		for (Entity remoteEntity : tracksEntities.getEntities().values()) {
            insertEntity(remoteEntity);
        }
	}

    public Id findProjectIdByTracksId(Id tracksId) {
        return findEntityLocalIdByTracksId(tracksId, ProjectProvider.Projects.CONTENT_URI);
    }

    public Id findContextIdByTracksId(Id tracksId) {
        return findEntityLocalIdByTracksId(tracksId, ContextProvider.Contexts.CONTENT_URI);
    }
    
    protected Id findTracksIdByProjectId(Id projectId) {
        return findEntityTracksIdByLocalId(projectId, ProjectProvider.Projects.CONTENT_URI);
    }

    protected Id findTracksIdByContextId(Id contextId) {
        return findEntityTracksIdByLocalId(contextId, ContextProvider.Contexts.CONTENT_URI);
    }
    
    protected abstract void verifyEntitiesForSynchronization(Map<Id, Entity> localEntities);

    protected abstract String readingRemoteText();

    protected abstract String processingText();

    protected abstract String readingLocalText();

    protected abstract String stageFinishedText();

    protected abstract String entityIndexUrl();

    protected abstract Entity createMergedLocalEntity(Entity localEntity,
            Entity newEntity);
    
    protected abstract String createEntityUrl(Entity localEntity);

    protected abstract String createDocumentForEntity(Entity localEntity);
    
    protected abstract EntityBuilder<Entity> createBuilder();
    
    private TracksEntities<Entity> getTrackEntities() throws WebClient.ApiException {
        
        String tracksEntityXml;
        
        try {
            tracksEntityXml = mWebClient.getUrlContent(entityIndexUrl());
        } catch (WebClient.ApiException e) {
            Log.w(cTag, e);
            throw e;
        }
        
        
        return getEntityParser().parseDocument(tracksEntityXml);
    }

	    
    protected abstract Parser<Entity> getEntityParser();



    private Id findEntityLocalIdByTracksId(Id tracksId, Uri contentUri) {
        Id id = Id.NONE;

        Cursor cursor = mContext.getContentResolver().query(
                contentUri, 
                new String[] { BaseColumns._ID}, 
                "tracks_id = ?", 
                new String[] {tracksId.toString()}, 
                null);
        
        if (cursor.moveToFirst()) {
            id = Id.create(cursor.getLong(0));
        }
        cursor.close();
        return id;
    }

    private Id findEntityTracksIdByLocalId(Id localId, Uri contentUri) {
        Id id = Id.NONE;

        Cursor cursor = mContext.getContentResolver().query(
                contentUri, 
                new String[] { "tracks_id" }, 
                BaseColumns._ID + " = ?", 
                new String[] {localId.toString()}, 
                null);
        if (cursor.moveToFirst()) {
            id = Id.create(cursor.getLong(0));
        }
        cursor.close();
        return id;
    }

    private void insertEntity(Entity entity) {
        mPersister.insert(entity);
    }
    
    private void updateEntity(Entity entity) {
        mPersister.update(entity);
    }

    private boolean hideEntity(Entity entity)
    {
    	preHideEntity(entity);
        return mPersister.hide(entity.getLocalId());
    }
    
    protected  void preHideEntity(Entity entity) {
		
	}

    
	private Entity findEntityByLocalName(Collection<Entity> remoteEntities,
            Entity localEntity) {
        Entity foundEntity = null;
        for (Entity entity : remoteEntities)
            if (entity.getLocalName().equals(localEntity.getLocalName())) {
                foundEntity = entity;
            }
        return foundEntity;
    }
    
    private void mergeSingle(TracksEntities<Entity> tracksEntities,
            Entity localEntity) {
        final Map<Id, Entity> remoteEntities = tracksEntities.getEntities();
        if (!localEntity.getTracksId().isInitialised()) {
            handleLocalEntityNotYetInTracks(localEntity, remoteEntities);
            return;
        }
        Entity remoteEntity = remoteEntities.get(localEntity.getTracksId());
        if (remoteEntity != null) {
            mergeLocalAndRemoteEntityBasedOnModifiedDate(localEntity, remoteEntity);
            remoteEntities.remove(remoteEntity.getTracksId());
        } else if (tracksEntities.isErrorFree()){
            // only delete entities if we didn't encounter errors parsing
            hideEntity(localEntity);
        }
    }

	private void handleLocalEntityNotYetInTracks(Entity localEntity,
			final Map<Id, Entity> remoteEntities) {
		Entity newEntity = findEntityByLocalName(remoteEntities.values(),
		        localEntity);
		
		if (newEntity != null) {
		    remoteEntities.remove(newEntity.getTracksId());
		} else {
		    newEntity = createEntityInTracks(localEntity);
		}
		
		if (newEntity != null) {
		    updateEntity(createMergedLocalEntity(localEntity, newEntity));

		}
	}

    private void mergeLocalAndRemoteEntityBasedOnModifiedDate(Entity localEntity, Entity remoteEntity) {
        final long remoteModified = remoteEntity.getModifiedDate();
        final long localModified = localEntity.getModifiedDate();
        
        if (remoteModified == localModified && remoteEntity.getHidden() == localEntity.getHidden())
            return;

        if (remoteModified >= localModified) {
            updateEntity(createMergedLocalEntity(localEntity, remoteEntity));
        } else {
            updateTracks(localEntity);
        }

    }

    private void updateTracks(Entity localEntity) {
        String document = createDocumentForEntity(localEntity);
        try {
            mWebClient.putContentToUrl(createEntityUrl(localEntity), document);
        } catch (WebClient.ApiException e) {
            Log.w(cTag, "Failed to update entity in tracks " + localEntity + ":" + e.getMessage(), e);
        }
    }

    private Entity createEntityInTracks(Entity entity) {
        String document = createDocumentForEntity(entity);
        try {
            String location = mWebClient.postContentToUrl(entityIndexUrl(),
                    document);
            if (!TextUtils.isEmpty(location.trim())) {
                Id id = parseIdFromLocation(location);
                EntityBuilder<Entity> builder = createBuilder();
                builder.mergeFrom(entity);
                builder.setTracksId(id);
                entity = builder.build();
            }
        } catch (WebClient.ApiException e) {
            Log.w(cTag, "Failed to create entity in tracks " + entity + ":" + e.getMessage(), e);
        }
        return entity;
    }

    private Id parseIdFromLocation(String location) {
        String[] parts = location.split("/");
        String document = parts[parts.length - 1];
        long id = Long.parseLong( document ); 
        return Id.create(id);
    }
    
    private Map<Id, Entity> getShuffleEntities() {
        Map<Id, Entity> list = new HashMap<Id, Entity>();

        Cursor cursor = mContext.getContentResolver().query(
                mPersister.getContentUri(), 
                mPersister.getFullProjection(),
                null, null, null);
        while (cursor.moveToNext()) {
            Entity entity = mPersister.read(cursor);
            list.put(entity.getLocalId(), entity);
        }
        cursor.close();
        return list;
    }

 
}

