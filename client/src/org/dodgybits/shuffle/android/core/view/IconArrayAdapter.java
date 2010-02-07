package org.dodgybits.shuffle.android.core.view;

import org.dodgybits.android.shuffle.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IconArrayAdapter extends ArrayAdapter<CharSequence> {

	private Integer[] mIconIds;
	
    public IconArrayAdapter(
    		Context context, int resource, 
    		int textViewResourceId, CharSequence[] objects,
    		Integer[] iconIds) {
        super(context, resource, textViewResourceId, objects);
        mIconIds = iconIds;
    }
		
    public View getView(int position, View convertView, ViewGroup parent) {
    	View view = super.getView(position, convertView, parent);
    	TextView nameView = (TextView) view.findViewById(R.id.name);
    	// don't use toString in order to preserve colour change
    	nameView.setText(getItem(position));
    	Integer iconId = null;
    	if (position < mIconIds.length) {
    		iconId = mIconIds[position];
    		if (iconId != null) {
    			nameView.setCompoundDrawablesWithIntrinsicBounds(
    					getContext().getResources().getDrawable(iconId), null, null, null);
    		}
    	}
    	return view;
    }
	
}


