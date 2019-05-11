package com.magdy.travelli.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Locale;

public class StaticMembers {

    public static final String USERS = "users";
    public static final String USER = "user";
    public static final String TOURS = "tours";
    public static final String MEDIA = "media";
    public static final String HOTSPOTS = "hotspots";
    private static final String LANGUAGE = "language";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String LAT = "latitude";
    public static final String LONG = "longitude";
    public static final String ADDRESS = "address";
    public static final String TOKEN = "token";
    public static final String PAGE = "page";

    public static <T extends Activity> void startActivityOverAll(T activity, Class<?> destinationActivity) {
        Intent intent = new Intent(activity, destinationActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finishAffinity();
    }

    public static <T extends Activity> void startActivityOverAll(Intent intent, T activity) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finishAffinity();
    }

    public static <T extends View> void hideKeyboard(T view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static <A extends Activity> void hideKeyboard(A activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
    //////////////////////Visiblity with Animation/////////////////////

    public static <V extends View> void makeVisible(V layout) {
        layout.setVisibility(View.VISIBLE);
        layout.setAlpha(0.0f);
        layout.animate()
                .translationY(0)
                .alpha(1.0f)
                .setListener(null);
    }

    public static <V extends View> void makeGone(V layout) {
        layout.setVisibility(View.GONE);
    }
    //////////////////////Toasts/////////////////////

    private static Toast toast;

    public static void toastMessageShort(Context context, String messaage) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, messaage, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toastMessageShort(Context context, int messaage) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, messaage, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toastMessageShort(Context context, CharSequence messaage) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, messaage, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toastMessageLong(Context context, int messaage) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, messaage, Toast.LENGTH_LONG);
        toast.show();
    }

    public static boolean CheckTextInputEditText(TextInputEditText editText, final TextInputLayout textInputLayout, final String errorMessage) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    textInputLayout.setError(errorMessage);
                    textInputLayout.setErrorEnabled(true);
                } else {
                    textInputLayout.setErrorEnabled(false);
                }
            }
        });
        if (TextUtils.isEmpty(editText.getText())) {
            textInputLayout.setError(errorMessage);
            textInputLayout.setErrorEnabled(true);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public static String getLanguage(Context context) {
        return PrefManager.getInstance(context).getStringData(LANGUAGE);
    }

    public static void setLanguage(Context context, String langStr) {
        PrefManager.getInstance(context).setStringData(LANGUAGE, langStr);
    }

    public static void changeLocale(Context context, String langStr) {
        setLanguage(context, langStr);
        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(langStr.toLowerCase())); // API 17+ only.
        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);
        Locale locale = context.getResources().getConfiguration().locale;
        Locale.setDefault(locale);
    }
}
