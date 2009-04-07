package org.dodgybits.android.shuffle.view;

import org.dodgybits.android.shuffle.util.DrawableUtils;
import org.dodgybits.android.shuffle.util.TextColours;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
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
    	mBgColour = mTextColours.getBackgroundColour(colourIndex);
		setTextColor(mTextColour);
		GradientDrawable drawable = DrawableUtils.createGradient(mBgColour, Orientation.TOP_BOTTOM);
    	drawable.setCornerRadius(4.0f);
		//drawable.setAlpha(240);
    	setBackgroundDrawable(drawable);
    }

    public void setIcon(Drawable icon) {
    	mIcon = icon;
		setCompoundDrawablesWithIntrinsicBounds(mIcon, null, null, null);
    }
    
    
}
