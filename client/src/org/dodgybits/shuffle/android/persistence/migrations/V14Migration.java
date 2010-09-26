package org.dodgybits.shuffle.android.persistence.migrations;

import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import android.database.sqlite.SQLiteDatabase;

public class V14Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {

        db.execSQL("ALTER TABLE " + TaskProvider.TASK_TABLE_NAME
                + " ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0;");
        db.execSQL("ALTER TABLE " + ContextProvider.CONTEXT_TABLE_NAME
                + " ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0;");
        db.execSQL("ALTER TABLE " + ProjectProvider.PROJECT_TABLE_NAME
                + " ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0;");

	}

}
