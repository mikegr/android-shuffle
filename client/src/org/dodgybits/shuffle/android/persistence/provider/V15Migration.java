package org.dodgybits.shuffle.android.persistence.provider;

import org.dodgybits.shuffle.android.persistence.migrations.Migration;

import android.database.sqlite.SQLiteDatabase;

public class V15Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {

        db.execSQL("ALTER TABLE " + TaskProvider.TASK_TABLE_NAME
                + " ADD COLUMN hidden INTEGER NOT NULL DEFAULT 0;");
        db.execSQL("ALTER TABLE " + ContextProvider.CONTEXT_TABLE_NAME
                + " ADD COLUMN hidden INTEGER NOT NULL DEFAULT 0;");
        db.execSQL("ALTER TABLE " + ProjectProvider.PROJECT_TABLE_NAME
                + " ADD COLUMN hidden INTEGER NOT NULL DEFAULT 0;");

	}

}
