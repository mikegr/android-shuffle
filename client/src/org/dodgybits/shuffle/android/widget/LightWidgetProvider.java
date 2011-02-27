/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.shuffle.android.widget;

import static org.dodgybits.shuffle.android.core.util.Constants.cIdType;
import static org.dodgybits.shuffle.android.core.util.Constants.cPackage;
import static org.dodgybits.shuffle.android.core.util.Constants.cStringType;

import java.util.HashMap;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.DefaultEntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.view.ContextIcon;
import org.dodgybits.shuffle.android.list.config.StandardTaskQueries;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import roboguice.inject.ContentResolverProvider;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

/**
 * A widget provider.  We have a string that we pull from a preference in order to show
 * the configuration settings and the current time when the widget was updated.  We also
 * register a BroadcastReceiver for time-changed and timezone-changed broadcasts, and
 * update then too.
 */
public class LightWidgetProvider extends AbstractWidgetProvider {

    @Override
    protected int getWidgetLayoutId() {
        return R.layout.widget;
    }

    @Override
    protected int getTotalEntries() {
        return 4;
    }

    @Override
    protected int updateContext(android.content.Context androidContext, RemoteViews views, Context context, int taskCount) {
        int contextIconId = getIdIdentifier(androidContext, "context_icon_" + taskCount);
        String iconName = context != null ? context.getIconName() : null;
        ContextIcon icon = ContextIcon.createIcon(iconName, androidContext.getResources());
        if (icon != ContextIcon.NONE) {
            views.setImageViewResource(contextIconId, icon.smallIconId);
            views.setViewVisibility(contextIconId, View.VISIBLE);
        } else {
            views.setViewVisibility(contextIconId, View.INVISIBLE);
        }

        return contextIconId;
    }



}


