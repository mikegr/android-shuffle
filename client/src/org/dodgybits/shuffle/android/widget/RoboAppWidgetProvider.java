package org.dodgybits.shuffle.android.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.inject.Injector;
import roboguice.application.RoboApplication;
import roboguice.inject.ContextScope;
import roboguice.receiver.RoboBroadcastReceiver;

public class RoboAppWidgetProvider extends RoboBroadcastReceiver {

    @Override
    protected void handleReceive(Context context, Intent intent) {
        // Protect against rogue update broadcasts (not really a security issue,
        // just filter bad broadcasts out so subclasses are less likely to crash).
        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        }
        else if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                this.onDeleted(context, new int[] { appWidgetId });
            }
        }
        else if (AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {
            this.onEnabled(context);
        }
        else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {
            this.onDisabled(context);
        }
    }

    /**
     * Called in response to the {@link AppWidgetManager#ACTION_APPWIDGET_UPDATE} broadcast when
     * this AppWidget provider is being asked to provide {@link android.widget.RemoteViews RemoteViews}
     * for a set of AppWidgets.  Override this method to implement your own AppWidget functionality.
     *
     * {@more}
     *
     * @param context   The {@link android.content.Context Context} in which this receiver is
     *                  running.
     * @param appWidgetManager A {@link AppWidgetManager} object you can call {@link
     *                  AppWidgetManager#updateAppWidget} on.
     * @param appWidgetIds The appWidgetIds for which an update is needed.  Note that this
     *                  may be all of the AppWidget instances for this provider, or just
     *                  a subset of them.
     *
     * @see AppWidgetManager#ACTION_APPWIDGET_UPDATE
     */
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    }

    /**
     * Called in response to the {@link AppWidgetManager#ACTION_APPWIDGET_DELETED} broadcast when
     * one or more AppWidget instances have been deleted.  Override this method to implement
     * your own AppWidget functionality.
     *
     * {@more}
     *
     * @param context   The {@link android.content.Context Context} in which this receiver is
     *                  running.
     * @param appWidgetIds The appWidgetIds that have been deleted from their host.
     *
     * @see AppWidgetManager#ACTION_APPWIDGET_DELETED
     */
    public void onDeleted(Context context, int[] appWidgetIds) {
    }

    /**
     * Called in response to the {@link AppWidgetManager#ACTION_APPWIDGET_ENABLED} broadcast when
     * the a AppWidget for this provider is instantiated.  Override this method to implement your
     * own AppWidget functionality.
     *
     * {@more}
     * When the last AppWidget for this provider is deleted,
     * {@link AppWidgetManager#ACTION_APPWIDGET_DISABLED} is sent by the AppWidget manager, and
     * {@link #onDisabled} is called.  If after that, an AppWidget for this provider is created
     * again, onEnabled() will be called again.
     *
     * @param context   The {@link android.content.Context Context} in which this receiver is
     *                  running.
     *
     * @see AppWidgetManager#ACTION_APPWIDGET_ENABLED
     */
    public void onEnabled(Context context) {
    }

    /**
     * Called in response to the {@link AppWidgetManager#ACTION_APPWIDGET_DISABLED} broadcast, which
     * is sent when the last AppWidget instance for this provider is deleted.  Override this method
     * to implement your own AppWidget functionality.
     *
     * {@more}
     *
     * @param context   The {@link android.content.Context Context} in which this receiver is
     *                  running.
     *
     * @see AppWidgetManager#ACTION_APPWIDGET_DISABLED
     */
    public void onDisabled(Context context) {
    }

}
