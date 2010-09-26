package org.dodgybits.shuffle.android.core.model.persistence;

import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.Contexts.COLOUR;
import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.Contexts.DELETED;
import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.Contexts.ICON;
import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.Contexts.MODIFIED_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.Contexts.NAME;
import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.Contexts.TRACKS_ID;

import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Context.Builder;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;

import roboguice.inject.ContentResolverProvider;
import roboguice.inject.ContextScoped;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.inject.Inject;

@ContextScoped
public class ContextPersister extends AbstractEntityPersister<Context> {

    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int COLOUR_INDEX = 2;
    private static final int ICON_INDEX = 3;
    private static final int TRACKS_ID_INDEX = 4;
    private static final int MODIFIED_INDEX = 5;
    private static final int DELETED_INDEX = 6;
    
    @Inject
    public ContextPersister(ContentResolverProvider provider, Analytics analytics) {
        super(provider.get(), analytics);
    }

    @Override
    public Context read(Cursor cursor) {
        Builder builder = Context.newBuilder();
        builder
            .setLocalId(readId(cursor, ID_INDEX))
            .setModifiedDate(cursor.getLong(MODIFIED_INDEX))
            .setTracksId(readId(cursor, TRACKS_ID_INDEX))
            .setName(readString(cursor, NAME_INDEX))
            .setColourIndex(cursor.getInt(COLOUR_INDEX))
            .setIconName(readString(cursor, ICON_INDEX))
            .setDeleted(readBoolean(cursor, DELETED_INDEX));
        return builder.build();
    }

    @Override
    protected void writeContentValues(ContentValues values, Context context) {
        // never write id since it's auto generated
        values.put(MODIFIED_DATE, context.getModifiedDate());
        writeId(values, TRACKS_ID, context.getTracksId());
        writeString(values, NAME, context.getName());
        values.put(COLOUR, context.getColourIndex());
        writeString(values, ICON, context.getIconName());
        writeBoolean(values, DELETED, context.isDeleted());
    }
    
    @Override
    protected String getEntityName() {
        return "context";
    }
    
    @Override
    public Uri getContentUri() {
        return ContextProvider.Contexts.CONTENT_URI;
    }

    @Override
    public String[] getFullProjection() {
        return ContextProvider.Contexts.FULL_PROJECTION;
    }
    
    
    
}
