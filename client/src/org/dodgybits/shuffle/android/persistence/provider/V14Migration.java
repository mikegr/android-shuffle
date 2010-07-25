package org.dodgybits.shuffle.android.persistence.provider;

import org.dodgybits.shuffle.android.persistence.migrations.Migration;

import android.database.sqlite.SQLiteDatabase;

public class V14Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		//v14 does nothing but allows the upgrading to catch up.

	}

}
