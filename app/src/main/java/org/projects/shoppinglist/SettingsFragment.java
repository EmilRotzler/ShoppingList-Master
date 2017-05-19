package org.projects.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by Emil Rotzler on 20-04-2017.
 */

public class SettingsFragment extends PreferenceFragment {


    private static String SETTINGS_NAMEKEY = "name";
    private static String SETTINGS_LISTKEY = "listname";

    public static String getName(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_NAMEKEY, "");
    }

    public static String getList(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_LISTKEY, "");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adding the preferences from the xml
        //so this will in fact be the whole view.
        addPreferencesFromResource(R.xml.settings);
    }
}
