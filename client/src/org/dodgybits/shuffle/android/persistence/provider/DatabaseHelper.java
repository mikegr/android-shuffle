/**
 * 
 */
package org.dodgybits.shuffle.android.persistence.provider;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.dodgybits.shuffle.android.persistence.migrations.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseHelper extends SQLiteOpenHelper {
	private static final SortedMap<Integer, Migration> ALL_MIGRATIONS = new TreeMap<Integer, Migration>();

	static {
		ALL_MIGRATIONS.put(1, new V1Migration());
		ALL_MIGRATIONS.put(9, new V9Migration());
		ALL_MIGRATIONS.put(10, new V10Migration());
		ALL_MIGRATIONS.put(11, new V11Migration());
		ALL_MIGRATIONS.put(12, new V12Migration());
		ALL_MIGRATIONS.put(13, new V13Migration());
		ALL_MIGRATIONS.put(14, new V14Migration());
		ALL_MIGRATIONS.put(15, new V15Migration());
		ALL_MIGRATIONS.put(16, new V16Migration());
	}

    private Context mContext;

	DatabaseHelper(Context context) {
		super(context, AbstractCollectionProvider.cDatabaseName, null, AbstractCollectionProvider.cDatabaseVersion);
        mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(AbstractCollectionProvider.cTag, "Creating shuffle DB");
		executeMigrations(db, ALL_MIGRATIONS.keySet());
	}

	private void executeMigrations(SQLiteDatabase db,
			Set<Integer> migrationVersions) {
		for (Integer version : migrationVersions) {
			Log.i(AbstractCollectionProvider.cTag, "Migrating to version " + version);

			ALL_MIGRATIONS.get(version).migrate(db);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(AbstractCollectionProvider.cTag, 
		        "Upgrading database from version " + oldVersion + " to " + newVersion);
		SortedMap<Integer, Migration> migrations = ALL_MIGRATIONS.subMap(oldVersion, newVersion);
		executeMigrations(db, migrations.keySet());
	}

}