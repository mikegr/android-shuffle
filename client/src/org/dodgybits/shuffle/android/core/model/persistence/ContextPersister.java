package org.dodgybits.shuffle.android.core.model.persistence;

import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Contexts.COLOUR;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Contexts.ICON;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Contexts.MODIFIED_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Contexts.NAME;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Contexts.TRACKS_ID;

import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Context.Builder;

import android.content.ContentValues;
import android.database.Cursor;

public class ContextPersister extends AbstractEntityPersister implements EntityPersister<Context> {

    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int COLOUR_INDEX = 2;
    private static final int ICON_INDEX = 3;
    private static final int TRACKS_ID_INDEX = 4;
    private static final int MODIFIED_INDEX = 5;
    
    @Override
    public Context read(Cursor cursor) {
        Builder builder = Context.newBuilder();
        builder
            .setLocalId(readId(cursor, ID_INDEX))
            .setModifiedDate(cursor.getLong(MODIFIED_INDEX))
            .setTracksId(readId(cursor, TRACKS_ID_INDEX))
            .setName(readString(cursor, NAME_INDEX))
            .setColourIndex(cursor.getInt(COLOUR_INDEX))
            .setIconName(readString(cursor, ICON_INDEX));
        return builder.build();
    }

    @Override
    public void write(ContentValues values, Context context) {
        // never write id since it's auto generated
        values.put(MODIFIED_DATE, context.getModifiedDate());
        writeId(values, TRACKS_ID, context.getTracksId());
        writeString(values, NAME, context.getName());
        values.put(COLOUR, context.getColourIndex());
        writeString(values, ICON, context.getIconName());
    }

    
}
