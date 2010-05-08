package org.dodgybits.shuffle.android.synchronisation.tracks;

import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryTracksSyncError;

import java.io.StringReader;
import java.text.ParseException;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.flurry.android.FlurryAgent;

/**
 * Base class for handling synchronization, template method object.
 *
 * @author Morten Nielsen
 */
public abstract class Synchronizer<Entity extends TracksEntity> {
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
        verifyLocalEntities(localEntities);
        mTracksSynchronizer.reportProgress(Progress.createProgress(mBasePercent,
                readingRemoteText()));
        TracksEntities tracksEntities = getTrackEntities();
        int startCounter = localEntities.size() + 1;
        int count = 0;
        for (Entity localEntity : localEntities.values()) {
            count++;
            int percent = mBasePercent
                    + Math.round(((count * 100) / startCounter) * 0.33f);
            mTracksSynchronizer.reportProgress(Progress.createProgress(percent,
                    processingText()));
            synchronizeSingle(tracksEntities, localEntity);
        }

        for (Entity remoteEntity : tracksEntities.getEntities().values()) {
            insertEntity(remoteEntity);
        }

        mTracksSynchronizer.reportProgress(Progress.createProgress(
                mBasePercent + 33, stageFinishedText()));
    }

    protected Id findProjectIdByTracksId(Id tracksId) {
        return findEntityLocalIdByTracksId(tracksId, ProjectProvider.Projects.CONTENT_URI);
    }

    protected Id findContextIdByTracksId(Id tracksId) {
        return findEntityLocalIdByTracksId(tracksId, ContextProvider.Contexts.CONTENT_URI);
    }
    
    protected Id findTracksIdByProjectId(Id projectId) {
        return findEntityTracksIdByLocalId(projectId, ProjectProvider.Projects.CONTENT_URI);
    }

    protected Id findTracksIdByContextId(Id contextId) {
        return findEntityTracksIdByLocalId(contextId, ContextProvider.Contexts.CONTENT_URI);
    }
    
    protected abstract void verifyLocalEntities(Map<Id, Entity> localEntities);

    protected abstract String readingRemoteText();

    protected abstract String processingText();

    protected abstract String readingLocalText();

    protected abstract String stageFinishedText();

    protected abstract String endIndexTag();

    protected abstract String entityIndexUrl();

    protected abstract Entity createMergedLocalEntity(Entity localEntity,
            Entity newEntity);
    
    protected abstract String createEntityUrl(Entity localEntity);

    protected abstract String createDocumentForEntity(Entity localEntity);

    protected abstract Entity parseSingleEntity(XmlPullParser parser)
        throws ParseException;
    
    protected abstract EntityBuilder<Entity> createBuilder();
    
    private TracksEntities getTrackEntities() throws WebClient.ApiException {
        Map<Id, Entity> entities = new HashMap<Id, Entity>();
        boolean errorFree = true;
        String tracksEntityXml;
        
        try {
            tracksEntityXml = mWebClient.getUrlContent(entityIndexUrl());
        } catch (WebClient.ApiException e) {
            Log.w(cTag, e);
            throw e;
        }
        
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(tracksEntityXml));

            int eventType = parser.getEventType();
            boolean done = false;
            
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                Entity entity = null;
                try {
                    entity = parseSingleEntity(parser);
                } catch (Exception e) {
                    logTracksError(e);
                    errorFree = false;
                }
                
                if (entity != null && entity.isInitialized()) {
                    entities.put(entity.getTracksId(), entity);
                }
                
                eventType = parser.getEventType();
                String name = parser.getName();
                if (eventType == XmlPullParser.END_TAG &&
                   name.equalsIgnoreCase(endIndexTag())) {
                   done = true;
                }
            }
        } catch (XmlPullParserException e) {
            logTracksError(e);
            errorFree = false;
        }
        
        return new TracksEntities(entities, errorFree);
    }
    
    private void logTracksError(Exception e) {
        Log.e(cTag, "Failed to parse " + endIndexTag() + " " + e.getMessage());
        FlurryAgent.onError(cFlurryTracksSyncError, e.getMessage(), getClass().getName());
    }

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

    private boolean deleteEntity(Entity entity)
    {
        return mPersister.delete(entity.getLocalId());
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
    
    private void synchronizeSingle(TracksEntities tracksEntities,
            Entity localEntity) {
        final Map<Id, Entity> remoteEntities = tracksEntities.getEntities();
        if (!localEntity.getTracksId().isInitialised()) {
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
            return;
        }
        Entity remoteEntity = remoteEntities.get(localEntity.getTracksId());
        if (remoteEntity != null) {
            handleRemoteEntity(localEntity, remoteEntity);
            remoteEntities.remove(remoteEntity.getTracksId());
        } else if (tracksEntities.isErrorFree()){
            // only delete entities if we didn't encounter errors parsing
            deleteEntity(localEntity);
        }
    }

    private void handleRemoteEntity(Entity localEntity, Entity remoteEntity) {
        final long remoteModified = remoteEntity.getModifiedDate();
        final long localModified = localEntity.getModifiedDate();
        
        if (remoteModified == localModified)
            return;

        if (remoteModified > localModified) {
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

    private class TracksEntities {
        private Map<Id, Entity> mEntities;
        private boolean mErrorFree;
        
        public TracksEntities(Map<Id, Entity> entities, boolean errorFree) {
            mEntities = entities;
            mErrorFree = errorFree;
        }
        
        public Map<Id, Entity> getEntities() {
            return mEntities;
        }
        
        public boolean isErrorFree() {
            return mErrorFree;
        }
        
    }
}
