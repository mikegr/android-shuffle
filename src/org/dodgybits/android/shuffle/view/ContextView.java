package org.dodgybits.android.shuffle.view;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ContextView extends ItemView<Context> {
	private ImageView mIcon;
	private TextView mName;
	private SparseIntArray mTaskCountArray;

	public ContextView(android.content.Context androidContext) {
		super(androidContext);
		
        LayoutInflater vi = (LayoutInflater)androidContext.
        		getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(getViewResourceId(), this, true); 
        
		mName = (TextView) findViewById(R.id.name);
		mIcon = (ImageView) findViewById(R.id.icon);
	}
	
	protected int getViewResourceId() {
		return R.layout.context_view;
	}


	public void setTaskCountArray(SparseIntArray taskCountArray) {
		mTaskCountArray = taskCountArray;
	}
	
	@Override
	public void updateView(Context context) {
		mIcon.setPadding(0, 0, 0, 0);
		// add context icon
		Integer iconResource = context.iconResource;
		if (iconResource != null) {
			mIcon.setImageResource(iconResource);
			mIcon.setPadding(5, 0, 5, 0);
			mIcon.setVisibility(View.VISIBLE);
		} else {
			mIcon.setVisibility(View.GONE);
		}
		if (mTaskCountArray != null) {
			Integer count = mTaskCountArray.get(context.id);
			if (count == null) count = 0;
			mName.setText(context.name + " (" + count + ")");
		} else {
			mName.setText(context.name);
		}
	}

}
