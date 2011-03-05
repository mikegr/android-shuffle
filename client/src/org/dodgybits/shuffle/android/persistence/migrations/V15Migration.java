package org.dodgybits.shuffle.android.persistence.migrations;

import android.content.Context;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import android.database.sqlite.SQLiteDatabase;

public class V15Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {

        db.execSQL("ALTER TABLE " + TaskProvider.TASK_TABLE_NAME
                + " ADD COLUMN active INTEGER NOT NULL DEFAULT 1;");
        db.execSQL("ALTER TABLE " + ContextProvider.CONTEXT_TABLE_NAME
                + " ADD COLUMN active INTEGER NOT NULL DEFAULT 1;");
        db.execSQL("ALTER TABLE " + ProjectProvider.PROJECT_TABLE_NAME
                + " ADD COLUMN active INTEGER NOT NULL DEFAULT 1;");

	}

}
