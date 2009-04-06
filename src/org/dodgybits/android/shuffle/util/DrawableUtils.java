package org.dodgybits.android.shuffle.util;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;

public class DrawableUtils {

	private DrawableUtils() {
		//deny
	}
	
    public static Drawable createRoundedVerticalGradient(int colour) {
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

    public static Drawable createHorizontalGradient(int colour) {
		int[] colours = new int[2];
		float[] hsv1 = new float[3];
		float[] hsv2 = new float[3];
		Color.colorToHSV(colour, hsv1);
		Color.colorToHSV(colour, hsv2);
		hsv1[2] *= 1.1;
		hsv2[2] *= 0.90;
		colours[0] = Color.HSVToColor(hsv1);
		colours[1] = Color.HSVToColor(hsv2);
    	GradientDrawable drawable = new GradientDrawable(Orientation.LEFT_RIGHT, colours);
    	return drawable;
    	
    }
    
}
