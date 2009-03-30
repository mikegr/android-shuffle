package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.State;
import org.dodgybits.android.shuffle.model.Context.Icon;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.view.LabelView;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ContextEditorActivity extends AbstractEditorActivity<Context> {

    private static final String cTag = "ContextEditorActivity";
   
    private static final int COLOUR_PICKER = 0;
    private static final int ICON_PICKER = 1;
    
    private int mColourIndex;
    private Icon mIcon;
    
    private EditText mNameWidget;
    private LabelView mColourWidget;
    private Button mChangeColourButton;
    private ImageView mIconWidget;
    private TextView mIconNoneWidget;
    private Button mSetIconButton;
    private Button mClearIconButton;
        
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        // The text view for our context description, identified by its ID in the XML file.
        mNameWidget = (EditText) findViewById(R.id.name);

        mColourWidget = (LabelView) findViewById(R.id.colour_display);
        mColourIndex = -1;
        
        mIconWidget = (ImageView) findViewById(R.id.icon_display);
        mIconNoneWidget = (TextView) findViewById(R.id.icon_none);
        mIcon = Icon.NONE;
        
        mChangeColourButton = (Button) findViewById(R.id.colour_change_button);
        mChangeColourButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	// Launch activity to pick colour
            	Intent intent = new Intent(Intent.ACTION_PICK);
            	intent.setType(ColourPickerActivity.TYPE);
            	startActivityForResult(intent, COLOUR_PICKER);
            }
        });
        mSetIconButton = (Button) findViewById(R.id.icon_set_button);
        mSetIconButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	// Launch activity to pick icon
            	Intent intent = new Intent(Intent.ACTION_PICK);
            	intent.setType(IconPickerActivity.TYPE);
            	startActivityForResult(intent, ICON_PICKER);
            }
        });
        mClearIconButton = (Button) findViewById(R.id.icon_clear_button);
        mClearIconButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	mIcon = Icon.NONE;
            	displayIcon();
            }
        });        
        
        // Get the context!
        mCursor = managedQuery(mUri, Shuffle.Contexts.cFullProjection, null, null, null);
    }
    
    /**
     * @return id of layout for this view
     */
    protected int getContentViewResId() {
    	return R.layout.context_editor;
    }

    protected Context restoreItem(Bundle icicle) {
    	return BindingUtils.restoreContext(icicle,getResources());
    }
    
    protected void saveItem(Bundle outState, Context item) {
    	BindingUtils.saveContext(outState, item);
    }
    
    @Override
    protected void onResume() {
        Log.d(cTag, "onResume+");
        super.onResume();

        // If we didn't have any trouble retrieving the data, it is now
        // time to get at the stuff.
        if (mCursor != null) {
            // Make sure we are at the one and only row in the cursor.
            mCursor.moveToFirst();

            // Modify our overall title depending on the mode we are running in.
            if (mState == State.STATE_EDIT) {
                setTitle(R.string.title_edit_context);
            } else if (mState == State.STATE_INSERT) {
                setTitle(R.string.title_new_context);
            }

            // This is a little tricky: we may be resumed after previously being
            // paused/stopped.  We want to put the new text in the text view,
            // but leave the user where they were (retain the cursor position
            // etc).  This version of setText does that for us.
            Context context = BindingUtils.readContext(mCursor,getResources());
            mNameWidget.setTextKeepState(context.name);
            
            if (mColourIndex == -1) {
            	// if colour already set, use that (e.g. after use picks a new one)
            	mColourIndex = context.colourIndex;
            }
            displayColour();
            
            if (mIcon == Icon.NONE) {
            	// if icon already set, use that (e.g. after user picks a new one)
                mIcon = context.icon;
            }
        	displayIcon();

            // If we hadn't previously retrieved the original context, do so
            // now.  This allows the user to revert their changes.
            if (mOriginalItem == null) {
            	mOriginalItem = context;
            }
        } else {
            setTitle(getText(R.string.error_title));
            mNameWidget.setText(getText(R.string.error_message));
        }
        
        // select the description
        mNameWidget.selectAll();
        
    }
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
    	Log.d(cTag, "Got resultCode " + resultCode + " with data " + data);		
    	switch (requestCode) {
    	case COLOUR_PICKER:
        	if (resultCode == Activity.RESULT_OK) {
    			if (data != null) {
    				mColourIndex = Integer.parseInt(data.getStringExtra("colour"));
    				displayIcon();
    			}
    		}
    		break;
    	case ICON_PICKER:
        	if (resultCode == Activity.RESULT_OK) {
    			if (data != null) {
    				String iconName = data.getStringExtra("iconName");
    				mIcon = Icon.createIcon(iconName, getResources());
    				displayIcon();
    			}
    		}
    		break;
    		default:
    			Log.e(cTag, "Unknown requestCode: " + requestCode);
    	}
	}
    
	private void displayColour() {
		mColourWidget.setColourIndex(mColourIndex);
		mColourWidget.setVisibility(View.VISIBLE);
    }


	private void displayIcon() {
    	if (mIcon == Icon.NONE) {
    		mIconNoneWidget.setVisibility(View.VISIBLE);
    		mIconWidget.setVisibility(View.GONE);
    		mClearIconButton.setEnabled(false);
    	} else {
    		mIconNoneWidget.setVisibility(View.GONE);
    		mIconWidget.setImageResource(mIcon.largeIconId);
    		mIconWidget.setVisibility(View.VISIBLE);
    		mClearIconButton.setEnabled(true);
    	}
    }


    @Override
    protected void onPause() {
        Log.d(cTag, "onPause+");
        super.onPause();

        // The user is going somewhere else, so make sure their current
        // changes are safely saved away in the provider.  We don't need
        // to do this if only viewing.
        if (mCursor != null) {
            String description = mNameWidget.getText().toString();
            int length = description.length();

            // If this activity is finished, and there is no text, then we
            // do something a little special: simply delete the context entry.
            // Note that we do this both for editing and inserting...  it
            // would be reasonable to only do it when inserting.
            if (isFinishing() && (length == 0)) {
                setResult(RESULT_CANCELED);
                deleteItem();

            // Get out updates into the provider.
            } else {
            	String name = mNameWidget.getText().toString();
            	Context context  = new Context(name, mColourIndex, mIcon);
                ContentValues values = new ContentValues();
            	writeItem(values, context);

                // Commit all of our changes to persistent storage. When the update completes
                // the content provider will notify the cursor of the change, which will
                // cause the UI to be updated.
                getContentResolver().update(mUri, values, null, null);    	
            }
        }
    }
    
    protected void writeItem(ContentValues values, Context context) {
    	BindingUtils.writeContext(values, context);
    }

    /**
     * Take care of deleting a context.  Simply deletes the entry.
     */
    protected void deleteItem() {
    	super.deleteItem();
        mNameWidget.setText("");
        mColourWidget.setText("");
    }
    
}
