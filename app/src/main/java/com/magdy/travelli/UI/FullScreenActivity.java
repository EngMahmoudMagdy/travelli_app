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
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360DirectorFactory;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.asha.vrlib.model.MDHitEvent;
import com.asha.vrlib.model.MDHotspotBuilder;
import com.asha.vrlib.model.MDPinchConfig;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.model.MDRay;
import com.asha.vrlib.plugins.MDAbsPlugin;
import com.asha.vrlib.plugins.MDWidgetPlugin;
import com.asha.vrlib.plugins.hotspot.IMDHotspot;
import com.asha.vrlib.texture.MD360BitmapTexture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magdy.travelli.Adapters.CustomProjectionFactory;
import com.magdy.travelli.Adapters.MediaPlayerWrapper;
import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Hotspot;
import com.magdy.travelli.Data.Media;
import com.magdy.travelli.R;
import com.magdy.travelli.helpers.ImageLoaderHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import static com.magdy.travelli.helpers.StaticMembers.MEDIA;

public class FullScreenActivity extends AppCompatActivity {
    private static final float LOADING_TIME = 1500;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.playbut)
    Button playButton;
    @BindView(R.id.seekbar)
    SeekBar seekBar;
    @BindView(R.id.controllerslayout)
    FrameLayout controllersLayout;
    @BindView(R.id.controllers)
    ImageButton controllers;
    @BindView(R.id.gl_view)
    GLSurfaceView surfaceView;


    private MediaPlayerWrapper mMediaPlayerWrapper;
    private MDVRLibrary.IImageLoadProvider mImageLoadProvider = new ImageLoadProvider();

    private MDVRLibrary mVRLibrary;
    Media currentMedia;
    Hotspot currentHotspot;
    private List<MDAbsPlugin> plugins;

    @BindView(R.id.hotspot_point1)
    TextView hotspot_point1;
    @BindView(R.id.hotspot_point2)
    TextView hotspot_point2;

    @BindView(R.id.progress_layout2)
    RelativeLayout layoutProgress2;

    @BindView(R.id.circle_progress_bar1)
    ProgressBar circleProgress1;

    @BindView(R.id.circle_progress_bar2)
    ProgressBar circleProgress2;

    @BindView(R.id.vr_control)
    LinearLayout vrLinear;

    @BindView(R.id.vr_mode)
    CheckBox vr;

    private boolean loadingBool;
    private int percent;
    private Runnable loadRunnable;
    private Handler loadHandler = new Handler();
    TextToSpeech textToSpeech;
    boolean canspeak = false;
    int interactiveMode = MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Butter knife init....
        ButterKnife.bind(this);

        mMediaPlayerWrapper = new MediaPlayerWrapper();
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
        assert getIntent().getExtras() != null;
        currentMedia = (Media) getIntent().getSerializableExtra(Constants.CURRRENT_MED);
        buttonInit();
        if (currentMedia.getType() == 0) {
            imageStart();
            changeHotspots();
        } else {
            videoStart();
            if (getIntent().getBooleanExtra(Constants.ISREADY, false)) {
                setVideoUri(getIntent().getStringExtra(Constants.URL), getIntent().getLongExtra(Constants.SEEK, 100));
            }
        }
        final LinearLayout progresses = findViewById(R.id.progresses);
        mVRLibrary.setEyePickChangedListener(new MDVRLibrary.IEyePickListener2() {
            @Override
            public void onHotspotHit(MDHitEvent hitEvent) {
                IMDHotspot hotspot = hitEvent.getHotspot();
                long hitTimestamp = hitEvent.getTimestamp();
                progresses.setVisibility(View.GONE);
                if (hotspot != null) {
                    progresses.setVisibility(View.VISIBLE);
                    float p = ((float) (System.currentTimeMillis() - hitTimestamp) / LOADING_TIME) * 100;
                    layoutProgress2.setVisibility(mVRLibrary.getDisplayMode() == MDVRLibrary.DISPLAY_MODE_NORMAL ? View.GONE : View.VISIBLE);
                    circleProgress1.setProgress((int) p);
                    circleProgress2.setProgress((int) p);
                    for (Hotspot hotspot1 : currentMedia.getHotspots()) {
                        if (hotspot1.getKey().equals(hotspot.getTag()))
                            currentHotspot = hotspot1;
                    }
                    if (currentHotspot != null)
                        if (System.currentTimeMillis() - hitTimestamp > 3000) {

                            if (currentHotspot.getType() == 0) {
                                String s = currentHotspot.getText();
                                if (canspeak && !s.isEmpty()) {
                                    textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
                                }
                            } else {
                                FirebaseDatabase.getInstance().getReference(MEDIA).child(currentHotspot.getMediaId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        currentMedia = dataSnapshot.getValue(Media.class);
                                        if (currentMedia != null) {
                                            changeHotspots();
                                            if (currentMedia.getType() == 0) {
                                                imageStart();
                                            } else {
                                                videoStart();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            circleProgress1.setProgress(0);
                            circleProgress2.setProgress(0);
                            progresses.setVisibility(View.GONE);
                            mVRLibrary.resetEyePick();
                        }
                }
            }
        });
        mVRLibrary.switchDisplayMode(this, getIntent().getBooleanExtra(Constants.VR, false) ? MDVRLibrary.DISPLAY_MODE_GLASS : MDVRLibrary.DISPLAY_MODE_NORMAL);
        hotspot_point2.setVisibility(getIntent().getBooleanExtra(Constants.VR, false) ? View.VISIBLE : View.GONE);
        layoutProgress2.setVisibility(getIntent().getBooleanExtra(Constants.VR, false) ? View.VISIBLE : View.GONE);
        vr.setChecked(getIntent().getBooleanExtra(Constants.VR, false));
        if (mMediaPlayerWrapper.getPlayer() != null)
            getPlayer().setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    seekBar.setMax((int) iMediaPlayer.getDuration());
                    cancelBusy();
                    playCycle();
                }
            });
        if (savedInstanceState != null) {
            if (currentMedia.getType() > 0) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPlayer().seekTo(savedInstanceState.getLong(Constants.SEEK));
                        seekBar.setProgress((int) savedInstanceState.getLong(Constants.SEEK));
                    }
                }, 250);
            }
        }

    }

    void makeToast(View view, String text) {
        int x = view.getLeft();
        int y = view.getTop();
        Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.START, x, y);
        toast.show();
    }

    void buttonInit() {
        playButton = findViewById(R.id.playbut);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayerWrapper.getPlayer().isPlaying()) {
                    playButton.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                    getPlayer().pause();
                } else {
                    playButton.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    getPlayer().start();
                }
            }
        });
        controllersLayout = findViewById(R.id.controllerslayout);
        controllersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllersLayout.setVisibility(controllersLayout.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        });
        final ImageButton full = findViewById(R.id.fullscreen);
        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        full.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                makeToast(full, "Exit Full screen");
                return true;
            }
        });
        vr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    hotspot_point2.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    layoutProgress2.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    mVRLibrary.switchDisplayMode(getBaseContext());
                }
            }
        });
        vr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                makeToast(vr, mVRLibrary.getDisplayMode() == MDVRLibrary.DISPLAY_MODE_GLASS ? "VR mode" : "Panorama mode");
                return true;
            }
        });
        final ImageButton motionMode = findViewById(R.id.motion_mode);
        controllers = findViewById(R.id.controllers);
        motionMode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mVRLibrary.getInteractiveMode() == MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH)
                    makeToast(motionMode, "Choose Touch Mode");
                else
                    makeToast(motionMode, "Choose Motion & Touch");
                return true;
            }
        });
        motionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interactiveMode == MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH) {
                    interactiveMode = MDVRLibrary.INTERACTIVE_MODE_TOUCH;
                    motionMode.setImageDrawable(getResources().getDrawable(R.drawable.touch_and_motion_white));
                } else {
                    interactiveMode = MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH;
                    motionMode.setImageDrawable(getResources().getDrawable(R.drawable.touch_white));
                }
                mVRLibrary.switchInteractiveMode(getBaseContext(), interactiveMode);
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
                makeToast(controllers, "Use Controllers");
                return true;
            }
        });
    }

    void imageStart() {
        loadingBool = true;
        controllers.setVisibility(View.GONE);
        controllersLayout.setVisibility(View.INVISIBLE);
        if (mVRLibrary == null) {
            mVRLibrary = MDVRLibrary.with(this)
                    .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                    .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH)
                    .asBitmap(new MDVRLibrary.IBitmapProvider() {
                        @Override
                        public void onProvideBitmap(final MD360BitmapTexture.Callback callback) {
                            if (currentMedia != null && currentMedia.getParts() != null)
                                loadImage(currentMedia.getParts(), callback);
                        }
                    })
                    .listenTouchPick(new MDVRLibrary.ITouchPickListener() {
                        @Override
                        public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                            Log.d("Full", "Ray:" + ray + ", hitHotspot:" + hitHotspot);
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
                    .build(surfaceView);
        }
        mVRLibrary.notifyPlayerChanged();
    }

    private void loadImage(final List<String> urls, final MD360BitmapTexture.Callback callback) {
        Log.d("Full", "load image with max texture size:" + callback.getMaxTextureSize());
        showBusy();

        ///////// load image //////////
        ImageLoaderHelper.loadImageList(this,
                callback, currentMedia.getThumbnail(), urls,
                new ImageLoaderHelper.ImageLoadedListener() {
                    @Override
                    public void onImageTotalFinished() {
                        cancelBusy();
                        loadingBool = false;
                    }

                    @Override
                    public void onImageLoaded(Bitmap bitmap) {
                        mVRLibrary.onTextureResize(bitmap.getWidth(), bitmap.getHeight());
                        callback.texture(bitmap);
                    }
                });
    }

    public void showBusy() {
        percent = 0;
        progressBar.setVisibility(View.VISIBLE);
        loading();
    }

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
            loadHandler.postDelayed(loadRunnable, 500);
        }
    }

    void videoStart() {
        mVRLibrary = MDVRLibrary.with(this)
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
                        return MD360Director.builder().setPitch(90).build();
                    }
                })
                .projectionFactory(new CustomProjectionFactory())
                .barrelDistortionConfig(new BarrelDistortionConfig().setDefaultEnabled(false).setScale(0.95f))
                .build(surfaceView);
        mMediaPlayerWrapper.init();
        mMediaPlayerWrapper.setPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                cancelBusy();
                if (mVRLibrary != null) {
                    mVRLibrary.notifyPlayerChanged();
                }
            }
        });
        getPlayer().setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
                mVRLibrary.onTextureResize(width, height);
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
        setVideoUri(currentMedia.getLink(), 0);
        mVRLibrary.notifyPlayerChanged();
    }

    void setVideoUri(String uri, final long seek) {

        mMediaPlayerWrapper.openRemoteFile(uri);
        mMediaPlayerWrapper.prepare();
        getPlayer().start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getPlayer().seekTo(seek);
                seekBar.setProgress((int) seek);
            }
        }, 500);

        cancelBusy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (currentMedia.getType() > 0) {
                int t = intent.getIntExtra(Constants.TYPE, 0);
                if (t == (Constants.TYPE_RESULT) && intent.getIntExtra(Constants.RESULT, 0) == Activity.RESULT_OK) {
                    setVideoUri(intent.getStringExtra(Constants.FILE), 0);
                } else if (t == (Constants.TYPE_PROGRESS)) {
                    updateProgress(intent.getIntExtra(Constants.PROGRESS, 0));
                }
            }
        }
    };

    void updateProgress(int prog) {
        progressBar.setProgress(prog);
    }

    public void cancelBusy() {
        progressBar.setVisibility(View.GONE);
    }

    IMediaPlayer getPlayer() {
        return mMediaPlayerWrapper.getPlayer();
    }

    Runnable runnable;
    Handler handler = new Handler();

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

    void changeHotspots() {
        removePlugins();
        if (currentMedia != null)
            for (Hotspot hotspot : currentMedia.getHotspots()) {
                addHotspotToView(hotspot);
            }

    }

    void addHotspotToView(final Hotspot hotspot) {
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
                    .tag(hotspot.getKey())
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
                                FirebaseDatabase.getInstance().getReference(MEDIA).child(hotspot.getMediaId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        currentMedia = dataSnapshot.getValue(Media.class);
                                        if (currentMedia != null) {
                                            changeHotspots();
                                            if (currentMedia.getType() == 0) {
                                                imageStart();
                                            } else {
                                                videoStart();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                widgetPlugin.setChecked(!widgetPlugin.getChecked());
                            }
                        }
                    })
                    .tag(hotspot.getKey())
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
                    .into(target);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mVRLibrary.onResume(this);
        playButton.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
        registerReceiver(broadcastReceiver, new IntentFilter(
                Constants.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        mVRLibrary.onPause(this);
        if (mMediaPlayerWrapper != null)
            mMediaPlayerWrapper.pause();
        playButton.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVRLibrary.onDestroy();
        if (mMediaPlayerWrapper != null)
            mMediaPlayerWrapper.destroy();
        handler.removeCallbacks(runnable);
        loadHandler.removeCallbacks(loadRunnable);
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVRLibrary.onOrientationChanged(this);
        if (getPlayer() != null)
            getPlayer().start();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getPlayer() != null)
            outState.putLong(Constants.SEEK, getPlayer().getCurrentPosition());
    }
}
