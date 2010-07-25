/**
 * 
 */
package org.dodgybits.shuffle.android.persistence.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dodgybits.shuffle.android.persistence.migrations.Migration;
import org.dodgybits.shuffle.android.persistence.migrations.V10Migration;
import org.dodgybits.shuffle.android.persistence.migrations.V11Migration;
import org.dodgybits.shuffle.android.persistence.migrations.V12Migration;
import org.dodgybits.shuffle.android.persistence.migrations.V13Migration;
import org.dodgybits.shuffle.android.persistence.migrations.V1Migration;
import org.dodgybits.shuffle.android.persistence.migrations.V9Migration;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseHelper extends SQLiteOpenHelper {
	private static final Map<Integer, Migration> migrations = new HashMap<Integer, Migration>();

	static {
		migrations.put(1, new V1Migration());
		migrations.put(9, new V9Migration());
		migrations.put(10, new V10Migration());
		migrations.put(11, new V11Migration());
		migrations.put(12, new V12Migration());
		migrations.put(13, new V13Migration());
		// v14 does nothing but allows the upgrading to catch up.
		migrations.put(14, new V14Migration());
		migrations.put(15, new V15Migration());
	}

	DatabaseHelper(Context context) {
		super(context, AbstractCollectionProvider.cDatabaseName, null, AbstractCollectionProvider.cDatabaseVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(AbstractCollectionProvider.cTag, "Creating shuffle DB");
		executeMigrations(db, allMigrationVersionsSorted());
	}

	private void executeMigrations(SQLiteDatabase db,
			List<Integer> migrationVersions) {
		for (Integer version : migrationVersions) {
			Log.i(AbstractCollectionProvider.cTag, "Migrating to version " + version);
			try {
				migrations.get(version).migrate(db);
			} catch (Exception e) {
				Log.i(AbstractCollectionProvider.cTag, "Problem migrating to version " + version
						+ ":\n" + e);
				break;
			}
		}
	}

	private List<Integer> allMigrationVersionsSorted() {
		ArrayList<Integer> migrationVersions = new ArrayList<Integer>(migrations
				.keySet());
		Collections.sort(migrationVersions);
		return migrationVersions;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(AbstractCollectionProvider.cTag, "Upgrading database from version " + oldVersion
				+ " to " + newVersion);
		List<Integer> migrationVersions = allMigrationVersionsSorted();
		List<Integer> migrationsBetweenVersions = migrationVersions.subList(migrationVersions.indexOf(oldVersion), migrationVersions.indexOf(newVersion));
		executeMigrations(db, migrationsBetweenVersions);
	}

}