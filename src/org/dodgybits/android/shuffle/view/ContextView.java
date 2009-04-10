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

package org.dodgybits.android.shuffle.view;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.util.DrawableUtils;
import org.dodgybits.android.shuffle.util.TextColours;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ContextView extends ItemView<Context> {
	protected TextColours mTextColours;
	private ImageView mIcon;
	private TextView mName;
	private View mColour;
	private SparseIntArray mTaskCountArray;

	public ContextView(android.content.Context androidContext) {
		super(androidContext);
		
        LayoutInflater vi = (LayoutInflater)androidContext.
        		getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(getViewResourceId(), this, true); 
        
        mColour = (View) findViewById(R.id.colour);
		mName = (TextView) findViewById(R.id.name);
		mIcon = (ImageView) findViewById(R.id.icon);
		mTextColours = TextColours.getInstance(androidContext);
	}
	
	protected int getViewResourceId() {
		return R.layout.context_view;
	}


	public void setTaskCountArray(SparseIntArray taskCountArray) {
		mTaskCountArray = taskCountArray;
	}
	
	@Override
	public void updateView(Context context) {
		// add context icon
		int iconResource = context.icon.largeIconId;
		if (iconResource > 0) {
			mIcon.setImageResource(iconResource);
			mIcon.setVisibility(View.VISIBLE);
		} else {
			mIcon.setVisibility(View.INVISIBLE);
		}
		if (mTaskCountArray != null) {
			Integer count = mTaskCountArray.get(context.id);
			if (count == null) count = 0;
			mName.setText(context.name + " (" + count + ")");
		} else {
			mName.setText(context.name);
		}
    	int textColour = mTextColours.getTextColour(context.colourIndex);
		mName.setTextColor(textColour);

		int bgColour = mTextColours.getBackgroundColour(context.colourIndex);
    	GradientDrawable drawable = DrawableUtils.createGradient(bgColour, Orientation.TOP_BOTTOM);
    	drawable.setCornerRadius(12.0f);
    	mColour.setBackgroundDrawable(drawable);
	}

}
