package org.dodgybits.shuffle.android.persistence.migrations;

import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.CONTEXT_TABLE_NAME;
import android.database.sqlite.SQLiteDatabase;

public class V9Migration extends AbstractMigration {

	@Override
	public void migrate(SQLiteDatabase db) {
		createContextTable(db);
	}
	
	private void createContextTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + CONTEXT_TABLE_NAME);
		db.execSQL("CREATE TABLE " 
		        + CONTEXT_TABLE_NAME 
		        + " ("
				+ "_id INTEGER PRIMARY KEY," 
				+ "name TEXT,"
				+ "colour INTEGER," 
				+ "iconName TEXT," 
				+ "tracks_id INTEGER," 
				+ "modified INTEGER" 
				+ ");");
	}
}
