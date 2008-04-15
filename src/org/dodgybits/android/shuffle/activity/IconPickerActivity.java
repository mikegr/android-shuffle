package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class IconPickerActivity extends Activity implements OnItemClickListener {

    @SuppressWarnings("unused")
	private static final String cTag = "IconPickerActivity";

    public static final String TYPE = "vnd.android.cursor.dir/vnd.dodgybits.icons";

    private GridView mGrid;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.icon_picker);
        mGrid = (GridView) findViewById(R.id.iconGrid);
        mGrid.setAdapter(new IconAdapter(this));
        mGrid.setOnItemClickListener(this);
    }

    @SuppressWarnings("unchecked")
	public void onItemClick(AdapterView parent, View v, int position, long id) {
		setResult(Activity.RESULT_OK, String.valueOf(mGrid.getAdapter().getItem(position)));
		finish();
    }
    
    public class IconAdapter extends BaseAdapter {
        public IconAdapter(Context context) {
        	loadIcons();
        }
        
        private int[] mIconIds;
        
        private void loadIcons() {
        	mIconIds = new int[] {
    			android.R.drawable.contact_home_location,
    			android.R.drawable.contact_mobile_phone,
    			android.R.drawable.contact_telephone,
    			android.R.drawable.contact_work_location,    			
    			android.R.drawable.stat_sys_phone_call,
    			R.drawable.applications_accessories,
    			R.drawable.applications_development,
    			R.drawable.applications_games,
    			R.drawable.applications_graphics,
    			R.drawable.applications_internet,
    			R.drawable.applications_office,
    			R.drawable.computer,    			
    			R.drawable.emblem_favorite,    			
    			R.drawable.emblem_important,    			
    			R.drawable.go_home,    			
    			R.drawable.network_wireless,    			
    			R.drawable.office_calendar,    			
    			R.drawable.system_file_manager,    			
    			R.drawable.system_users,    			
    			R.drawable.utilities_terminal,    			
    			R.drawable.video_x_generic,    			
    			R.drawable.x_office_address_book,    			
        	};
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(IconPickerActivity.this);
            Integer iconId = mIconIds[position];
            i.setImageResource(iconId);
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //i.setLayoutParams(new Gallery.LayoutParams(32, 32));
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
