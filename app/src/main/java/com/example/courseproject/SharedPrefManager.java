package com.example.courseproject;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    public static final String PREFS_NAME = "notes_app_prefs";
    public static final String KEY_LOGGED_IN_EMAIL = "logged_in_email";
    public static final String KEY_REMEMBER_ME = "remember_me";
    public static final String KEY_SORT_MODE = "sort_mode";
    public static final String KEY_REMEMBERED_EMAIL = "remembered_email";

    private static SharedPrefManager instance = null;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public static SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean writeString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }

    public String readString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public boolean writeBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean readBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /** Clears the logged-in session. Does not touch "remember me" / remembered email. */
    public boolean logout() {
        editor.remove(KEY_LOGGED_IN_EMAIL);
        return editor.commit();
    }

    public boolean isLoggedIn() {
        return !readString(KEY_LOGGED_IN_EMAIL, "").isEmpty();
    }
}