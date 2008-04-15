package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.util.TextColours;
import org.dodgybits.android.shuffle.view.LabelView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout.Alignment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class ColourPickerActivity extends Activity implements
		OnItemClickListener {

	@SuppressWarnings("unused")
	private static final String cTag = "ColourPicker";

	public static final String TYPE = "vnd.android.cursor.dir/vnd.dodgybits.colours";

	private GridView mGrid;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.colour_picker);
		mGrid = (GridView) findViewById(R.id.colourGrid);
		mGrid.setAdapter(new IconAdapter(this));
		mGrid.setOnItemClickListener(this);
	}

	@SuppressWarnings("unchecked")
	public void onItemClick(AdapterView parent, View v, int position, long id) {
		setResult(Activity.RESULT_OK, String.valueOf(position));
		finish();
	}

	public class IconAdapter extends BaseAdapter {
		private TextColours textColours;
		
		public IconAdapter(Context context) {
			textColours = TextColours.getInstance(context);
		}


		public View getView(int position, View convertView, ViewGroup parent) {
			LabelView view;
			if (convertView instanceof LabelView) {
				view = (LabelView)convertView;
			} else {
				view = new LabelView(ColourPickerActivity.this);
				view.setText("Abc");
				view.setAlignment(Alignment.ALIGN_CENTER);
			}
			view.setColourIndex(position, false);
			return view;
		}

		public final int getCount() {
			return textColours.getNumColours();
		}

		public final Object getItem(int position) {
			return textColours.getTextColour(position);
		}

		public final long getItemId(int position) {
			return position;
		}
	}

}
