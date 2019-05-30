package com.magdy.travelli.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.magdy.travelli.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StaticMembers {

    public static final String USERS = "users";
    public static final String USER = "user";
    public static final String TOURS = "tours";
    public static final String MEDIA = "media";
    public static final String HOTSPOTS = "hotspots";
    public static final String FAV = "fav";
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String DATE_FORMAT_VIEW = "yyyy-M-dd hh:mm";
    public static final String PLACES = "places";
    public static final String MEDIA_LIST = "media_list";
    public static final String FAV_TOURS = "fav_tours";
    public static final String AUTHORITY = "";
    private static final String LANGUAGE = "language";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String LAT = "latitude";
    public static final String LONG = "longitude";
    public static final String ADDRESS = "address";
    public static final String TOKEN = "token";
    public static final String PAGE = "page";
    public static final String NAME = "name";

    ////////////////// change Dots////////////////////
    public static void changeDots(int currentPage, int count, LinearLayout dotsLayout, Context context) {
        ImageView[] dots = new ImageView[count];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageResource(R.drawable.bullet_unselected);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > currentPage)
            dots[currentPage].setImageResource(R.drawable.bullet_selected);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    /////////////////////////////combine bitmaps///////////////////
    /*
    *
Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
Canvas canvas = new Canvas(result);
canvas.drawBitmap(firstImage, 0f, 0f, null);
canvas.drawBitmap(secondImage, 10, 10, null);
return result;*/

    public static Bitmap[] splitBitmap(Bitmap bitmap, int xCount) {
        // Allocate a two dimensional array to hold the individual images.
        Bitmap[] bitmaps = new Bitmap[xCount];
        int width;
        // Divide the original bitmap width by the desired vertical column count
        width = bitmap.getWidth() / xCount;
        // Divide the original bitmap height by the desired horizontal row count
        // Loop the array and create bitmaps for each coordinate
        for (int x = 0; x < xCount; ++x) {
            bitmaps[x] = Bitmap.createBitmap(bitmap, x * width, 0, width, bitmap.getHeight());
        }
        // Return the array
        return bitmaps;
    }

    /////////////////Dates converter/////////////////////
    public static String changeDateFromIsoToView(String dateFrom) {
        SimpleDateFormat sdf = new SimpleDateFormat(StaticMembers.ISO_DATE_FORMAT, Locale.US);
        SimpleDateFormat sdfTo = new SimpleDateFormat(StaticMembers.DATE_FORMAT_VIEW, Locale.getDefault());
        try {
            return sdfTo.format(sdf.parse(dateFrom));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFrom;
    }

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
