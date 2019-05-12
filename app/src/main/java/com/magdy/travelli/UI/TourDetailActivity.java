package com.magdy.travelli.UI;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360DirectorFactory;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.asha.vrlib.model.MDHotspotBuilder;
import com.asha.vrlib.model.MDPinchConfig;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.model.MDRay;
import com.asha.vrlib.plugins.MDAbsPlugin;
import com.asha.vrlib.plugins.MDWidgetPlugin;
import com.asha.vrlib.plugins.hotspot.IMDHotspot;
import com.asha.vrlib.texture.MD360BitmapTexture;
import com.google.android.apps.muzei.render.GLTextureView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magdy.travelli.Adapters.CustomProjectionFactory;
import com.magdy.travelli.Adapters.MediaPlayerWrapper;
import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Hotspot;
import com.magdy.travelli.Data.Media;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;
import com.magdy.travelli.Services.VideoDownloadService;
import com.magdy.travelli.helpers.StaticMembers;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import tv.danmaku.ijk.media.player.IMediaPlayer;

import static com.magdy.travelli.Data.Constants.FILE;
import static com.magdy.travelli.Data.Constants.ID;
import static com.magdy.travelli.Data.Constants.ISREADY;
import static com.magdy.travelli.Data.Constants.SEEK;
import static com.magdy.travelli.Data.Constants.URL;
import static com.magdy.travelli.Data.Constants.VR;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;


public class TourDetailActivity extends AppCompatActivity {

    private static final String TAG = TourDetailActivity.class.getSimpleName();
    private MDVRLibrary mVRLibrary, mVideoLib;
    private Button playButton;
    private ProgressBar progressBar;
    private AppCompatSeekBar seekBar;
    private ImageButton controllers;
    private FrameLayout controllersLayout;
    FloatingActionButton reserve, addReview;
    AppCompatImageButton right, left;
    private Target mTarget;
    Tour tour;
    private GLTextureView imageView, videoView;
    String outFilePath;
    private MediaPlayerWrapper mMediaPlayerWrapper;
    private MDVRLibrary.IImageLoadProvider mImageLoadProvider = new ImageLoadProvider();

