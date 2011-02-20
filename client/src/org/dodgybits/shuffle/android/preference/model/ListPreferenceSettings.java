package org.dodgybits.shuffle.android.preference.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import org.dodgybits.shuffle.android.core.model.persistence.selector.Flag;

public class ListPreferenceSettings {
    private static final String cTag = "ListPreferenceSettings";

    public static final String LIST_FILTER_ACTIVE = ".list_active";
    public static final String LIST_FILTER_COMPLETED = ".list_completed";
    public static final String LIST_FILTER_DELETED = ".list_deleted";
    public static final String LIST_FILTER_PENDING = ".list_pending";

    public static enum View {
        inbox,
    }

    private static final String PREFIX = "prefix";
    private static final String BUNDLE = "list-preference-settings";
    private static final String DEFAULT_COMPLETED = "defaultCompleted";
    private static final String DEFAULT_PENDING = "defaultPending";
    private static final String DEFAULT_DELETED = "defaultDeleted";
    private static final String DEFAULT_ACTIVE = "defaultActive";

    private String prefix;
    private Flag defaultCompleted = Flag.ignored;
    private Flag defaultPending = Flag.ignored;
    private Flag defaultDeleted = Flag.no;
    private Flag defaultActive = Flag.yes;

    public ListPreferenceSettings(String prefix) {
        this.prefix = prefix;
    }

    public void addToIntent(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString(PREFIX, prefix);
        bundle.putString(DEFAULT_COMPLETED, defaultCompleted.name());
        bundle.putString(DEFAULT_PENDING, defaultPending.name());
        bundle.putString(DEFAULT_DELETED, defaultDeleted.name());
        bundle.putString(DEFAULT_ACTIVE, defaultActive.name());
        intent.putExtra(BUNDLE, bundle);
    }


    public String getPrefix() {
        return prefix;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Flag getDefaultCompleted() {
        return defaultCompleted;
    }

    public Flag getDefaultPending() {
        return defaultPending;
    }

    public Flag getDefaultDeleted() {
        return defaultDeleted;
    }

    public Flag getDefaultActive() {
        return defaultActive;
    }

    public ListPreferenceSettings setDefaultCompleted(Flag value) {
        defaultCompleted = value;
        return this;
    }

    public ListPreferenceSettings setDefaultPending(Flag value) {
        defaultPending = value;
        return this;
    }

    public ListPreferenceSettings setDefaultDeleted(Flag value) {
        defaultDeleted = value;
        return this;
    }

    public ListPreferenceSettings setDefaultActive(Flag value) {
        defaultActive = value;
        return this;
    }

    public Flag getActive(Context context) {
        return getValue(context, LIST_FILTER_ACTIVE, defaultActive);
    }

    public Flag getCompleted(Context context) {
        return getValue(context, LIST_FILTER_COMPLETED, defaultCompleted);
    }

    public Flag getDeleted(Context context) {
        return getValue(context, LIST_FILTER_DELETED, defaultDeleted);
    }

    public Flag getPending(Context context) {
        return getValue(context, LIST_FILTER_PENDING, defaultPending);
    }

    private Flag getValue(Context context, String setting, Flag defaultValue) {
        Flag value = Flag.valueOf(getSharedPreferences(context).getString(prefix + setting, defaultValue.name()));
        Log.d(cTag, "Got value "  + value + " for settings " + prefix + setting + " with default " + defaultValue);
        return value;
    }

    public static ListPreferenceSettings fromIntent(Intent intent) {
        Bundle bundle = intent.getBundleExtra(BUNDLE);
        ListPreferenceSettings settings = new ListPreferenceSettings(bundle.getString(PREFIX));
        settings.defaultCompleted = Flag.valueOf(bundle.getString(DEFAULT_COMPLETED));
        settings.defaultPending = Flag.valueOf(bundle.getString(DEFAULT_PENDING));
        settings.defaultDeleted = Flag.valueOf(bundle.getString(DEFAULT_DELETED));
        settings.defaultActive = Flag.valueOf(bundle.getString(DEFAULT_ACTIVE));

        return settings;
    }



}
