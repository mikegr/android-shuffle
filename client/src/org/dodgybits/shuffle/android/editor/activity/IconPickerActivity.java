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

package org.dodgybits.shuffle.android.editor.activity;

import org.dodgybits.android.shuffle.R;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class IconPickerActivity extends GuiceActivity implements OnItemClickListener {

    @SuppressWarnings("unused")
	private static final String cTag = "IconPickerActivity";

    public static final String TYPE = "vnd.android.cursor.dir/vnd.dodgybits.icons";

    @InjectView(R.id.iconGrid) GridView mGrid;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.icon_picker);
        mGrid.setAdapter(new IconAdapter(this));
        mGrid.setOnItemClickListener(this);
    }

    @SuppressWarnings("unchecked")
	public void onItemClick(AdapterView parent, View v, int position, long id) {
		Bundle bundle = new Bundle();
		int iconId = (Integer)mGrid.getAdapter().getItem(position);
		String iconName = getResources().getResourceEntryName(iconId);
	    bundle.putString("iconName", iconName);
	    Intent mIntent = new Intent();
	    mIntent.putExtras(bundle);
	    setResult(RESULT_OK, mIntent);
		finish();
    }
    
    public class IconAdapter extends BaseAdapter {
        public IconAdapter(Context context) {
        	loadIcons();
        }
        
        private int[] mIconIds;
        
        private void loadIcons() {
        	mIconIds = new int[] {
    			R.drawable.accessories_calculator,    			
    			R.drawable.accessories_text_editor,    			
    			R.drawable.applications_accessories,
    			R.drawable.applications_development,
    			R.drawable.applications_games,
    			R.drawable.applications_graphics,
    			R.drawable.applications_internet,
    			R.drawable.applications_office,
    			R.drawable.applications_system,    			
    			R.drawable.audio_x_generic,    			
    			R.drawable.camera_photo,    			
    			R.drawable.computer,    			
    			R.drawable.emblem_favorite,    			
    			R.drawable.emblem_important,    			
    			R.drawable.format_justify_fill,    			
    			R.drawable.go_home,    			
    			R.drawable.network_wireless,    			
    			R.drawable.office_calendar,    			
    			R.drawable.start_here,    			
    			R.drawable.system_file_manager,    			
    			R.drawable.system_search,    			
    			R.drawable.system_users,    			
    			R.drawable.utilities_terminal,    			
    			R.drawable.video_x_generic,    			
    			R.drawable.weather_showers_scattered,    			
    			R.drawable.x_office_address_book,    			
        	};
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(IconPickerActivity.this);
            Integer iconId = mIconIds[position];
            i.setImageResource(iconId);
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return i;
        }


        public final int getCount() {
            return mIconIds.length;
        }

        public final Object getItem(int position) {
            return mIconIds[position];
        }

        public final long getItemId(int position) {
            return position;
        }
    }

}
