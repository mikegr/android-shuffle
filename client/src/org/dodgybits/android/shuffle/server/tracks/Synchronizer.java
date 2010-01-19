package org.dodgybits.android.shuffle.server.tracks;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import org.dodgybits.android.shuffle.model.TracksCompatible;
import org.dodgybits.android.shuffle.service.Progress;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for handling synchronization, template method object.
 *
 * @author Morten Nielsen
 */
public abstract class Synchronizer<Entity extends TracksCompatible> {
    private static final String cTag = "Synchronizer";
    
    protected ContentResolver contentResolver;
    protected Resources resources;
    protected WebClient client;
    protected android.content.Context activity;
    protected final TracksSynchronizer tracksSynchronizer;
    protected SimpleDateFormat simpleDateFormat;
    private int basePercent;

    public Synchronizer(ContentResolver contentResolver,
            TracksSynchronizer tracksSynchronizer, WebClient client,
            Resources resources, android.content.Context activity,
            int basePercent) {
        this.contentResolver = contentResolver;
        this.tracksSynchronizer = tracksSynchronizer;
        this.client = client;
        this.resources = resources;
        this.activity = activity;
        this.basePercent = basePercent;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    }

    public void synchronize() throws WebClient.ApiException {
        tracksSynchronizer.reportProgress(Progress.createProgress(basePercent,
                readingLocalText()));
        Map<Long, Entity> localEntities = getShuffleEntities(contentResolver,
                resources);
        verifyLocalEntities(localEntities);
        tracksSynchronizer.reportProgress(Progress.createProgress(basePercent,
                readingRemoteText()));
        Map<Long, Entity> remoteEntities =
                getTrackEntities();
        int startCounter = localEntities.size() + 1;
        int count = 0;
        for (Entity localEntity : localEntities.values()) {
            count++;
            int percent = basePercent
                    + Math.round(((count * 100) / startCounter) * 0.33f);
            tracksSynchronizer.reportProgress(Progress.createProgress(percent,
                    processingText()));
            synchronizeSingle(remoteEntities, localEntity);

        }

        for (Entity remoteEntity : remoteEntities.values()) {
            saveLocalEntityFromRemote(remoteEntity);
        }

        tracksSynchronizer.reportProgress(Progress.createProgress(
                basePercent + 33, stageFinishedText()));
    }

    protected abstract void verifyLocalEntities(Map<Long, Entity> localEntities);

    protected abstract String readingRemoteText();

    protected abstract String processingText();

    protected abstract String readingLocalText();

    protected abstract String stageFinishedText();

    protected abstract void saveLocalEntityFromRemote(Entity remoteEntity);

    protected abstract String endIndexTag();

    protected abstract String entityIndexUrl();

    protected abstract boolean removeLocalEntity(Entity entity);

    protected abstract void saveLocalEntity(Entity entity);

    protected abstract Map<Long, Entity> getShuffleEntities(
            ContentResolver contentResolver, Resources resources);
    
    protected abstract Entity createMergedLocalEntity(Entity localEntity,
            Entity newEntity);
    
    protected abstract String createEntityUrl(Entity localEntity);

    protected abstract String createDocumentForEntity(Entity localEntity);

    protected abstract Entity parseSingleEntity(XmlPullParser parser)
        throws ParseException;
    
    protected Map<Long, Entity> getTrackEntities() throws WebClient.ApiException {
        Map<Long, Entity> entities = new HashMap<Long, Entity>();
        String tracksEntityXml;
        try {
            tracksEntityXml = client.getUrlContent(entityIndexUrl());
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
                Entity entity = parseSingleEntity(parser);
                if (null != entity)
                    entities.put(entity.getTracksId(), entity);
                eventType = parser.getEventType();
                String name = parser.getName();
                switch (eventType) {
                case XmlPullParser.END_TAG:
                    if (name.equalsIgnoreCase(endIndexTag())) {
                        done = true;
                    }
                    break;
                }
            }
        } catch (ParseException e) {
            Log.w(cTag, e);
        } catch (XmlPullParserException e) {
            Log.w(cTag, e);
        }
        return entities;
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
    
    private void synchronizeSingle(Map<Long, Entity> remoteEntities,
            Entity localEntity) {
        if (localEntity.getTracksId() == null) {

            Entity newEntity = findEntityByLocalName(remoteEntities.values(),
                    localEntity);
            if (newEntity != null)
                remoteEntities.remove(newEntity.getTracksId());
            else
                newEntity = createEntityInTracks(localEntity);

            if (newEntity != null) {
                saveLocalEntity(createMergedLocalEntity(localEntity, newEntity));

            }
            return;
        }
        Entity remoteEntity = remoteEntities.get(localEntity.getTracksId());
        if (remoteEntity != null) {
            handleRemoteEntity(localEntity, remoteEntity);
            remoteEntities.remove(remoteEntity.getTracksId());
        } else {
            removeLocalEntity(localEntity);
        }
    }

    private void handleRemoteEntity(Entity localEntity, Entity remoteEntity) {

        if (remoteEntity.getModified() == localEntity.getModified())
            return;

        if (remoteEntity.getModified() > localEntity
                .getModified()) {
            saveLocalEntity(createMergedLocalEntity(localEntity, remoteEntity));
        } else {
            updateTracks(localEntity);
        }

    }

    private void updateTracks(Entity localEntity) {
        String document = createDocumentForEntity(localEntity);
        try {
            client.putContentToUrl(createEntityUrl(localEntity), document);
        } catch (WebClient.ApiException ignored) {
            Log.w(cTag, ignored);
        }
    }

    private Entity createEntityInTracks(Entity entity) {
        String document = createDocumentForEntity(entity);
        try {
            String location = client.postContentToUrl(entityIndexUrl(),
                    document);
            if (!TextUtils.isEmpty(location.trim())) {
                Long id = parseIdFromLocation(location);
                entity.setTracksId(id);
            }
        } catch (WebClient.ApiException ignored) {
            Log.w(cTag, ignored);
        }
        return entity;
    }

    private Long parseIdFromLocation(String location) {
        String[] parts = location.split("/");
        String document = parts[parts.length - 1];
        return Long.parseLong( document );
    }

}
