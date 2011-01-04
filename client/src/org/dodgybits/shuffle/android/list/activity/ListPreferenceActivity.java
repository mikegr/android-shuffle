package org.dodgybits.shuffle.android.list.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.util.Constants;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

public class ListPreferenceActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private static final String cTag = "ListPreferenceActivity";

    private ListPreferenceSettings settings;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = ListPreferenceSettings.fromIntent(getIntent());

        setupScreen();
    }

    private void setupScreen() {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
        int screenId = getStringId("title_" + settings.getPrefix());
        String title = getString(screenId) + " " + getString(R.string.list_settings_title);
        screen.setTitle(title);

        screen.addPreference(createList(
                R.array.list_preferences_active_labels,
                R.string.active_items_title,
                settings.getActive(this).name(),
                ListPreferenceSettings.LIST_FILTER_ACTIVE,
                settings.getDefaultActive().name()));

        screen.addPreference(createList(
                R.array.list_preferences_pending_labels,
                R.string.pending_items_title,
                settings.getPending(this).name(),
                ListPreferenceSettings.LIST_FILTER_PENDING,
                settings.getDefaultPending().name()));

        screen.addPreference(createList(
                R.array.list_preferences_completed_labels,
                R.string.completed_items_title,
                settings.getCompleted(this).name(),
                ListPreferenceSettings.LIST_FILTER_COMPLETED,
                settings.getDefaultCompleted().name()));

        screen.addPreference(createList(
                R.array.list_preferences_deleted_labels,
                R.string.deleted_items_title,
                settings.getDeleted(this).name(),
                ListPreferenceSettings.LIST_FILTER_DELETED,
                settings.getDefaultDeleted().name()));

        setPreferenceScreen(screen);
    }

    private int getStringId(String id) {
        return getResources().getIdentifier(id, Constants.cStringType, Constants.cPackage);
    }

    private ListPreference createList(int entries, int title, String value, String keySuffix, Object defaultValue) {
        ListPreference listPreference = new ListPreference(this);
        listPreference.setEntryValues(R.array.list_preferences_flag_values);
        listPreference.setEntries(entries);
        listPreference.setTitle(title);
        String key = settings.getPrefix() + keySuffix;
        listPreference.setKey(key);
        listPreference.setDefaultValue(defaultValue);
        listPreference.setOnPreferenceChangeListener(this);

        CharSequence[] entryStrings = listPreference.getEntries();
        int index = listPreference.findIndexOfValue(value);
        if (index > -1) {
            listPreference.setSummary(entryStrings[index]);
        }

        Log.d(cTag, "Creating list perference key=" + key + " value=" + value + " default=" + defaultValue + " title=" + title);

        return listPreference;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        ListPreference listPreference = (ListPreference)preference;
        int index = listPreference.findIndexOfValue((String)o);
        preference.setSummary(listPreference.getEntries()[index]);
        return true;
    }

}