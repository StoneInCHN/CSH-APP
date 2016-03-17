package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.WelcomeAdapter;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.widget.WelcomeGallery;

import cn.jpush.android.api.JPushInterface;

/**
 * 欢迎界面
 *
 * @author mingdasen
 */
public class WelcomeActivity extends BaseActivity {
    public static RelativeLayout rl_wecome;
    private Timer timer = new Timer();
    float firstX = 0;
    float firstY;
    float secondX;
    float secondY;
    private WelcomeGallery gallery = null;
    private ArrayList<Integer> imgList;
    private ArrayList<ImageView> portImg;
    private int preSelImgIndex = 0;
    private LinearLayout ll_focus_indicator_container = null;
    private ImageView immediateExperience;
    public static WelcomeActivity instance;
    public static boolean IsValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        instance = this;
        rl_wecome = (RelativeLayout) findViewById(R.id.rl_welcome);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // View layout = inflater.inflate(imgList.get(position %
        // imgList.size()),
        // null);
        View convertView = inflater.inflate(R.layout.welcome4, null);
        immediateExperience = (ImageView) convertView
                .findViewById(R.id.immediateExperienceBtn);
        if (immediateExperience != null) {
            immediateExperience.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if (isLogined()) {
                        Intent intent = new Intent(WelcomeActivity.this,
                                MainNewActivity.class);
                        intent.putExtra("className", "WelcomeActivity");
                        startActivity(intent);
                        WelcomeActivity.this.finish();
                    } else {
                        Intent intent = new Intent(WelcomeActivity.this,
                                LoginActivity.class);
                        LoginMessageUtils.showDialogFlag = true;
                        intent.putExtra("className", "WelcomeActivity");
                        startActivity(intent);
                        WelcomeActivity.this.finish();
                    }
                }

            });
        }
        SharedPreferences preferences = getSharedPreferences("gallery",
                MODE_PRIVATE);
        if (preferences.getBoolean("galleryFlag", false) == false) {
            Editor editor = preferences.edit();
            editor.putBoolean("galleryFlag", true);
            editor.commit();
            ll_focus_indicator_container = (LinearLayout) findViewById(R.id.ll_focus_indicator_container);
            ll_focus_indicator_container.setVisibility(View.VISIBLE);
            imgList = new ArrayList<Integer>();
            imgList.add(R.layout.welcome1);
            imgList.add(R.layout.welcome2);
            imgList.add(R.layout.welcome3);
            imgList.add(R.layout.welcome4);
            InitFocusIndicatorContainer();
            gallery = (WelcomeGallery) findViewById(R.id.welcomegallery);
            gallery.setAdapter(new WelcomeAdapter(WelcomeActivity.this, imgList));
            gallery.setFocusable(true);
            gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int selIndex, long arg3) {
                    portImg.get(preSelImgIndex).setImageResource(
                            R.drawable.huanyinye_dian);
                    portImg.get(selIndex).setImageResource(
                            R.drawable.huanyinye_click_dian);
                    preSelImgIndex = selIndex;
                    if (selIndex == 2) {
                        immediateExperience.setVisibility(View.VISIBLE);

                    } else {
                        immediateExperience.setVisibility(View.GONE);
                        gallery.setOnTouchListener(null);
                    }
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

        } else {

            Timer timer = new Timer();// timer中有一个线程,这个线程不断执行task
            timer.schedule(task, 1000 * 3);// 设置这个task在延迟三秒之后自动执行
            if (rl_wecome != null) {
                rl_wecome.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                    }
                });
            }
        }

    }

    private void InitFocusIndicatorContainer() {
        portImg = new ArrayList<ImageView>();
        for (int i = 0; i < imgList.size(); i++) {
            ImageView localImageView = new ImageView(WelcomeActivity.this);
            localImageView.setId(i);
            ImageView.ScaleType localScaleType = ImageView.ScaleType.FIT_XY;
            localImageView.setScaleType(localScaleType);
            LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                    36, 36);
            localImageView.setLayoutParams(localLayoutParams);
            localImageView.setPadding(5, 5, 5, 5);
            localImageView.setImageResource(R.drawable.huanyinye_dian);
            portImg.add(localImageView);
            this.ll_focus_indicator_container.addView(localImageView);
        }
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (isLogined()) {
                Intent intent = new Intent(WelcomeActivity.this,
                        MainNewActivity.class);
                intent.putExtra("className", "WelcomeActivity");
                startActivity(intent);
                WelcomeActivity.this.finish();
            } else {
                Intent intent = new Intent(WelcomeActivity.this,
                        LoginActivity.class);
                intent.putExtra("className", "WelcomeActivity");
                LoginMessageUtils.showDialogFlag = true;
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        System.gc();
        if (imgList != null) {
            imgList.clear();
        }
        if (portImg != null) {
            portImg.clear();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(baseContext);
    }


    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(baseContext);
    }
}
