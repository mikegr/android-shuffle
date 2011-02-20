package org.dodgybits.shuffle.android.list.view;

import android.content.Context;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;
import org.dodgybits.android.shuffle.R;

public class StatusView extends TextView {

    public static enum Status {
        yes, no, fromContext, fromProject
    }


    private SpannableString mDeleted;
    private SpannableString mDeletedFromContext;
    private SpannableString mDeletedFromProject;

    private SpannableString mInactive;
    private SpannableString mInactiveFromContext;
    private SpannableString mInactiveFromProject;

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
        int deletedColour = getResources().getColor(R.drawable.red);
        ParcelableSpan deletedColorSpan = new ForegroundColorSpan(deletedColour);

        int inactiveColour = getResources().getColor(R.drawable.mid_gray);
        ParcelableSpan inactiveColorSpan = new ForegroundColorSpan(inactiveColour);

        String deleted = getResources().getString(R.string.deleted);
        String inactive = getResources().getString(R.string.inactive);
        String fromContext =  getResources().getString(R.string.from_context);
        String fromProject =  getResources().getString(R.string.from_project);

        mDeleted = new SpannableString(deleted);
        mDeleted.setSpan(deletedColorSpan, 0, mDeleted.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mDeletedFromContext = new SpannableString(deleted + " " + fromContext);
        mDeletedFromContext.setSpan(deletedColorSpan, 0, mDeletedFromContext.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mDeletedFromProject = new SpannableString(deleted + " " + fromProject);
        mDeletedFromProject.setSpan(deletedColorSpan, 0, mDeletedFromProject.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        mInactive = new SpannableString(inactive);
        mInactive.setSpan(inactiveColorSpan, 0, mInactive.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mInactiveFromContext = new SpannableString(inactive + " " + fromContext);
        mInactiveFromContext.setSpan(inactiveColorSpan, 0, mInactiveFromContext.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mInactiveFromProject = new SpannableString(inactive + " " + fromProject);
        mInactiveFromProject.setSpan(inactiveColorSpan, 0, mInactiveFromProject.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    public void updateStatus(boolean active, boolean deleted) {
        updateStatus(active ? StatusView.Status.yes : StatusView.Status.no,
                deleted ? StatusView.Status.yes : StatusView.Status.no);
    }

    public void updateStatus(Status active, Status deleted) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        switch (deleted) {
            case yes:
                builder.append(mDeleted);
                break;

            case fromContext:
                builder.append(mDeletedFromContext);
                break;

            case fromProject:
                builder.append(mDeletedFromProject);
                break;
        }

        builder.append(" ");

        switch (active) {
            case no:
                builder.append(mInactive);
                break;

            case fromContext:
                builder.append(mInactiveFromContext);
                break;

            case fromProject:
                builder.append(mInactiveFromProject);
                break;
        }

        setText(builder);
    }

}
