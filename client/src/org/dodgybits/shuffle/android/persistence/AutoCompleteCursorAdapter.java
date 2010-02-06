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

package org.dodgybits.shuffle.android.persistence;

import java.util.Arrays;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

/**
 * A database cursor adaptor that can be used for an AutoCompleteTextField.
 * 
 */
public class AutoCompleteCursorAdapter extends SimpleCursorAdapter {
    private static final String cTag = "AutoCompleteCursorAdapter";

	
	private Context mContext;
	private String[] mProjection;
	private Uri mContentUri;
	
	public AutoCompleteCursorAdapter(Context context, Cursor c,	String[] from, Uri contentUri) {
		super(context, android.R.layout.simple_list_item_1, c, from, new int[] { android.R.id.text1 });
		mContext = context;
		mProjection = from;
		mContentUri = contentUri;
	}
	
	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (constraint == null || constraint.length() == 0) {
			return super.runQueryOnBackgroundThread(constraint);
		}
        StringBuilder buffer = null; 
        String[] args = null; 
        if (constraint != null) { 
            buffer = new StringBuilder(); 
            buffer.append(mProjection[0]); 
            buffer.append(" LIKE ?"); 
            args = new String[] { '%' + constraint.toString()  + '%'}; 
        } 
        String query = buffer.toString();
        Log.d(cTag, "Query '" + query + "' with params: " + Arrays.asList(args));
        return mContext.getContentResolver().query(mContentUri, mProjection, 
                query, args, null); 
	}

    @Override 
    public CharSequence convertToString(Cursor cursor) { 
    	// assuming name is first entry in cursor....
        return cursor.getString(0); 
    } 

}
