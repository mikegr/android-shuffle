package org.dodgybits.android.shuffle.util;

import org.dodgybits.android.shuffle.R;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

public class AlertUtils {
	private static final String cTag = "AlertUtils";
	
	
	private AlertUtils() {
		//deny
	}
	
	public static void showDeleteGroupWarning(final Context context, final String groupName, final String childName, 
			final int childCount, final OnClickListener buttonListener) {
		CharSequence title = context.getString(R.string.warning_title);
		CharSequence message = context.getString(R.string.delete_warning, groupName.toLowerCase(), childCount, childName.toLowerCase());
		CharSequence deleteButtonText = context.getString(R.string.menu_delete);
		CharSequence cancelButtonText = context.getString(R.string.cancel_button_title);
		OnCancelListener cancelListener = new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				Log.d(cTag, "Cancelled delete. Do nothing.");
			}
		};
		Builder builder = new Builder(context);
		builder.setTitle(title).setIcon(R.drawable.dialog_warning)
			.setMessage(message).setNegativeButton(cancelButtonText, buttonListener)
			.setPositiveButton(deleteButtonText, buttonListener)
			.setOnCancelListener(cancelListener);
		builder.create().show();
	}
	
	public static void showCleanUpInboxMessage(final Context context) {
		CharSequence title = context.getString(R.string.info_title);
		CharSequence message = context.getString(R.string.clean_inbox_message);
		CharSequence buttonText = context.getString(R.string.ok_button_title);
		Builder builder = new Builder(context);
		builder.setTitle(title).setIcon(R.drawable.dialog_information)
			.setMessage(message)
			.setPositiveButton(buttonText, null);
		builder.create().show();
	}
	
}
