package me.sihrc.flag.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import me.sihrc.flag.R;

/**
 * Created by Chris on 4/16/15.
 * Handles Shared Preferences for the app
 */
public class PrefsHelper {
    static PrefsHelper instance;

    SharedPreferences prefs;

    public static PrefsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PrefsHelper();
            instance.prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        }

        return instance;
    }

    /**
     * Getters *
     */
    public String getString(String key) {
        return prefs.getString(key, "");
    }
    public int getInt(String key) {return prefs.getInt(key, -1);}

    /**
     * Setters *
     */
    public void saveString(String key, String value) {
        if (value == null) {
            Log.e("PreferenceHandler", key + ", value is null");
            return;
        }
        prefs.edit().putString(key, value).apply();
    }

    public void saveInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }
}
