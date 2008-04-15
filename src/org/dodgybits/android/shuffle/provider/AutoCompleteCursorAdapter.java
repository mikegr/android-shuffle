package org.dodgybits.android.shuffle.provider;

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
	protected Cursor runQuery(CharSequence constraint) {
		if (constraint == null || constraint.length() == 0) {
			return super.runQuery(constraint);
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
    protected String convertToString(Cursor cursor) { 
    	// assuming name is first entry in cursor....
        return cursor.getString(0); 
    } 

}
