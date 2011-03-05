package org.dodgybits.shuffle.android.persistence.migrations;

import android.content.Context;
import org.dodgybits.shuffle.android.persistence.provider.ReminderProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import android.database.sqlite.SQLiteDatabase;

public class V11Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		// Shuffle v1.1.1 (2nd release)
		db.execSQL("ALTER TABLE " + TaskProvider.TASK_TABLE_NAME
				+ " ADD COLUMN start INTEGER;");
		db.execSQL("ALTER TABLE " + TaskProvider.TASK_TABLE_NAME
				+ " ADD COLUMN timezone TEXT;");
		db.execSQL("ALTER TABLE " + TaskProvider.TASK_TABLE_NAME
				+ " ADD COLUMN allDay INTEGER NOT NULL DEFAULT 0;");
		db.execSQL("ALTER TABLE " + TaskProvider.TASK_TABLE_NAME
				+ " ADD COLUMN hasAlarm INTEGER NOT NULL DEFAULT 0;");
		db.execSQL("ALTER TABLE " + TaskProvider.TASK_TABLE_NAME
				+ " ADD COLUMN calEventId INTEGER;");
		db.execSQL("UPDATE " + TaskProvider.TASK_TABLE_NAME + " SET start = due;");
		db.execSQL("UPDATE " + TaskProvider.TASK_TABLE_NAME + " SET allDay = 1 " +
				"WHERE due > 0;");
		
		createRemindersTable(db);
		createRemindersEventIdIndex(db);
		createTaskCleanupTrigger(db);
        // no break since we want it to fall through
	}
	private void createRemindersTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + ReminderProvider.cReminderTableName);
		db.execSQL("CREATE TABLE " + ReminderProvider.cReminderTableName + " (" + "_id INTEGER PRIMARY KEY,"
				+ "taskId INTEGER," + "minutes INTEGER,"
				+ "method INTEGER NOT NULL" + " DEFAULT "
				+ ReminderProvider.Reminders.METHOD_DEFAULT + ");");
	}

	private void createRemindersEventIdIndex(SQLiteDatabase db) {
        db.execSQL("DROP INDEX IF EXISTS remindersEventIdIndex");
		db.execSQL("CREATE INDEX remindersEventIdIndex ON " + ReminderProvider.cReminderTableName + " ("
				+ ReminderProvider.Reminders.TASK_ID + ");");
	}
	private void createTaskCleanupTrigger(SQLiteDatabase db) {
		// Trigger to remove data tied to a task when we delete that task
        db.execSQL("DROP TRIGGER IF EXISTS tasks_cleanup_delete");
		db.execSQL("CREATE TRIGGER tasks_cleanup_delete DELETE ON " + TaskProvider.TASK_TABLE_NAME
				+ " BEGIN "
				+ "DELETE FROM " + ReminderProvider.cReminderTableName + " WHERE taskId = old._id;"
				+ "END");
	}

}
