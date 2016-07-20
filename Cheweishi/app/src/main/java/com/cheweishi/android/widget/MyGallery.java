package com.cheweishi.android.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

import com.cheweishi.android.utils.LogHelper;

import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("deprecation")
public class MyGallery extends Gallery {


    private static final int timerAnimation = 1;
    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case timerAnimation:
                    int position = getSelectedItemPosition();
//                    LogHelper.d("我在正在滚动:" + position);
                    if (-1 == position) {
                        destroy();
                        return;
                    }
                    onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
//                    if (position >= (getCount() % -1)) {
//                        onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
//                    } else {
//                        onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
//                    }
//                    setSelection((position + 1) % 6, true);
                    break;

                default:
                    break;
            }
        }

        ;
    };

    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        public void run() {
            mHandler.sendEmptyMessage(timerAnimation);
        }
    };

    public MyGallery(Context paramContext) {
        super(paramContext);
//        timer.schedule(task, 3000, 3000);
    }

    public MyGallery(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
//        timer.schedule(task, 3000, 3000);

    }

    public MyGallery(Context paramContext, AttributeSet paramAttributeSet,
                     int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
//        timer.schedule(task, 3000, 3000);

    }

    private boolean isScrollingLeft(MotionEvent paramMotionEvent1,
                                    MotionEvent paramMotionEvent2) {
        float f2 = paramMotionEvent2.getX();
        float f1 = paramMotionEvent1.getX();
        if (f2 > f1)
            return true;
        return false;
    }

    @Override
    public boolean onFling(MotionEvent paramMotionEvent1,
                           MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
        LogHelper.d("onFling");
        int keyCode;
        if (isScrollingLeft(paramMotionEvent1, paramMotionEvent2)) {
            keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
        } else {
            keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(keyCode, null);
        return true;
    }

    public void destroy() {
        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (null != task) {
            task.cancel();
            task = null;
        }
    }

    public void pause() {
        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (null != task) {
            task.cancel();
            task = null;
        }
    }

    public void start() {
        destroy();
        if (null == timer)
            timer = new Timer();
        if (null == task)
            task = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(timerAnimation);
                }
            };
        timer.purge();
        timer.schedule(task, 3000, 3000);
    }


}