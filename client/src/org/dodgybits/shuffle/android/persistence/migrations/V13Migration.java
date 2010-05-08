package org.dodgybits.shuffle.android.persistence.migrations;


import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;

import android.database.sqlite.SQLiteDatabase;

public class V13Migration extends AbstractMigration {

	@Override
	public void migrate(SQLiteDatabase db) {
		 // Shuffle v1.4.0
        db.execSQL("ALTER TABLE " + ProjectProvider.PROJECT_TABLE_NAME
                + " ADD COLUMN parallel INTEGER NOT NULL DEFAULT 0;");
	}

}
