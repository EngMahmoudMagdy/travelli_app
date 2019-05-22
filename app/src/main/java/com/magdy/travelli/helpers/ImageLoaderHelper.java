package com.magdy.travelli.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.asha.vrlib.texture.MD360BitmapTexture;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

public class ImageLoaderHelper {
    static Target target;

    public static void loadImage(Context context, MD360BitmapTexture.Callback callback,
                          String url,
                          final ImageLoadedListener imageLoadedListener) {
        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageLoadedListener.onImageLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(context)
                .load(url)
                .resize(callback.getMaxTextureSize(), callback.getMaxTextureSize())
                .onlyScaleDown()
                .centerInside()
                .memoryPolicy(NO_CACHE, NO_STORE)
                .into(target);
    }

    public interface ImageLoadedListener {
        void onImageLoaded(Bitmap bitmap);
    }
}
