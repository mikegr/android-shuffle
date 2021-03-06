package org.dodgybits.shuffle.android.persistence.migrations;

import android.content.Context;
import org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class V1Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		createProjectTable(db);
		createTaskTable(db);
	}
	private void createProjectTable(SQLiteDatabase db) {
		Log.w(AbstractCollectionProvider.cTag, "Destroying all old data");
		db.execSQL("DROP TABLE IF EXISTS " + ProjectProvider.PROJECT_TABLE_NAME);
		db.execSQL("CREATE TABLE " 
		        + ProjectProvider.PROJECT_TABLE_NAME 
		        + " ("
				+ "_id INTEGER PRIMARY KEY," 
				+ "name TEXT,"
				+ "archived INTEGER," 
				+ "defaultContextId INTEGER" 
				+ ");");
	}

	private void createTaskTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TaskProvider.TASK_TABLE_NAME);
		db.execSQL("CREATE TABLE " 
		        + TaskProvider.TASK_TABLE_NAME 
		        + " ("
				+ "_id INTEGER PRIMARY KEY," 
				+ "description TEXT,"
				+ "details TEXT," 
				+ "contextId INTEGER,"
				+ "projectId INTEGER," 
				+ "created INTEGER,"
				+ "modified INTEGER," 
				+ "due INTEGER,"
				+ "displayOrder INTEGER," 
				+ "complete INTEGER" 
   				+ ");");
	}
}
