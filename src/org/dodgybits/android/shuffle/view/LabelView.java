package org.dodgybits.android.shuffle.view;

import org.dodgybits.android.shuffle.util.DrawableUtils;
import org.dodgybits.android.shuffle.util.TextColours;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * A TextView with coloured text and a round edged coloured background.
 */
public class LabelView extends TextView {
	protected TextColours mTextColours;
	protected Drawable mIcon;
	protected int mTextColour;
	protected int mBgColour;
	
	public LabelView(Context context) {
		super(context);
		init(context);
	}
	
	@SuppressWarnings("unchecked")
    public LabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressWarnings("unchecked")
	public LabelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

    private void init(Context context) {
		mTextColours = TextColours.getInstance(context);
    }
    
    public void setColourIndex(int colourIndex) {
    	mTextColour = mTextColours.getTextColour(colourIndex);
    	mBgColour = mTextColours.getBackgroundColours(colourIndex);
		setTextColor(mTextColour);			
    	setBackgroundDrawable(DrawableUtils.createRoundedVerticalGradient(mBgColour));
    }

    public void setIcon(Drawable icon) {
    	mIcon = icon;
		setCompoundDrawablesWithIntrinsicBounds(mIcon, null, null, null);
    }
    
    
}
