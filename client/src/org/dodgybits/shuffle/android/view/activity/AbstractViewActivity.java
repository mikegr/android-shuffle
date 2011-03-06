/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.shuffle.android.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledActivity;
import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * A generic activity for editing an item in a database. This can be used either
 * to simply view an item (Intent.VIEW_ACTION), view and edit an item
 * (Intent.EDIT_ACTION), or create a new item (Intent.INSERT_ACTION).
 */
public abstract class AbstractViewActivity<E extends Entity> extends
        FlurryEnabledActivity implements View.OnClickListener {

    private @InjectView(R.id.edit_button) Button mEditButton;

	protected Uri mUri;
    protected Cursor mCursor;
    protected E mOriginalItem;

	@Override
	protected void onCreate(Bundle icicle) {
		Ln.d("onCreate+");
		super.onCreate(icicle);

        mUri = getIntent().getData();

		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		setContentView(getContentViewResId());

        Drawable icon = getResources().getDrawable(R.drawable.ic_menu_edit);
        icon.setBounds(0, 0, 36, 36);
        mEditButton.setCompoundDrawables(icon, null, null, null);
        mEditButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuUtils.addPrefsHelpMenuItems(this, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (MenuUtils.checkCommonItemsSelected(item, this, MenuUtils.INBOX_ID)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.edit_button:
			doEditAction();
			break;
        }
	}

	protected void doEditAction() {
        Intent editIntent = new Intent(Intent.ACTION_EDIT, mUri);
        startActivity(editIntent);
        finish();
	}


	/**
	 * @return id of layout for this view
	 */
	abstract protected int getContentViewResId();

	abstract protected void updateUIFromItem(E item);

	abstract protected EntityPersister<E> getPersister();

}
