package org.dodgybits.shuffle.android.list.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;
import org.dodgybits.android.shuffle.R;

public class StatusView extends TextView {

    private SpannableString mDeleted;
    private SpannableString mDeletedAndInactive;
    private SpannableString mInactive;

    public StatusView(Context context) {
        super(context);

        createStatusStrings();
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);

        createStatusStrings();
    }

    public StatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        createStatusStrings();
    }

    private void createStatusStrings() {
        String deleted = getResources().getString(R.string.deleted);
        int deletedColour = getResources().getColor(R.drawable.red);
        ForegroundColorSpan deletedColorSpan = new ForegroundColorSpan(deletedColour);

        String inactive = getResources().getString(R.string.inactive);
        int inactiveColour = getResources().getColor(R.drawable.mid_gray);
        ForegroundColorSpan inactiveColorSpan = new ForegroundColorSpan(inactiveColour);

        mDeleted = new SpannableString(deleted);
        mDeleted.setSpan(deletedColorSpan, 0, deleted.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mDeletedAndInactive = new SpannableString(inactive + " " + deleted);
        mDeletedAndInactive.setSpan(inactiveColorSpan, 0, inactive.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mDeletedAndInactive.setSpan(deletedColorSpan, inactive.length(), mDeletedAndInactive.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mInactive = new SpannableString(inactive);
        mInactive.setSpan(inactiveColorSpan, 0, inactive.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    public void updateStatus(boolean active, boolean deleted) {
        if (deleted) {
            if (active) {
                setText(mDeleted);
            } else {
                setText(mDeletedAndInactive);
            }
        } else {
            if (active) {
                setText("");
            } else {
                setText(mInactive);
            }
        }
    }

}
