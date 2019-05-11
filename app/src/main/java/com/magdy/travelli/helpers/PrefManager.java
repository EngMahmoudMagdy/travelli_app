package com.magdy.travelli.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import static com.magdy.travelli.helpers.StaticMembers.EMAIL;
import static com.magdy.travelli.helpers.StaticMembers.PASSWORD;
import static com.magdy.travelli.helpers.StaticMembers.TOKEN;


public class PrefManager {
    private static final String MY_PREFS_NAME = "travelli";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(MY_PREFS_NAME, PRIVATE_MODE);
    }

    public static PrefManager getInstance(Context context) {
        return new PrefManager(context);
    }


    public String getAPIToken() {
        return pref.getString(TOKEN, "");
        //return "123";
    }

    public String getEmail() {
        return pref.getString(EMAIL, "");
    }

    public String getPassword() {
        return pref.getString(PASSWORD, "");
    }

    public boolean hasToken() {
        return !pref.getString(TOKEN, "").isEmpty();
    }

    public void setAPIToken(String apiToken) {
        editor = pref.edit();
        editor.putString(TOKEN, apiToken);
        editor.apply();
    }

    public void setEmail(String apiToken) {
        editor = pref.edit();
        editor.putString(EMAIL, apiToken);
        editor.apply();
    }

    public void setPassword(String apiToken) {
        editor = pref.edit();
        editor.putString(PASSWORD, apiToken);
        editor.apply();
    }

    public boolean removeToken() {
        editor = pref.edit();
        editor.remove(TOKEN);
        editor.apply();
        return true;
    }

    public void setStringData(String key, String value) {
        editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringData(String key) {
        return pref.getString(key, "");
    }

    public boolean removeKey(String key) {
        editor = pref.edit();
        editor.remove(key);
        editor.apply();
        return true;
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor = pref.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public void setObject(String key, Object o) {
        editor = pref.edit();
        String json = new Gson().toJson(o);
        editor.putString(key, json);
        editor.apply();
    }

    public Object getObject(String key, Class<?> c) {
        try {
            String json = pref.getString(key, "");
            if (json.isEmpty())
                return null;
            return new Gson().fromJson(json, c);
        } catch (Exception e) {
            return null;
        }
    }
}
