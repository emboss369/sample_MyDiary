package org.example.username.mydiary;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SlideshowActivity extends AppCompatActivity {

    protected static final String SLIDESHOW_WAIT_TIME = "slideshow_wait_time";
    protected static final String SLIDESHOW_ANIMATION = "slideshow_animation";
    protected static final String FADE_IN_OUT = "fade_in_out";
    protected static final String SLIDE_IN = "slide_in";
    private Realm mRealm;

    private ImageSwitcher mImageSwitcher;

    private int mPosition = 0;

    private ArrayList<Long> mImageIds = new ArrayList<>();

    private boolean mIsSlildeshow = true;

    private class MainTimerTask extends TimerTask {

        @Override
        public void run() {
            if (mIsSlildeshow) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        movePositon(1);
                    }
                });
            }
        }
    }

    private Timer mTimer;
    private TimerTask mTimerTask = new MainTimerTask();
    private Handler mHandler = new Handler();

    MediaPlayer mMediaPlayer;


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    //private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mImageSwitcher.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slideshow);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);

        // イメージスイッチャー
        mImageSwitcher = (ImageSwitcher) findViewById(R.id.image_switcher);


        // Set up the user interaction to manually show or hide the system UI.
        mImageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.sildeshow_button).setOnTouchListener(mDelayHideTouchListener);

        // realm初期処理
        mRealm = Realm.getDefaultInstance();

        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                return imageView;
            }
        });


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String animationType = pref.getString(SLIDESHOW_ANIMATION, FADE_IN_OUT);


        // アニメーション効果を設定する。
        switch (animationType){
            case FADE_IN_OUT:
                mImageSwitcher.setInAnimation(this,android.R.anim.fade_in);
                mImageSwitcher.setOutAnimation(this,android.R.anim.fade_out);
                break;
            case SLIDE_IN:
                mImageSwitcher.setInAnimation(this,android.R.anim.slide_in_left);
                mImageSwitcher.setOutAnimation(this,android.R.anim.slide_out_right);
                break;
            default:
                mImageSwitcher.clearAnimation();
                break;

        }

        RealmResults<Diary> diaries = mRealm.where(Diary.class).findAll();
        for (Iterator<Diary> i = diaries.iterator(); i.hasNext(); ) {

            Diary d = i.next();
            byte[] bytes = d.getImage();
            if (bytes != null && bytes.length > 0) {
                mImageIds.add(new Long(d.getId()));
            }
        }

        ImageView next = (ImageView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movePositon(1);
            }
        });

        ImageView prev = (ImageView) findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movePositon(-1);
            }
        });

        Button slideshowButton = (Button) findViewById(R.id.sildeshow_button);
        slideshowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsSlildeshow = !mIsSlildeshow;
                if (mIsSlildeshow) {
                    mMediaPlayer.start();
                } else {
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(0);
                }
            }
        });
        movePositon(0);

        mMediaPlayer = MediaPlayer.create(this, R.raw.getdown);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();

        if(mMediaPlayer != null){
            mMediaPlayer.stop();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mImageSwitcher.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    // 画像の変更用メソッド
    private void movePositon(int to) {
        if (mImageIds.size() == 0){
            return;
        }

        mPosition = mPosition + to;

        if (mPosition >= mImageIds.size()) {
            mPosition = 0;
        } else if (mPosition < 0) {
            mPosition = mImageIds.size() - 1;
        }


        Long id = mImageIds.get(mPosition);
        Diary diary = mRealm.where(Diary.class).equalTo("id", id).findFirst();
        if (diary == null) return;
        byte[] bytes = diary.getImage();
        if (bytes != null && bytes.length > 0) {
            Bitmap bitmap = MyUtils.getImageFromByte(bytes);
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            mImageSwitcher.setImageDrawable(drawable);
            setTitle(diary.getTitle());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String waitTimeStr = pref.getString(SLIDESHOW_WAIT_TIME,"3");
        int waitTime = Integer.parseInt(waitTimeStr) * 1000;

        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, waitTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.cancel();

        mMediaPlayer.pause();

    }


}
