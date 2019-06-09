package com.magdy.travelli.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.asha.vrlib.texture.MD360BitmapTexture;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import static com.magdy.travelli.helpers.StaticMembers.MEDIA;
import static com.magdy.travelli.helpers.StaticMembers.makeThumbnailBig;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

public class ImageLoaderHelper {
    static Target target;

    public static void loadImageList(final Context context,
                                     final MD360BitmapTexture.Callback callback,
                                     String thumbnail,
                                     final List<String> urls,
                                     final ImageLoadedListener imageLoadedListener) {
        loadImage(context, callback, thumbnail, new ImageLoadedListener() {
            @Override
            public void onImageTotalFinished() {

            }

            @Override
            public void onImageLoaded(Bitmap bitmap) {
                imageLoadedListener.onImageLoaded(bitmap);
                loadImageList(context, callback, urls, urls.size() / 2, bitmap, imageLoadedListener);
            }
        });

    }

    private static void loadImageList(final Context context, final MD360BitmapTexture.Callback callback,
                                      final List<String> urls, final int i,
                                      final Bitmap bitmapTotal,
                                      final ImageLoadedListener imageLoadedListener) {
        final int size = urls.size();
        loadImage(context, callback, urls.get(i), new ImageLoadedListener() {
            @Override
            public void onImageTotalFinished() {

            }

            @Override
            public void onImageLoaded(Bitmap bitmapPart) {
                Bitmap bitmap;
                int index;
                if (i == urls.size() / 2) {
                    bitmap = makeThumbnailBig(bitmapTotal, bitmapPart.getHeight());
                    index = i + 1;
                    int left = (i == 4) ? bitmap.getWidth() - bitmapPart.getWidth() :
                            i * bitmapPart.getWidth();
                    bitmap = StaticMembers.compineBitmap(bitmap, bitmapPart, left);
                } else {
                    if (i > urls.size() / 2) {
                        /////// Right side of image
                        index = size - i - 1;
                    } else {
                        /////// Left side of image
                        index = size - i;
                    }
                    int left = (i == 4) ? bitmapTotal.getWidth() - bitmapPart.getWidth() :
                            i * bitmapPart.getWidth();
                    bitmap = StaticMembers.compineBitmap(bitmapTotal, bitmapPart, left);
                }
                imageLoadedListener.onImageLoaded(bitmap);
                //stop recursion here
                if (index > -1 && index < size)
                    loadImageList(context, callback, urls, index, bitmap, imageLoadedListener);
                else imageLoadedListener.onImageTotalFinished();
            }
        });

    }

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
                .memoryPolicy(NO_STORE)
                .tag(MEDIA)
                .into(target);
    }

    public interface ImageLoadedListener {
        void onImageLoaded(Bitmap bitmap);

        void onImageTotalFinished();
    }
}
