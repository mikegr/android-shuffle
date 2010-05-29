package org.dodgybits.shuffle.android.persistence.migrations;


import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import android.database.sqlite.SQLiteDatabase;

public class V12Migration extends AbstractMigration {

	@Override
	public void migrate(SQLiteDatabase db) {
		 // Shuffle v1.2.0
        db.execSQL("ALTER TABLE " + TaskProvider.TASK_TABLE_NAME
				+ " ADD COLUMN tracks_id INTEGER;");
        db.execSQL("ALTER TABLE " + ContextProvider.CONTEXT_TABLE_NAME
				+ " ADD COLUMN tracks_id INTEGER;");
        db.execSQL("ALTER TABLE " + ContextProvider.CONTEXT_TABLE_NAME
				+ " ADD COLUMN modified INTEGER;");
        db.execSQL("ALTER TABLE " + ProjectProvider.PROJECT_TABLE_NAME
				+ " ADD COLUMN tracks_id INTEGER;");
        db.execSQL("ALTER TABLE " + ProjectProvider.PROJECT_TABLE_NAME
				+ " ADD COLUMN modified INTEGER;");
        
	}

}
