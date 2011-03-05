package org.dodgybits.shuffle.android.persistence.migrations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public interface Migration {
	public void migrate(SQLiteDatabase db);
}
