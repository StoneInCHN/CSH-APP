package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;

/**
 * @author Tanck
 * @desc 帧动画加载布局
 */
public class TweenAnimLoadingLayout extends LoadingLayout {

    private AnimationDrawable animationDrawable;

    public TweenAnimLoadingLayout(Context context, Mode mode,
                                  Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);
        // 初始化
        mHeaderImage.setImageResource(R.drawable.load_1);
//        animationDrawable = (AnimationDrawable) mHeaderImage.getDrawable();
    }

    // 默认图片
    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.load_1;
    }

    @Override
    protected void onLoadingDrawableSet(Drawable imageDrawable) {
        // NO-OP
//        if (null != imageDrawable) {
//            final int dHeight = imageDrawable.getIntrinsicHeight();
//            final int dWidth = imageDrawable.getIntrinsicWidth();
//
//            Log.d("Tanck", "h:" + dHeight + "---" + "w:" + dWidth);
//            mHeaderImage.setImageResource(R.drawable.load_2);
//        }
    }

    @Override
    protected void onPullImpl(float scaleOfLayout) {
        // NO-OP
        if (1 >= scaleOfLayout) {
            mHeaderImage.setImageResource(R.drawable.load_1);
            return;
        } else if (1.5 >= scaleOfLayout) {
            mHeaderImage.setImageResource(R.drawable.load_2);
            return;
        } else if (2.0 >= scaleOfLayout) {
            mHeaderImage.setImageResource(R.drawable.load_3);
            return;
        } else if (2.5 >= scaleOfLayout) {
            mHeaderImage.setImageResource(R.drawable.load_4);
            return;
        }
    }

    // 下拉以刷新
    @Override
    protected void pullToRefreshImpl() {
        // NO-OP

    }

    // 正在刷新时回调
    @Override
    protected void refreshingImpl() {
        // 播放帧动画
        mHeaderImage.setImageResource(R.drawable.loading_anim);
        animationDrawable = (AnimationDrawable) mHeaderImage.getDrawable();
        animationDrawable.start();
    }

    // 释放以刷新
    @Override
    protected void releaseToRefreshImpl() {
        // NO-OP
//        mHeaderImage.clearAnimation();
    }

    // 重新设置
    @Override
    protected void resetImpl() {
        mHeaderImage.setVisibility(View.VISIBLE);
        mHeaderImage.clearAnimation();
    }
}
