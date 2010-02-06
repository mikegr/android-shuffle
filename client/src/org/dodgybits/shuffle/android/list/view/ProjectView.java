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
import org.dodgybits.shuffle.android.core.model.Project;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

public class ProjectView extends ItemView<Project> {
	private TextView mName;
	private ImageView mParallelIcon;
	
	private SparseIntArray mTaskCountArray;
	private ForegroundColorSpan mSpan;
	
	public ProjectView(Context androidContext) {
		super(androidContext);
		
        LayoutInflater vi = (LayoutInflater)androidContext.
			getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(getViewResourceId(), this, true); 
		
		mName = (TextView) findViewById(R.id.name);
		mParallelIcon = (ImageView) findViewById(R.id.parallel_image);
		
        int colour = getResources().getColor(R.drawable.pale_blue);
        mSpan = new ForegroundColorSpan(colour);
		
	}
	
	protected int getViewResourceId() {
		return R.layout.list_project_view;
	}

	
	public void setTaskCountArray(SparseIntArray taskCountArray) {
		mTaskCountArray = taskCountArray;
	}
	
	@Override
	public void updateView(Project project) {
		if (mTaskCountArray != null) {
			Integer count = mTaskCountArray.get(project.id.intValue());
			if (count == null) count = 0;
			
            CharSequence label = project.name + "  (" + count + ")";
            SpannableString spannable = new SpannableString(label);
            spannable.setSpan(mSpan, project.name.length(),
                    label.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			mName.setText(spannable);
		} else {
			mName.setText(project.name);
		}
		
		if (mParallelIcon != null) {
    		if (project.isParallel) {
    		    mParallelIcon.setImageResource(R.drawable.parallel);
    		} else {
                mParallelIcon.setImageResource(R.drawable.sequence);
    		}
		}
		
	}

}