    boolean isReady = false, isPlaying = false, loadingBool = false, isImage = true;
    RequestQueue queue;
    ArrayList<Media> mediaList;
    ArrayList<Hotspot> hotspots;
    private List<MDAbsPlugin> plugins;
    int turn = 0;
    Media currentMedia;
    TextView indicator;
    TextToSpeech textToSpeech;
    boolean canspeak = false;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);
        imageView = findViewById(R.id.gl_view);
        videoView = findViewById(R.id.gl_video);
        progressBar = findViewById(R.id.progress);
        seekBar = findViewById(R.id.seekbar);
        indicator = findViewById(R.id.indicator);
        mMediaPlayerWrapper = new MediaPlayerWrapper();
        queue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        plugins = new ArrayList<>();
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getBaseContext(), "Not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        canspeak = true;
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Init failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tour = (Tour) getIntent().getSerializableExtra(Constants.TOUR);
        buttonsInit();
        imageStart();
        videoStart();
        hotspots = new ArrayList<>();
        if (savedInstanceState == null) {
            mediaList = new ArrayList<>();
            //downloadMedia(tour.getId());
            FirebaseDatabase.getInstance().getReference(StaticMembers.MEDIA).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mediaList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Media media = snapshot.getValue(Media.class);
                        if (media != null)
                            mediaList.add(media);
                    }
                    turn = 0;
                    if (!mediaList.isEmpty()) {
                        currentMedia = mediaList.get(0);
                        indicator.setText(String.format(Locale.getDefault(), "%d ==> %d", turn + 1, mediaList.size()));
                        //getmVRLibrary().onResume(getBaseContext());
                        if (currentMedia.getType() == 0) {
                            imageStart();
                            downloadHotspots(currentMedia.getId(), currentMedia.getKey());
                            mVRLibrary.notifyPlayerChanged();
                        } else {
                            videoStart();
                            Intent mServiceIntent = new Intent(getBaseContext(), VideoDownloadService.class);
                            mServiceIntent.putExtra(URL, currentMedia.getLink());
                            mServiceIntent.putExtra(ID, currentMedia.getId());
                            startService(mServiceIntent);
                            isReady = true;
                        }
                    } else {
                        right.setVisibility(View.INVISIBLE);
                        left.setVisibility(View.INVISIBLE);
                    }
                    Toast.makeText(getBaseContext(), "Media links download complete!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            mediaList = savedInstanceState.getParcelableArrayList(Constants.CURRRENT_MED);
            turn = savedInstanceState.getInt(Constants.TURN_MED);
            currentMedia = mediaList.get(turn);
            indicator.setText(String.format(Locale.getDefault(), "%d ==> %d", turn + 1, mediaList.size()));
            updateRightLeft();
            if (currentMedia.getType() > 0) {
                outFilePath = savedInstanceState.getString(Constants.FILE);
                videoStart();
                isReady = true;
                MDVRLibrary library = getmVRLibrary();
                library.notifyPlayerChanged();
                setVideoUri(outFilePath, savedInstanceState.getLong(SEEK));
            } else {
                hotspots = savedInstanceState.getParcelableArrayList(Constants.CURRRENT_HS);
                assert hotspots != null;
                for (Hotspot hotspot : hotspots) {
                    addHotspot(hotspot);
                }
                imageStart();
                MDVRLibrary library = getmVRLibrary();
                library.notifyPlayerChanged();
            }
        }
        mViewPager = findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        //mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.primary_500));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() > 1) {
                    addReview.setVisibility(View.VISIBLE);
                    reserve.setVisibility(View.GONE);
                } else {
                    addReview.setVisibility(View.GONE);
                    reserve.setVisibility(View.VISIBLE);
                }
                /*Log.d(TAG, "onTabSelected: pos: " + tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        break;
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    MDVRLibrary getmVRLibrary() {
        if (currentMedia != null) {
            if (currentMedia.getType() == 0) {
                return mVideoLib;
            } else
                return mVRLibrary;
        }
        return mVRLibrary;
    }

    void videoStart() {
        loadingBool = false;
        seekBar.setVisibility(View.VISIBLE);
        controllers.setVisibility(View.VISIBLE);
        controllersLayout.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        if (mVideoLib == null) {
            mVideoLib = MDVRLibrary.with(this)
                    .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                    .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH)
                    .asVideo(new MDVRLibrary.IOnSurfaceReadyCallback() {
                        @Override
                        public void onSurfaceReady(Surface surface) {
                            mMediaPlayerWrapper.setSurface(surface);
                        }
                    })
                    .pinchConfig(new MDPinchConfig().setMin(1.0f).setMax(8.0f).setDefaultValue(0.1f))
                    .pinchEnabled(true)
                    .directorFactory(new MD360DirectorFactory() {
                        @Override
                        public MD360Director createDirector(int index) {
                            return MD360Director.builder().setPitch(-90).build();
                        }
                    })
                    .projectionFactory(new CustomProjectionFactory())
                    .barrelDistortionConfig(new BarrelDistortionConfig().setDefaultEnabled(false).setScale(0.95f))
                    .build(videoView);
        }
        mMediaPlayerWrapper.init();
        mMediaPlayerWrapper.setPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                cancelBusy();
                if (mVideoLib != null) {
                    mVideoLib.notifyPlayerChanged();
                }
            }
        });
        getPlayer().setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                String error = String.format(Locale.US, "Play Error what=%d extra=%d", what, extra);
                Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        getPlayer().setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
                getmVRLibrary().onTextureResize(width, height);
            }
        });

        getPlayer().setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                seekBar.setMax((int) iMediaPlayer.getDuration());
                cancelBusy();
                playCycle();
            }
        });
        getPlayer().setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                float f = (i / 100.0f) * seekBar.getMax();
                seekBar.setSecondaryProgress((int) f);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    getPlayer().seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        videoView.setTag("Video");
    }

    void buttonsInit() {
        playButton = findViewById(R.id.playbut);
        controllersLayout = findViewById(R.id.controllerslayout);
        controllersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllersLayout.setVisibility(controllersLayout.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayerWrapper.getPlayer().isPlaying()) {
                    playButton.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                    getPlayer().pause();
                    isPlaying = false;
                } else {
                    playButton.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    getPlayer().start();
                    isPlaying = true;
                }
            }
        });
        final ImageButton full = findViewById(R.id.fullscreen);
        final ImageButton vr = findViewById(R.id.vr_mode);
        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), FullScreenActivity.class);
                i.putExtra(Constants.CURRRENT_MED, currentMedia);
                i.putExtra(Constants.ALLMED, mediaList);
                if (currentMedia.getType() > 0) {
                    i.putExtra(SEEK, getPlayer().getCurrentPosition());
                    i.putExtra(URL, outFilePath);
                    i.putExtra(ISREADY, isReady);
                } else {
                    i.putExtra(Constants.CURRRENT_HS, hotspots);
                }
                i.putExtra(VR, false);
                startActivity(i);
            }
        });
        full.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                makeToast(full, getString(R.string.fullscreen));
                return true;
            }
        });
        vr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), FullScreenActivity.class);
                i.putExtra(Constants.CURRRENT_MED, currentMedia);
                i.putExtra(Constants.ALLMED, mediaList);
                if (currentMedia.getType() > 0) {
                    i.putExtra(SEEK, getPlayer().getCurrentPosition());
                    i.putExtra(URL, outFilePath);
                    i.putExtra(ISREADY, isReady);
                } else {
                    i.putExtra(Constants.CURRRENT_HS, hotspots);
                }
                i.putExtra(VR, true);
                startActivity(i);
            }
        });
        vr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                makeToast(vr, getString(R.string.vr_mode));
                return true;
            }
        });
        final ImageButton motionMode = findViewById(R.id.motion_mode);
        controllers = findViewById(R.id.controllers);
        motionMode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (getmVRLibrary().getInteractiveMode() == MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH)
                    makeToast(motionMode, getString(R.string.touch_mode));
                else
                    makeToast(motionMode, getString(R.string.motion_touch));

                return true;
            }
        });
        motionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getmVRLibrary().getInteractiveMode() == MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH) {
                    getmVRLibrary().switchInteractiveMode(getBaseContext(), MDVRLibrary.INTERACTIVE_MODE_TOUCH);
                    motionMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_3d_rotation_black_24dp));
                } else {
                    getmVRLibrary().switchInteractiveMode(getBaseContext(), MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH);
                    motionMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_app_black_24dp));
                }
            }
        });
        controllers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllersLayout.setVisibility(controllersLayout.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        });
        controllers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                makeToast(controllers, getString(R.string.controllers));
                return true;
            }
        });
        right = findViewById(R.id.right);
        left = findViewById(R.id.left);
        left.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (turn > 0 && !mediaList.isEmpty()) {
                    turn--;
                    currentMedia = mediaList.get(turn);
                    indicator.setText(String.format(Locale.getDefault(), "%d ==> %d", turn + 1, mediaList.size()));
                    if (currentMedia.getType() == 0) {
                        imageStart();
                        downloadHotspots(currentMedia.getId(), currentMedia.getKey());
                    } else {
                        videoStart();
                        Intent mServiceIntent = new Intent(getBaseContext(), VideoDownloadService.class);
                        mServiceIntent.putExtra(URL, currentMedia.getLink());
                        mServiceIntent.putExtra(ID, currentMedia.getId());
                        startService(mServiceIntent);
                        isReady = true;
                    }
                    MDVRLibrary library = getmVRLibrary();
                    library.notifyPlayerChanged();
                    if (mMediaPlayerWrapper != null) {
                        if (mMediaPlayerWrapper.getPlayer().isPlaying())
                            getPlayer().pause();
                    }
                    updateRightLeft();
                }
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (turn < mediaList.size() - 1 && !mediaList.isEmpty()) {
                    turn++;
                    currentMedia = mediaList.get(turn);
                    indicator.setText(String.format(Locale.getDefault(), "%d ==> %d", turn + 1, mediaList.size()));
                    if (currentMedia.getType() == 0) {
                        imageStart();
                        downloadHotspots(currentMedia.getId(), currentMedia.getKey());
                    } else {
                        videoStart();
                        Intent mServiceIntent = new Intent(getBaseContext(), VideoDownloadService.class);
                        mServiceIntent.putExtra(URL, currentMedia.getLink());
                        mServiceIntent.putExtra(ID, currentMedia.getId());
                        startService(mServiceIntent);
                        isReady = true;
                    }
                    MDVRLibrary library = getmVRLibrary();
                    library.notifyPlayerChanged();
                    if (mMediaPlayerWrapper != null) {
                        if (mMediaPlayerWrapper.getPlayer().isPlaying())
                            getPlayer().pause();
                    }
                    updateRightLeft();
                }
            }
        });
        reserve = findViewById(R.id.book);
        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"Book Now!",Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getBaseContext()).startActivity(new Intent(getBaseContext(), Reserve.class));
            }
        });
        addReview = findViewById(R.id.fab);
        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                FragmentManager fragmentManager = getSupportFragmentManager();
                ReviewingFragment fragment = ReviewingFragment.newInstance(tour);
                if (fragmentManager != null)
                    fragment.show(fragmentManager, "reviewing");
            }
        });
    }

    void updateRightLeft() {
        if (turn > 0) {
            left.setVisibility(View.VISIBLE);
        } else {
            left.setVisibility(View.INVISIBLE);
        }
        if (turn == mediaList.size() - 1) {
            right.setVisibility(View.INVISIBLE);
        } else {
            right.setVisibility(View.VISIBLE);
        }
    }

    void makeToast(View view, String text) {

        int x = view.getLeft();
        int y = view.getTop();
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.START, x, y);
        toast.show();
    }

    void imageStart() {
        loadingBool = true;
        controllers.setVisibility(View.GONE);
        controllersLayout.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        if (mVRLibrary == null) {
            mVRLibrary = MDVRLibrary.with(this)
                    .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                    .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH)
                    .asBitmap(new MDVRLibrary.IBitmapProvider() {
                        @Override
                        public void onProvideBitmap(final MD360BitmapTexture.Callback callback) {
                            if (currentMedia != null)
                                loadImage(currentMedia.getLink(), callback);
                        }
                    })
                    .listenTouchPick(new MDVRLibrary.ITouchPickListener() {
                        @Override
                        public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                            Log.d(TAG, "Ray:" + ray + ", hitHotspot:" + hitHotspot);
                        }
                    })
                    .pinchEnabled(true)
                    .directorFactory(new MD360DirectorFactory() {
                        @Override
                        public MD360Director createDirector(int index) {
                            return MD360Director.builder().setPitch(-90).build();
                        }
                    })
                    //.projectionFactory(new CustomProjectionFactory())
                    .build(imageView);
            imageView.setTag(mTarget);
        } else {
            mVRLibrary.notifyPlayerChanged();
        }
    }

    private void loadImage(String url, final MD360BitmapTexture.Callback callback) {
        Log.d(TAG, "load image with max texture size:" + callback.getMaxTextureSize());
        showBusy();
        if (mediaList.isEmpty()) {
            right.setVisibility(View.INVISIBLE);
        } else {
            if (turn < mediaList.size() - 1)
                right.setVisibility(View.VISIBLE);
        }
        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d(TAG, "loaded image, size:" + bitmap.getWidth() + "," + bitmap.getHeight());
                // notify if size changed
                getmVRLibrary().onTextureResize(bitmap.getWidth(), bitmap.getHeight());
                // texture
                callback.texture(bitmap);
                cancelBusy();
                loadingBool = false;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        Picasso.with(getApplicationContext())
                .load(url)
                .resize(callback.getMaxTextureSize(), callback.getMaxTextureSize())
                .onlyScaleDown()
                .centerInside()
                .memoryPolicy(NO_CACHE, NO_STORE)
                .into(mTarget);
    }

    public void cancelBusy() {
        percent = 0;
        progressBar.setVisibility(View.GONE);
    }

    public void showBusy() {
        percent = 0;
        progressBar.setVisibility(View.VISIBLE);
        loading();
    }

    IMediaPlayer getPlayer() {
        return mMediaPlayerWrapper.getPlayer();
    }

    void playCycle() {
        seekBar.setProgress((int) getPlayer().getCurrentPosition());
        if (getPlayer().isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    Handler loadhandler = new Handler(), handler = new Handler();
    Runnable loadRunnable, runnable;
    int percent = 0;

    void loading() {
        percent = (percent + 10) % 100;
        progressBar.setProgress(percent);
        if (loadingBool) {
            loadRunnable = new Runnable() {
                @Override
                public void run() {
                    loading();
                }
            };
            loadhandler.postDelayed(loadRunnable, 500);
        }
    }


    void downloadHotspots(String id, String key) {
        String response = "";
        switch (Integer.parseInt(id)) {
            case 1:
                response = "{\"hotspots\":[{\"id\":\"1\",\"text\":\"2\",\"yaw\":25.920267219381,\"pitch\":5.0098862414458,\"type\":1},{\"id\":\"2\",\"text\":\"3\",\"yaw\":-15.920267219381,\"pitch\":5.0098862414458,\"type\":1}],\"success\":1}";
                break;
            case 2:
                response = "{\"hotspots\":[{\"id\":\"3\",\"text\":\"1\",\"yaw\":-156.30399964212,\"pitch\":-0.13624313075199,\"type\":1},{\"id\":\"4\",\"text\":\"3\",\"yaw\":-156.30399964212,\"pitch\":-0.13624313075199,\"type\":1},{\"id\":\"5\",\"text\":\"this is the hanging church it is a historical place in Egypt\",\"yaw\":66.052202336034,\"pitch\":2.682307006456,\"type\":0}],\"success\":1}\n";
                break;
            case 3:
                response = "{\"hotspots\":[{\"id\":\"6\",\"text\":\"1\",\"yaw\":-100.92026721938,\"pitch\":5.0098862414458,\"type\":1}],\"success\":1}\n";
                break;
        }

        if (!response.isEmpty()) {
            try {
                JSONObject o = new JSONObject(response);
                int suc = o.getInt("success");
                if (suc == 1) {
                    JSONArray arr = o.getJSONArray("hotspots");
                    Hotspot hotspot;
                    hotspots.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = (JSONObject) arr.get(i);
                        hotspot = new Hotspot(obj.getString("id")
                                , obj.getString("text")
                                , obj.getDouble("yaw")
                                , obj.getDouble("pitch")
                                , obj.getInt("type"));
                        addHotspot(hotspot);
                        hotspots.add(hotspot);
                    }
//                    for (Hotspot h : hotspots) {
//                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference(StaticMembers.MEDIA)
//                                .child(key).child(StaticMembers.HOTSPOTS).push();
//                        dr.setValue(h);
//                        h.setKey(dr.getKey());
//                    }
                    Toast.makeText(getBaseContext(), "Hotspots download complete!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getBaseContext(), o.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(getBaseContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

    }

    void addHotspot(final Hotspot hotspot) {
        double y = 25 * Math.sin(Math.toRadians(hotspot.getPitch()));
        double z = -25 * Math.cos(Math.toRadians(hotspot.getPitch()));
        MDPosition position = MDPosition.newInstance().setY((float) y).setZ((float) z).setYaw((float) hotspot.getPitch())
                .setAngleY((float) (-1 * (hotspot.getYaw() + 90) + 180));
        MDHotspotBuilder builder;
        if (hotspot.getType() == 0) {
            builder = MDHotspotBuilder.create(mImageLoadProvider)
                    .size(4f, 4f)
                    .provider(0, this, R.drawable.sound_icon_inactive)
                    .provider(1, this, R.drawable.sound_icon_active)
                    .provider(10, this, R.drawable.sound_icon_inactive)
                    .provider(11, this, R.drawable.sound_icon_active)
                    .listenClick(new MDVRLibrary.ITouchPickListener() {
                        @Override
                        public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                            if (hitHotspot instanceof MDWidgetPlugin) {
                                MDWidgetPlugin widgetPlugin = (MDWidgetPlugin) hitHotspot;
                                String s = hotspot.getText();
                                if (canspeak && !s.isEmpty()) {
                                    textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
                                }
                                widgetPlugin.setChecked(!widgetPlugin.getChecked());
                            }
                        }
                    })
                    .title("text to speech")
                    .position(position)
                    .status(0, 1)
                    .checkedStatus(10, 11);
        } else {
            builder = MDHotspotBuilder.create(mImageLoadProvider)
                    .size(4f, 4f)
                    .provider(0, this, R.drawable.panorama2incative)
                    .provider(1, this, R.drawable.panorama2)
                    .provider(10, this, R.drawable.panorama2incative)
                    .provider(11, this, R.drawable.panorama2)
                    .listenClick(new MDVRLibrary.ITouchPickListener() {
                        @Override
                        public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                            if (hitHotspot instanceof MDWidgetPlugin) {
                                MDWidgetPlugin widgetPlugin = (MDWidgetPlugin) hitHotspot;
                                if (!mediaList.isEmpty()) {
                                    for (int i = 0; i < mediaList.size(); i++) {
                                        currentMedia = mediaList.get(i);
                                        if (currentMedia.getId().equals(hotspot.getText())) {
                                            turn = i;
                                            downloadHotspots(currentMedia.getId(), currentMedia.getKey());
                                            indicator.setText(String.format(Locale.getDefault(), "%d ==> %d", turn + 1, mediaList.size()));
                                            if (currentMedia.getType() == 0) {
                                                imageStart();
                                                mVRLibrary.notifyPlayerChanged();
                                            } else {
                                                videoStart();
                                                Intent mServiceIntent = new Intent(getBaseContext(), VideoDownloadService.class);
                                                mServiceIntent.putExtra(URL, currentMedia.getLink());
                                                mServiceIntent.putExtra(ID, currentMedia.getId());
                                                startService(mServiceIntent);
                                                isReady = true;
                                            }
                                            updateRightLeft();
                                            break;
                                        }
                                    }
                                }
                                widgetPlugin.setChecked(!widgetPlugin.getChecked());
                            }
                        }
                    })
                    .title("image")
                    .position(position)
                    .status(0, 1)
                    .checkedStatus(10, 11);
        }

        MDWidgetPlugin plugin = new MDWidgetPlugin(builder);
        plugins.add(plugin);
        mVRLibrary.addPlugin(plugin);
        //Toast.makeText(this, "add plugin position:" + position, Toast.LENGTH_SHORT).show();
    }

    void removePlugins() {
        plugins.clear();
        mVRLibrary.removePlugins();
    }

    private class ImageLoadProvider implements MDVRLibrary.IImageLoadProvider {

        private SimpleArrayMap<Uri, Target> targetMap = new SimpleArrayMap<>();

        @Override
        public void onProvideBitmap(final Uri uri, final MD360BitmapTexture.Callback callback) {

            final Target target = new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // texture
                    callback.texture(bitmap);
                    targetMap.remove(uri);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    targetMap.remove(uri);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            targetMap.put(uri, target);

            Picasso.with(getApplicationContext())
                    .load(uri)
                    .resize(callback.getMaxTextureSize(), callback.getMaxTextureSize())
                    .onlyScaleDown().centerInside()
                    .memoryPolicy(NO_CACHE, NO_STORE).into(target);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int t = intent.getIntExtra(Constants.TYPE, 0);
            if (currentMedia.getType() > 0)
                if (t == (Constants.TYPE_RESULT) && intent.getIntExtra(Constants.RESULT, 0) == Activity.RESULT_OK) {
                    outFilePath = intent.getStringExtra(Constants.FILE);
                    isPlaying = true;
                    setVideoUri(outFilePath, 0);
                    isReady = true;
                } else if (t == (Constants.TYPE_PROGRESS)) {
                    progressBar.setProgress(intent.getIntExtra(Constants.PROGRESS, 0));
                    isPlaying = false;
                    isReady = false;
                }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mVRLibrary != null)
            mVRLibrary.onPause(this);
        if (mVideoLib != null)
            mVideoLib.onPause(this);
        if (mMediaPlayerWrapper.getPlayer() != null) {
            IMediaPlayer player = mMediaPlayerWrapper.getPlayer();
            player.pause();
        }
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVRLibrary != null)
            mVRLibrary.onResume(this);
        if (mVideoLib != null)
            mVideoLib.onResume(this);
        playButton.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
        registerReceiver(broadcastReceiver, new IntentFilter(
                Constants.NOTIFICATION));
        isReady = true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVRLibrary != null)
            mVRLibrary.onOrientationChanged(this);
        if (mVideoLib != null)
            mVideoLib.onOrientationChanged(this);
    }

    void setVideoUri(String uri, final long seek) {
        mMediaPlayerWrapper.openRemoteFile(uri);
        mMediaPlayerWrapper.prepare();
        if (isPlaying) {
            getPlayer().start();
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getPlayer() != null)
                    getPlayer().seekTo(seek);
                seekBar.setProgress((int) seek);
            }
        }, 250);
        cancelBusy();
        mVideoLib.notifyPlayerChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentMedia != null) {

            outState.putParcelableArrayList(Constants.CURRRENT_MED, mediaList);
            outState.putInt(Constants.TURN_MED, turn);
            if (currentMedia.getType() == 0) {
                outState.putParcelableArrayList(Constants.CURRRENT_HS, hotspots);
            } else {
                outState.putLong(SEEK, getPlayer().getCurrentPosition());
                outState.putString(FILE, outFilePath);
            }
        }
        isReady = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVRLibrary != null)
            mVRLibrary.onDestroy();
        if (mVideoLib != null)
            mVideoLib.onDestroy();
        mMediaPlayerWrapper.destroy();
        handler.removeCallbacks(runnable);
        loadhandler.removeCallbacks(loadRunnable);
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment fragment = DetailsFragment.newInstance(tour);
        adapter.addFrag(fragment, "Details");
        fragment = PlacesFragment.newInstance(tour);
        adapter.addFrag(fragment, "Places");
        fragment = ReviewsFragment.newInstance(tour);
        adapter.addFrag(fragment, "Reviews");
        viewPager.setAdapter(adapter);
    }
}
