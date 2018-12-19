package com.magdy.travelli.UI;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import static com.magdy.travelli.Data.Constants.COOKIE_KEY;
import static com.magdy.travelli.Data.Constants.SESSION_COOKIE;
import static com.magdy.travelli.Data.Constants.SET_COOKIE_KEY;

public class MyApp extends Application {
    private static MyApp mInstance;
    private static RequestQueue mRequestQueue;
    private static final String TAG = "default";
    private SharedPreferences _preferences;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        _preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static synchronized MyApp getInstance()
    {
        return mInstance ;
    }
    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue==null)
            mRequestQueue = Volley.newRequestQueue(this.getApplicationContext());
        return mRequestQueue;
    }
    public void addToRequestQueue(Request request, String tag)
    {
        request.setTag(TextUtils.isEmpty(tag)?TAG : tag);
        getRequestQueue().add(request);
    }
    public void addToRequestQueue(Request request)
    {
        request.setTag(TAG );
        getRequestQueue().add(request);
    }
    public void cancelAllRequests(Object tag)
    {
        if (mRequestQueue!=null)
            mRequestQueue.cancelAll(tag);
    }
    public final void checkSessionCookie(Map<String, String> headers) {
        if (headers.containsKey(SET_COOKIE_KEY)
                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
            String cookie = headers.get(SET_COOKIE_KEY);
            if (cookie.length() > 0) {
                String[] splitCookie = cookie.split(";");
                String[] splitSessionId = splitCookie[0].split("=");
                cookie = splitSessionId[1];
                SharedPreferences.Editor prefEditor = _preferences.edit();
                prefEditor.putString(SESSION_COOKIE, cookie);
                prefEditor.apply();
            }
        }
    }
    public final void addSessionCookie(Map<String, String> headers) {
        String sessionId = _preferences.getString(SESSION_COOKIE, "");
        if (sessionId.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(SESSION_COOKIE);
            builder.append("=");
            builder.append(sessionId);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }
    }
}
