package com.cheweishi.android.widget;

import java.util.HashMap;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * @author com.cheweishi.android.widget.CarReportViewPager
 */
public class CarReportSpeedViewPager extends ViewPager {
	// private float mTrans;
	private float mScale;
	private static final float SCALE_MAX = 0.0001f;

	private HashMap<Integer, View> mChildViews = new LinkedHashMap<Integer, View>();
	private View leftView;
	private View rightView;

	// private Animation anim;

	public CarReportSpeedViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// this.position = position % mChildViews.size();
		// 滑动特别小的距离时，我们认为没有动，可有可无的判断
		float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;

		leftView = mChildViews.get(position);
		rightView = mChildViews.get(position + 1);

		animateStack(leftView, rightView, effectOffset, positionOffsetPixels);
		super.onPageScrolled(position, positionOffset, positionOffsetPixels);
	}

	private boolean isSmall(float positionOffset) {
		return Math.abs(positionOffset) < 0.0001;
	}

	public void setObjectForPosition(View view, int position) {
		mChildViews.put(position, view);
	}

	private void animateStack(View leftView, View rightView,
			float effectOffset, int positionOffsetPixels) {
		if (leftView != null) {

			mScale = 2 * effectOffset >= 1 ? 0 : 1 - 2 * effectOffset;
			if (leftView instanceof ReportCircleView) {
				((ReportCircleView) leftView).resume();
				((ReportCircleView) leftView).setTransOffset(mScale);
				if (effectOffset == 0) {
					((ReportCircleView) leftView).pause();
				}
			}
			// mTrans = -getPageMargin() - effectOffset * positionOffsetPixels /
			// 2;
			// leftView.setScaleX(mScale);
			// leftView.setScaleY(mScale);
			// leftView.setTranslationX(mTrans);

		}
		if (rightView != null) {
			/**
			 * 缩小比例 如果手指从右到左的滑动（切换到后一个）：0.0~1.0，即从一半到最大
			 * 如果手指从左到右的滑动（切换到前一个）：1.0~0，即从最大到一半
			 */
			mScale = effectOffset >= 0.5 ? 2 * (effectOffset - 0.5f) : 0;
			if (rightView instanceof ReportCircleView) {
				((ReportCircleView) rightView).resume();
				((ReportCircleView) rightView).setTransOffset(mScale);
				if (effectOffset == 0) {
					((ReportCircleView) rightView).pause();
				}
			}

			// mScale=effectOffset.

			/**
			 * x偏移量： 如果手指从右到左的滑动（切换到后一个）：0-720 如果手指从左到右的滑动（切换到前一个）：720-0
			 */
			// mTrans = -getPageMargin() - effectOffset * positionOffsetPixels /
			// 2;
			// Log.i("zzqq", "-----getWidth:" + getWidth()
			// + "\n----getPageMargin():" + getPageMargin()
			// + "\n-----positionOffsetPixels:" + positionOffsetPixels);
			// Log.i("zzqq", "----mTrans:" + mTrans);
			// rightView.setScaleX(mScale);
			// rightView.setScaleY(mScale);
		}
		// 好像没什么用
		// if (leftView != null) {
		// leftView.bringToFront();
		// }
	}

	// int position = 1;
	boolean isCircle;
	float originalX;
	float originalY;
	float moveX;
	float moveY;

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		AvgSpeedView circle = (AvgSpeedView) mChildViews.get(1);
		switch (e.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			if (isClickedItem(e, circle)) {
//				circle.setIsTranslate(false);
//				isCircle = true;
//				originalX = e.getX();
//				originalY = e.getY();
//				return true;
//			} else {
//				isCircle = false;
//				circle.setIsTranslate(true);
//			}
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if (isCircle) {
//				circle.resume();
//				moveX = e.getX();
//				moveY = e.getY();
//				circle.setRotateSpeed(calculateDegree(originalX, originalY,
//						moveX, moveY));
//				return true;
//			}
//
//			break;
//		case MotionEvent.ACTION_UP:
//			if (isCircle) {
//				circle.pause();
//				return true;
//			}
//			break;

		default:
			break;
		}

		return super.onTouchEvent(e);

		// return true;
	}

	private boolean isClickedItem(MotionEvent e, ReportCircleView circle) {
		if (circle.suduW < e.getX()
				&& e.getX() < (circle.suduW + circle.bitR * 2)
				&& (circle.suduH) < e.getY()
				&& e.getY() < (circle.suduH + circle.bitR * 2)) {
			return true;
		} else if (circle.bailiW < e.getX()
				&& e.getX() < (circle.bailiW + circle.bitR * 2)
				&& (circle.bailiH) < e.getY()
				&& e.getY() < (circle.bailiH + circle.bitR * 2)) {
			return true;
		} else if (circle.lichengW < e.getX()
				&& e.getX() < (circle.lichengW + circle.bitR * 2)
				&& (circle.lichengH) < e.getY()
				&& e.getY() < (circle.lichengH + circle.bitR * 2)) {
			return true;
		} else if (circle.shijianW < e.getX()
				&& e.getX() < (circle.shijianW + circle.bitR * 2)
				&& (circle.shijianH) < e.getY()
				&& e.getY() < (circle.shijianH + circle.bitR * 2)) {
			return true;
		} else if (circle.youhaoW < e.getX()
				&& e.getX() < (circle.youhaoW + circle.bitR * 2)
				&& (circle.youhaoH) < e.getY()
				&& e.getY() < (circle.youhaoH + circle.bitR * 2)) {
			return true;
		}
		return false;
	}

	/**
	 * 状态栏高度
	 */
	private int decorHeight;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

			View v = ((Activity) getContext()).getWindow().getDecorView();
			Rect outRect = new Rect();
			v.getWindowVisibleDisplayFrame(outRect);
			decorHeight = outRect.height();

			Display display = ((Activity) getContext()).getWindowManager()
					.getDefaultDisplay();
			@SuppressWarnings("deprecation")
			int disHeight = display.getHeight();

			decorHeight = disHeight - decorHeight;
		}
	}

	boolean isRight = true;

	/**
	 * 计算移动的角度 cosAB=(a^2+b^2-c^2)/(2*a*b)
	 * 
	 * @param oX
	 *            originalX
	 * @param oY
	 * @param mX
	 *            moveX
	 * @param mY
	 * @return 计算出的角度 单位（度）
	 */
	private int calculateDegree(float oX, float oY, float mX, float mY) {
		int cX = getWidth() / 2;
		int cY = getHeight() / 2;

		double sideA = Math.sqrt(Math.pow(cX - oX, 2) + Math.pow(cY - oY, 2));
		double sideB = Math.sqrt(Math.pow(cX - mX, 2) + Math.pow(cY - mY, 2));
		double sideC = Math.sqrt(Math.pow(mX - oX, 2) + Math.pow(mY - oY, 2));

		double cosAB = (Math.pow(sideA, 2) + Math.pow(sideB, 2) - Math.pow(
				sideC, 2)) / (2 * sideA * sideB);
		int degree = (int) (Math.acos(cosAB) * 180 / Math.PI);

		if (isLeftRight(oX, oY, mX, mY)) {
			return degree;
		} else {
			return 360 - degree;
		}

	}

	/**
	 * 判断点(mX,mY)是否在向量(oX,oY)到(cX,cY)的左边 ***两点 构成的直线为：
	 * -(cY-oY)/(cX-oX)*x+y=(cX*oY-oX*cY)/(cX-oX)
	 * 
	 * @param oX
	 *            originalX
	 * @param oY
	 * @param mX
	 *            moveX
	 * @param mY
	 * @return true是左边
	 */
	private boolean isLeftRight(float oX, float oY, float mX, float mY) {
		int cX = getWidth() / 2;
		int cY = getHeight() / 2;

		double lineK = -(cY - oY) / (cX - oX);
		double lineB = (cX * oY - oX * cY) / (cX - oX);

		double d = lineK * mX + mY;
		if ((oX - cX) * (d - lineB) > 0) {
			return true;
		} else {
			return false;
		}
	}
}
