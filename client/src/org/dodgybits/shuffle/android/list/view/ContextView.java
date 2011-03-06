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

package org.dodgybits.shuffle.android.list.view;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.util.TextColours;
import org.dodgybits.shuffle.android.core.view.ContextIcon;
import org.dodgybits.shuffle.android.core.view.DrawableUtils;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ContextView extends ItemView<Context> {
	protected TextColours mTextColours;
	private ImageView mIcon;
	private TextView mName;
    private StatusView mStatus;
	private View mColour;
	private SparseIntArray mTaskCountArray;

	public ContextView(android.content.Context context) {
		super(context);
		init(context);
	}
	
    public ContextView(android.content.Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(android.content.Context androidContext) {
        LayoutInflater vi = (LayoutInflater)androidContext.
        		getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(getViewResourceId(), this, true); 
        
        mColour = (View) findViewById(R.id.colour);
		mName = (TextView) findViewById(R.id.name);
        mStatus = (StatusView)findViewById(R.id.status);
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
        updateIcon(context);
        updateNameLabel(context);
        updateStatus(context);
        updateBackground(context);
	}

    private void updateIcon(Context context) {
        ContextIcon icon = ContextIcon.createIcon(context.getIconName(), getResources());
        int iconResource = icon.largeIconId;
        if (iconResource > 0) {
            mIcon.setImageResource(iconResource);
            mIcon.setVisibility(View.VISIBLE);
        } else {
            mIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void updateNameLabel(Context context) {
        if (mTaskCountArray != null) {
            Integer count = mTaskCountArray.get((int)context.getLocalId().getId());
            if (count == null) count = 0;
            mName.setText(context.getName() + " (" + count + ")");
        } else {
            mName.setText(context.getName());
        }
        int textColour = mTextColours.getTextColour(context.getColourIndex());
        mName.setTextColor(textColour);
    }

    private void updateStatus(Context context) {
        if (mStatus != null) {
            mStatus.updateStatus(context.isActive(), context.isDeleted(), false);
        }
    }

    private void updateBackground(Context context) {
        int bgColour = mTextColours.getBackgroundColour(context.getColourIndex());
        GradientDrawable drawable = DrawableUtils.createGradient(bgColour, Orientation.TOP_BOTTOM);
        drawable.setCornerRadius(12.0f);
        mColour.setBackgroundDrawable(drawable);
    }


}
