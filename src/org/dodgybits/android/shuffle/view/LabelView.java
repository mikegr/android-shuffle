package org.dodgybits.android.shuffle.view;

import org.dodgybits.android.shuffle.util.TextColours;

import android.content.Context;
import android.graphics.Color;
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
    	mBgColour = mTextColours.getBackgroundColours(colourIndex);
		setTextColor(mTextColour);			
    	setBackgroundDrawable(createGradient(mBgColour));
    }

    public void setIcon(Drawable icon) {
    	mIcon = icon;
		setCompoundDrawablesWithIntrinsicBounds(mIcon, null, null, null);
    }
    
    private static Drawable createGradient(int colour) {
		int[] colours = new int[2];
		float[] hsv1 = new float[3];
		float[] hsv2 = new float[3];
		Color.colorToHSV(colour, hsv1);
		Color.colorToHSV(colour, hsv2);
		hsv1[2] *= 1.1;
		hsv2[2] *= 0.90;
		colours[0] = Color.HSVToColor(hsv1);
		colours[1] = Color.HSVToColor(hsv2);
    	GradientDrawable drawable = new GradientDrawable(Orientation.TOP_BOTTOM, colours);
    	drawable.setCornerRadius(4.0f);
		//drawable.setAlpha(240);
    	return drawable;
    	
    }
    
}
