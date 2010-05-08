package org.dodgybits.shuffle.android.persistence.migrations;

import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.cContextTableName;
import android.database.sqlite.SQLiteDatabase;

public class V9Migration extends AbstractMigration {

	@Override
	public void migrate(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE " + cContextTableName
				+ " RENAME TO contextOld");
		createContextTable(db);
		db.execSQL("INSERT INTO " + cContextTableName
				+ " (_id,name,colour)"
				+ " SELECT _id,name,colour FROM contextOld");
		db.execSQL("DROP TABLE contextOld");
		// no break since we want it to fall through
		
	}
	private void createContextTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + cContextTableName);
		db.execSQL("CREATE TABLE " 
		        + cContextTableName 
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
