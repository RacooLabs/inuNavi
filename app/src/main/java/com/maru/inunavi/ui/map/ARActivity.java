package com.maru.inunavi.ui.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.maru.inunavi.MainActivity;
import com.maru.inunavi.R;
import com.unity3d.player.UnityPlayer;


public class ARActivity extends AppCompatActivity {

    public static Context mContext;
    protected UnityPlayer mUnityPlayer;
    private String arJson = "";
    private ImageView ar_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        Intent intent = getIntent();

        if(intent != null){
            arJson = intent.getStringExtra("arJson");
        }

        /*mUnityPlayer = new UnityPlayer(this);
        int glesMode = mUnityPlayer.getSettings().getInt("gles_mode", 1);
        boolean trueColor8888 = false;
        mUnityPlayer.init(glesMode, trueColor8888);

        setContentView(R.layout.map_ar_activity);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.ar_view);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(800, 1200);
        layout.addView(mUnityPlayer.getView(), 0, lp);*/

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().takeSurface(null);
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        getWindow().setFormat(PixelFormat.RGB_565);

        mUnityPlayer = new UnityPlayer(this);
        if (mUnityPlayer.getSettings ().getBoolean ("hide_status_bar", true))
            getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int glesMode = mUnityPlayer.getSettings().getInt("gles_mode", 1);
        boolean trueColor8888 = false;
        mUnityPlayer.init(glesMode, trueColor8888);

        View playerView = mUnityPlayer.getView();

        setContentView(R.layout.map_ar_activity);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.ar_view);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams (ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

        layout.addView(playerView, 0, lp);

        ar_back = findViewById(R.id.ar_back);

        ar_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ARActivity.this, MainActivity.class);
                intent.putExtra("CallType", 1001);
                setResult(Activity.RESULT_OK, intent);
                finish();
                overridePendingTransition(0, 0);

            }
        });

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                mUnityPlayer.UnitySendMessage("GameObject", "dataRecept", arJson);

            }
        };

        handler.sendEmptyMessageDelayed(0,6000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();

    }

    protected void onDestroy ()
    {
        mUnityPlayer.removeAllViews();
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // onPause()/onResume() must be sent to UnityPlayer to enable pause and resource recreation on resume.
    protected void onPause()
    {

        super.onPause();
        mUnityPlayer.pause();
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.onKeyMultiple(event.getKeyCode(), event.getRepeatCount(), event);
        return super.dispatchKeyEvent(event);
    }

}
