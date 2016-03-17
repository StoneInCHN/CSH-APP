package com.cheweishi.android.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.Speed;
import com.cheweishi.android.entity.SubSpeed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 
 * @author 阳光小强 http://blog.csdn.net/dawanganban
 * 
 */
public class AvgSpeedViewY extends View {
	private int XPoint = 80;
	private int YPoint = 100;
	private int XScale = 80;
	private int YScale = 40;
	private int XLength = 960;
	private int YLength = 200;
	private int everyheight = 10;
	// 定义并初始化要绘的图形的坐标
	private float x = 0;
	private float y = 0;
	private float speedx = 50;
	private float speedy = 50;
	// 定义并初始化坐标移动的变化量
	private float addx = 2;
	private float addy = 2;
	private Paint paint = new Paint();
	private Timer timer;
	// TimerTask一个抽象类，它的子类代表一个可以被Timer计划的任务
	private TimerTask task;
	private int MaxDataSize = XLength / XScale;
	private int index;
	private List<Integer> data = new ArrayList<Integer>();
	private List<Speed> listSpeed = new ArrayList<Speed>();
	private String[] YLabel = new String[YLength / YScale];
	private boolean flag = false;
	private static int TEXT_SIZE = 34;
	private float RATIO;
	private boolean speedFlag = false;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x1234) {
				AvgSpeedViewY.this.invalidate();

			}
		};
	};

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(XPoint + 3, getScreenHeight() / 4);
	}

	public AvgSpeedViewY(Context context, List<Speed> listSpeed,
			boolean speedFlag) {
		super(context);
		this.speedFlag = speedFlag;
		this.listSpeed = listSpeed;
		// getHolder().addCallback(this);
		setBackgroundColor(Color.rgb(245, 245, 245));
		if (speedFlag == false) {
			for (int i = 0; i < YLabel.length; i++) {
				YLabel[i] = (i * 30) + "";
			}
		} else {
			for (int i = 0; i < YLabel.length; i++) {
				YLabel[i] = (i * 5) + "";
			}
		}
		// setBackgroundColor(Color.rgb(255, 255, 255));
		// setZOrderOnTop(true);
		// getHolder().setFormat(PixelFormat.TRANSPARENT);
		YPoint = getScreenHeight() / 4- 80;
		YLength = getScreenHeight() / 4 - 100;
		XPoint = getScreenWidth() / 10;
		YScale = YLength / 5;
		XLength = getScreenWidth() * 4 / 3;
		int more = XLength % 4;
		// Toast.makeText(getContext(), more+"", Toast.LENGTH_LONG).show();
		XLength += 1;
		XScale = XLength / (6 * 30);
		MaxDataSize = 2 * XLength / XScale + 1;

	}

	private int getScreenHeight() {

		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		((Activity) getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);

		int screenWidth = dm.widthPixels;

		int screenHeigh = dm.heightPixels;
		return screenHeigh;
	}

	private int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		((Activity) getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);

		int screenWidth = dm.widthPixels;

		int screenHeigh = dm.heightPixels;
		// XScale =
		return screenWidth;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int screenWidth = getScreenWidth();
		int screenHeight = getScreenHeight();
		// Toast.makeText(context, screenWidth + "_" + screenHeight,
		// Toast.LENGTH_LONG).show();
		float ratioWidth = (float) screenWidth / 720;
		float ratioHeight = (float) screenHeight / 1280;

		RATIO = Math.min(ratioWidth, ratioHeight);
		TEXT_SIZE = Math.round(28 * RATIO);
		if (data.size() > 0) {
			ppX = XPoint + 0 * XScale;
			ppY = YPoint - data.get(0) * YScale;
		}
		// Canvas canvas = getHolder().lockCanvas();
		if (canvas != null) {
			canvas.drawColor(getContext().getResources().getColor(
					R.color.gray_backgroud));
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.STROKE);
			paint.setAntiAlias(true); //
			paint.setColor(Color.BLACK);
			paint.setTextSize(TEXT_SIZE);
			// paint.setStrokeWidth(5);
			// paint.setTextSize(34);
			// if (flag == false) {
			// 画Y轴
			canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint);

			// Y轴箭头
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			// canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint
			// - YLength + 6, paint); // 箭头
			// canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint
			// - YLength + 6, paint);
			Path path = new Path();
			path.moveTo(XPoint, YPoint - YLength);
			path.lineTo(XPoint - 3, YPoint - YLength + 6);
			path.lineTo(XPoint + 3, YPoint - YLength + 6);
			path.lineTo(XPoint, YPoint - YLength);
			// canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint
			// - YLength + 6, paint); // 箭头
			// canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint
			// - YLength + 6, paint);
			canvas.drawPath(path, paint);
			// Toast.makeText(getContext(), text, duration)
			// 添加刻度和文字
			paint.setStyle(Paint.Style.STROKE);
			for (int i = 0; i * YScale < YLength; i++) {
				// canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5,
				// YPoint
				// - i * YScale, paint); // 刻度
				paint.setColor(getContext().getResources().getColor(
						R.color.gray_normal));
				canvas.drawText(YLabel[i],
						XPoint - getStringWidth(YLabel[i], paint) - 5, YPoint
								- i * YScale + getStringHeight(YLabel[i]) / 2,
						paint);// 文字
			}
			paint.setTextSize(TEXT_SIZE / 2);
			if (speedFlag == false) {
				canvas.drawText("(km/h)",
						XPoint - getStringWidth("(km/h)", paint) - 5, YPoint
								- YLength, paint);// 文字
			} else {
				canvas.drawText("(L/百公里)",
						XPoint - getStringWidth("(L/百公里)", paint) - 5, YPoint
								- YLength, paint);// 文字
			}
			paint.setTextSize(TEXT_SIZE);
			paint.setColor(getContext().getResources().getColor(R.color.black));
			// // 画X轴
			canvas.drawLine(XPoint, YPoint, XPoint + 3, YPoint, paint);
		}
		// Paint paint = new Paint();
		// paint.setStyle(Paint.Style.STROKE);
		// paint.setAntiAlias(true); // 去锯齿
		// paint.setColor(Color.BLUE);
		// if (flag == false) {
		// // 画Y轴
		// canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint);
		//
		// // Y轴箭头
		// canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint
		// - YLength + 6, paint); // 箭头
		// canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint
		// - YLength + 6, paint);
		// // Toast.makeText(getContext(), text, duration)
		// // 添加刻度和文字
		// for (int i = 0; i * YScale < YLength; i++) {
		// canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint
		// - i * YScale, paint); // 刻度
		//
		// canvas.drawText(YLabel[i], XPoint - 50, YPoint - i * YScale,
		// paint);// 文字
		// }
		//
		// // 画X轴
		// canvas.drawLine(XPoint, YPoint, XPoint + XLength + XScale * 60,
		// YPoint, paint);
		// // Y轴箭头
		// canvas.drawLine(XPoint + XLength + 6 + XScale * 60, YPoint, XPoint
		// + XLength + XScale * 60, YPoint + 3, paint); // 箭头
		// canvas.drawLine(XPoint + XLength + 6 + XScale * 60, YPoint, XPoint
		// + XLength + XScale * 60, YPoint - 3, paint);
		// for (int i = 0; i * XScale < XLength; i++) {
		// // Toast.makeText(getContext(), XPoint + i
		// // * XScale+"", Toast.LENGTH_LONG).show();
		// if (i % 60 == 0) {
		// canvas.drawLine(XPoint + i * XScale, (YPoint), XPoint + i
		// * XScale, (YPoint) - 5, paint); // 刻度
		//
		// canvas.drawText("" + i / 10 + "时", XPoint + i * XScale,
		// YPoint + 50, paint);// 文字
		// }
		// }
		// // 绘折线
		// /*
		// * if(data.size() > 1){ for(int i=1; i<data.size(); i++){
		// * canvas.drawLine(XPoint + (i-1) * XScale, YPoint - data.get(i-1) *
		// * YScale, XPoint + i * XScale, YPoint - data.get(i) * YScale,
		// * paint); } }
		// */
		// paint.setColor(Color.GREEN);
		// paint.setStrokeWidth(3);
		//
		// // Paint paint2 = new Paint();
		// // paint2.setColor(Color.BLUE);
		// // paint2.setStyle(Paint.Style.FILL);
		// if (data.size() > 1) {
		//
		// for (int i = 0; i < data.size() - 1; i++) {
		// // Toast.makeText(getContext(), YPoint - data.get(i) *
		// // YScale+"", Toast.LENGTH_LONG).show();
		// Path path = new Path();
		// // Path path2 = new Path();
		// path.moveTo(XPoint, YPoint - data.get(i) * YScale);
		// // path2.moveTo(XPoint, YPoint);
		// if ((YPoint - data.get(i) * YScale) <= 250
		// && ((YPoint - data.get(i + 1) * YScale) <= 250)) {
		// // Paint paint = new Paint();
		// paint.setStyle(Paint.Style.STROKE);
		// paint.setAntiAlias(true); //
		// paint.setColor(Color.RED);
		// // Toast.makeText(getContext(), "good",
		// // Toast.LENGTH_LONG).show();
		// path.lineTo(XPoint + (i + 1) * XScale,
		// YPoint - data.get(i) * YScale);
		// ppX = XPoint + (i + 1) * XScale;
		// ppY = YPoint - data.get(i) * YScale;
		// canvas.drawPath(path, paint);
		// // paint.setColor(Color.RED);
		// } else {
		// if ((YPoint - data.get(i) * YScale) <=300
		// && ((YPoint - data.get(i + 1) * YScale) <=300)) {
		// paint.setStyle(Paint.Style.STROKE);
		// paint.setAntiAlias(true); //
		// paint.setColor(Color.YELLOW);
		// // Toast.makeText(getContext(), "good",
		// // Toast.LENGTH_LONG).show();
		// ppX = XPoint + (i + 1) * XScale;
		// ppY = YPoint - data.get(i) * YScale;
		// path.lineTo(XPoint + (i + 1) * XScale, YPoint
		// - data.get(i) * YScale);
		// canvas.drawPath(path, paint);
		// } else {
		// paint.setStyle(Paint.Style.STROKE);
		// paint.setAntiAlias(true); //
		// paint.setColor(Color.GREEN);
		// // paint.setStyle(Paint.Style.STROKE);
		// // paint.setAntiAlias(true); //
		// // paint.setColor(Color.YELLOW);
		// // Toast.makeText(getContext(), "good",
		// // Toast.LENGTH_LONG).show();
		// ppX = XPoint + (i + 1) * XScale;
		// ppY = YPoint - data.get(i) * YScale;
		// path.lineTo(XPoint + (i + 1) * XScale, YPoint
		// - data.get(i) * YScale);
		// canvas.drawPath(path, paint);
		// }
		// }
		//
		// // if(i == 5){
		// // break;
		// // }
		// // canvas.drawPath(path2, paint2);
		// // path2.lineTo(XPoint + i * XScale, YPoint - data.get(i) *
		// // YScale);
		// }
		// // path2.lineTo(XPoint + (data.size() - 1) * XScale, YPoint);
		// // canvas.drawPath(path, paint);
		// // canvas.drawPath(path2, paint2);
		// }
		// }
		// if (flag == true) {
		//
		// Path path = new Path();
		// path.moveTo(XPoint + XLength / 2, YPoint - YLength);
		// path.lineTo(XPoint + XLength / 2 + 50, YPoint - YLength - 50);
		// canvas.drawPath(path, paint);
		// path.moveTo(XPoint + XLength / 2 + 50, YPoint - YLength - 50);
		// path.lineTo(XPoint + XLength / 2 + 150, YPoint - YLength - 50);
		// canvas.drawPath(path, paint);
		// paint.setColor(Color.YELLOW);
		// paint.setStrokeWidth(1);
		// paint.setTextSize(30);
		// canvas.drawText("haha", XPoint + XLength / 2 + 50, YPoint - YLength
		// - 50 - 10, paint);
		// canvas.drawText("haha", XPoint + XLength / 2 + 50, YPoint - YLength
		// - 50 + 30, paint);
		// RectF rf = new RectF(XPoint + XLength / 2 - 5,
		// YPoint - YLength - 5, XPoint + XLength / 2 + 5, YPoint
		// - YLength + 5);
		// // canvas.drawCircle(XPoint + XLength / 2, YPoint - YLength, 5,
		// // paint);
		// paint.setStyle(Paint.Style.FILL);
		// canvas.drawOval(rf, paint);
		// }
		// canvas.drawB
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// Toast.makeText(getContext(), "good",
		// Toast.LENGTH_LONG).show();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x1 = (int) event.getX();
			int y1 = (int) event.getY();
			System.out.println(x1 + "_" + y1);
			for (int i = 0; i < data.size() - 1; i++) {
				if (x1 <= (XPoint + (i + 1) * XScale)
						&& x1 >= (XPoint + (i) * XScale)) {
					if (y1 >= (YPoint - data.get(i + 1) * YScale)
							&& y1 >= (YPoint - data.get(i) * YScale)
							&& y1 < YPoint) {
						flag = true;
						x = 0;
						y = 0;
						AvgSpeedViewY.this.invalidate();
						// stopTimer();
						// startTimer();
						// handler.sendEmptyMessage(0x1234);
						// draw();
						Toast.makeText(getContext(), "good", Toast.LENGTH_LONG)
								.show();
					}
				}
			}
			// handler.sendEmptyMessage(0x1234);
			// for (int i = 0; i < data.size() - 1; i++) {
			// System.out.println((XPoint + (i) * XScale) + "_"
			// + (YPoint - data.get(i) * YScale) + "--"
			// + (XPoint + (i + 1) * XScale) + "_"
			// + (YPoint - data.get(i + 1) * YScale));
			// if ((YPoint - data.get(i) * YScale) <= (YPoint - data
			// .get(i + 1) * YScale)) {
			// if (x <= (XPoint + (i + 1) * XScale)
			// && x >= (XPoint + (i) * XScale)) {
			// if (y <= (YPoint - data.get(i + 1) * YScale)
			// && y >= (YPoint - data.get(i) * YScale)) {
			// Toast.makeText(getContext(), "good",
			// Toast.LENGTH_LONG).show();
			// }
			// }
			// } else {
			// if (x <= (XPoint + (i + 1) * XScale)
			// && x >= (XPoint + (i) * XScale)) {
			// if (y >= (YPoint - data.get(i + 1) * YScale)
			// && y <= (YPoint - data.get(i) * YScale)) {
			// Toast.makeText(getContext(), "good",
			// Toast.LENGTH_LONG).show();
			// }
			// }
			// }
			// // path.lineTo(XPoint + i * XScale, YPoint - data.get(i) *
			// // YScale);
			// // path2.lineTo(XPoint + i * XScale, YPoint - data.get(i) *
			// // YScale);
			// }
			// Toast.makeText(getContext(), x + "_" + y,
			// Toast.LENGTH_LONG).show();
			break;
		}
		return super.onTouchEvent(event);
	}

	private int ppX;
	private int ppY;

	private int getStringWidth(String str, Paint paint) {
		return (int) paint.measureText(str);
	}

	private int getStringHeight(String str) {
		FontMetrics fr = paint.getFontMetrics();
		return (int) Math.ceil(fr.descent - fr.top) + 2; // ceil()
															// 函数向上舍入为最接近的整数。

	}

	/**
	 * 绘图方法，在其中完成具体的过程
	 */
	// public void draw() {
	//
	// if (data.size() > 0) {
	// ppX = XPoint + 0 * XScale;
	// ppY = YPoint - data.get(0) * YScale;
	// }
	// Canvas canvas = getHolder().lockCanvas();
	// if (canvas != null) {
	// canvas.drawColor(Color.WHITE);
	// Paint paint = new Paint();
	// paint.setStyle(Paint.Style.STROKE);
	// paint.setAntiAlias(true); //
	// paint.setColor(Color.BLACK);
	// // paint.setStrokeWidth(5);
	// paint.setTextSize(34);
	// // if (flag == false) {
	// // 画Y轴
	// canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint);
	//
	// // Y轴箭头
	// canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint
	// - YLength + 6, paint); // 箭头
	// canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint
	// - YLength + 6, paint);
	// // Toast.makeText(getContext(), text, duration)
	// // 添加刻度和文字
	// for (int i = 0; i * YScale < YLength; i++) {
	// // canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint
	// // - i * YScale, paint); // 刻度
	// paint.setColor(getContext().getResources().getColor(R.color.gray_normal));
	// canvas.drawText(YLabel[i],
	// XPoint - getStringWidth(YLabel[i], paint) - 5, YPoint
	// - i * YScale + getStringHeight(YLabel[i]) / 2,
	// paint);// 文字
	// }
	// paint.setColor(getContext().getResources().getColor(R.color.black));
	// // // 画X轴
	// canvas.drawLine(XPoint, YPoint, XPoint + 3,
	// YPoint, paint);
	// // // Y轴箭头
	// // canvas.drawLine(XPoint + XLength + 6 + XScale * 60, YPoint, XPoint
	// // + XLength + XScale * 60, YPoint + 3, paint); // 箭头
	// // canvas.drawLine(XPoint + XLength + 6 + XScale * 60, YPoint, XPoint
	// // + XLength + XScale * 60, YPoint - 3, paint);
	// // for (int i = 0; i * XScale < XLength; i++) {
	// // // Toast.makeText(getContext(), XPoint + i
	// // // * XScale+"", Toast.LENGTH_LONG).show();
	// // if (i % 60 == 0) {
	// // canvas.drawLine(XPoint + i * XScale, (YPoint), XPoint + i
	// // * XScale, (YPoint) - 5, paint); // 刻度
	// //
	// // canvas.drawText("" + i / 10 + "时", XPoint + i * XScale,
	// // YPoint + 50, paint);// 文字
	// // }
	// // }
	// // 绘折线
	// /*
	// * if(data.size() > 1){ for(int i=1; i<data.size(); i++){
	// * canvas.drawLine(XPoint + (i-1) * XScale, YPoint - data.get(i-1) *
	// * YScale, XPoint + i * XScale, YPoint - data.get(i) * YScale,
	// * paint); } }
	// */
	// // paint.setColor(Color.GREEN);
	// // paint.setStrokeWidth(3);
	// //
	// // Paint paint2 = new Paint();
	// // paint2.setColor(Color.BLUE);
	// // paint2.setStyle(Paint.Style.FILL);
	// // if (listSpeed.size() > 0) {
	// // for (int i = 0; i < listSpeed.size(); i++) {
	// // if (listSpeed.get(i).getListSubSpeed() != null
	// // && listSpeed.get(i).getListSubSpeed().size() > 1) {
	// // // Toast.makeText(getContext(), "goo" +
	// // // listSpeed.get(i).getListSubSpeed().size(),
	// // // Toast.LENGTH_LONG).show();
	// // System.out.println("goo"
	// // + listSpeed.get(i).getListSubSpeed().size());
	// // for (int j = 0; j < listSpeed.get(i).getListSubSpeed()
	// // .size() - 1; j++) {
	// // // Toast.makeText(getContext(), "goo",
	// // // Toast.LENGTH_LONG).show();
	// // // Toast.makeText(getContext(), YPoint - data.get(i)
	// // // *
	// // // YScale+"", Toast.LENGTH_LONG).show();
	// // ppX = XPoint
	// // + (int) (listSpeed.get(i).getListSubSpeed()
	// // .get(j).getTime() / 3600) * XScale;
	// // ppY = YPoint
	// // - (int) (listSpeed.get(i).getListSubSpeed()
	// // .get(j).getSpeed() / 30) * YScale;
	// // Path path = new Path();
	// // Path path2 = new Path();
	// // path.moveTo(ppX, ppY);
	// // // path.moveTo(XPoint, YPoint - data.get(0) *
	// // // YScale);
	// // path2.moveTo(XPoint, YPoint);
	// // if (listSpeed.get(i).getListSubSpeed().get(j)
	// // .getSpeed() >= 120
	// // && listSpeed.get(i).getListSubSpeed()
	// // .get(j + 1).getSpeed() >= 120) {
	// // // Toast.makeText(getContext(), "goo1",
	// // // Toast.LENGTH_LONG).show();
	// // System.out.println("good1");
	// // // Paint paint = new Paint();
	// // paint.setStyle(Paint.Style.STROKE);
	// // paint.setAntiAlias(true); //
	// // paint.setColor(Color.RED);
	// // // Toast.makeText(getContext(), "good",
	// // // Toast.LENGTH_LONG).show();
	// // path.lineTo(XPoint
	// // + (listSpeed.get(i).getListSubSpeed()
	// // .get(j + 1).getTime() / 3600)
	// // * XScale, YPoint
	// // - (listSpeed.get(i).getListSubSpeed()
	// // .get(j + 1).getSpeed() / 30)
	// // * YScale);
	// // // ppX = XPoint + (i + 1) * XScale;
	// // // ppY = YPoint - data.get(i + 1) * YScale;
	// // canvas.drawPath(path, paint);
	// //
	// // // paint.setColor(Color.RED);
	// // } else {
	// // // Toast.makeText(getContext(), "goo2",
	// // // Toast.LENGTH_LONG).show();
	// // System.out.println("good2");
	// // if (listSpeed.get(i).getListSubSpeed().get(j)
	// // .getSpeed() <= 30
	// // && listSpeed.get(i).getListSubSpeed()
	// // .get(j + 1).getSpeed() <= 30) {
	// // paint.setStyle(Paint.Style.STROKE);
	// // paint.setAntiAlias(true); //
	// // paint.setColor(Color.YELLOW);
	// // // Toast.makeText(getContext(), "good",
	// // // Toast.LENGTH_LONG).show();
	// // // ppX = XPoint + (i + 1) * XScale;
	// // // ppY = YPoint - data.get(i + 1) * YScale;
	// // path.lineTo(
	// // XPoint
	// // + (listSpeed.get(i)
	// // .getListSubSpeed()
	// // .get(j + 1)
	// // .getTime() / 3600)
	// // * XScale, YPoint
	// // - (listSpeed.get(i)
	// // .getListSubSpeed()
	// // .get(j + 1)
	// // .getSpeed() / 30)
	// // * YScale);
	// // canvas.drawPath(path, paint);
	// //
	// // } else {
	// // // Toast.makeText(getContext(), "goo3",
	// // // Toast.LENGTH_LONG).show();
	// // System.out.println("good3");
	// // paint.setStyle(Paint.Style.STROKE);
	// // paint.setAntiAlias(true); //
	// // paint.setColor(Color.GREEN);
	// // // paint.setStyle(Paint.Style.STROKE);
	// // // paint.setAntiAlias(true); //
	// // // paint.setColor(Color.YELLOW);
	// // // Toast.makeText(getContext(), "good",
	// // // Toast.LENGTH_LONG).show();
	// // path.lineTo(
	// // XPoint
	// // + (listSpeed.get(i)
	// // .getListSubSpeed()
	// // .get(j + 1)
	// // .getTime() / 3600)
	// // * XScale, YPoint
	// // - (listSpeed.get(i)
	// // .getListSubSpeed()
	// // .get(j + 1)
	// // .getSpeed() / 30)
	// // * YScale);
	// // canvas.drawPath(path, paint);
	// //
	// // }
	// // }
	// // }
	// // }
	// // }
	// // }
	// // if (data.size() > 1) {
	// //
	// // for (int i = 0; i < data.size() - 1; i++) {
	// // // Toast.makeText(getContext(), YPoint - data.get(i) *
	// // // YScale+"", Toast.LENGTH_LONG).show();
	// // Path path = new Path();
	// // Path path2 = new Path();
	// // path.moveTo(ppX, ppY);
	// // // path.moveTo(XPoint, YPoint - data.get(0) * YScale);
	// // // path2.moveTo(XPoint, YPoint);
	// // if (data.get(i) >= 2 && data.get(i + 1) >= 2) {
	// // // Paint paint = new Paint();
	// // paint.setStyle(Paint.Style.STROKE);
	// // paint.setAntiAlias(true); //
	// // paint.setColor(Color.RED);
	// // // Toast.makeText(getContext(), "good",
	// // // Toast.LENGTH_LONG).show();
	// // path.lineTo(XPoint + (i + 1) * XScale,
	// // YPoint - data.get(i + 1) * YScale);
	// // ppX = XPoint + (i + 1) * XScale;
	// // ppY = YPoint - data.get(i + 1) * YScale;
	// // canvas.drawPath(path, paint);
	// // // paint.setColor(Color.RED);
	// // } else {
	// // if (data.get(i) <= 1 && data.get(i + 1) <= 1) {
	// // paint.setStyle(Paint.Style.STROKE);
	// // paint.setAntiAlias(true); //
	// // paint.setColor(Color.YELLOW);
	// // // Toast.makeText(getContext(), "good",
	// // // Toast.LENGTH_LONG).show();
	// // ppX = XPoint + (i + 1) * XScale;
	// // ppY = YPoint - data.get(i + 1) * YScale;
	// // path.lineTo(XPoint + (i + 1) * XScale, YPoint
	// // - data.get(i + 1) * YScale);
	// // canvas.drawPath(path, paint);
	// // } else {
	// // paint.setStyle(Paint.Style.STROKE);
	// // paint.setAntiAlias(true); //
	// // paint.setColor(Color.GREEN);
	// // // paint.setStyle(Paint.Style.STROKE);
	// // // paint.setAntiAlias(true); //
	// // // paint.setColor(Color.YELLOW);
	// // // Toast.makeText(getContext(), "good",
	// // // Toast.LENGTH_LONG).show();
	// // ppX = XPoint + (i + 1) * XScale;
	// // ppY = YPoint - data.get(i + 1) * YScale;
	// // path.lineTo(XPoint + (i + 1) * XScale, YPoint
	// // - data.get(i + 1) * YScale);
	// // canvas.drawPath(path, paint);
	// // }
	// // }
	// // // path.lineTo(XPoint + (i + 1) * XScale, YPoint -
	// // // data.get(i +
	// // // 1)
	// // // * YScale);
	// // // canvas.drawPath(path, paint);
	// // // if(i == 5){
	// // // break;
	// // // }
	// // // canvas.drawPath(path2, paint2);
	// // // path2.lineTo(XPoint + i * XScale, YPoint - data.get(i) *
	// // // YScale);
	// // }
	// // // path2.lineTo(XPoint + (data.size() - 1) * XScale, YPoint);
	// // // canvas.drawPath(path, paint);
	// // // canvas.drawPath(path2, paint2);
	// // }
	// // // }
	// // if (flag == true) {
	// //
	// // Path path = new Path();
	// // path.moveTo(XPoint + XLength / 2 + x, YPoint - YLength + y);
	// // path.lineTo(XPoint + XLength / 2 + 50 + x, YPoint - YLength
	// // - 50 + y);
	// // canvas.drawPath(path, paint);
	// // path.moveTo(XPoint + XLength / 2 + 50 + x, YPoint - YLength
	// // - 50 + y);
	// // path.lineTo(XPoint + XLength / 2 + 150 + x, YPoint - YLength
	// // - 50 + y);
	// // canvas.drawPath(path, paint);
	// // paint.setColor(Color.YELLOW);
	// // paint.setStrokeWidth(1);
	// // paint.setTextSize(30);
	// // canvas.drawText("haha", XPoint + XLength / 2 + 50 + x, YPoint
	// // - YLength - 50 - 10 + y, paint);
	// // canvas.drawText("haha", XPoint + XLength / 2 + 50 + x, YPoint
	// // - YLength - 50 + 30 + y, paint);
	// // RectF rf = new RectF(x, y, speedx + x, speedy + y);
	// // // canvas.drawCircle(XPoint + XLength / 2, YPoint - YLength, 5,
	// // // paint);
	// // paint.setStyle(Paint.Style.FILL);
	// // canvas.drawOval(rf, paint);
	// // // 锁定画布
	// //
	// // // 初始化画布
	// // // canvas.drawColor(Color.WHITE);
	// // // 绘制图形，这里画个矩形
	// // canvas.drawRect(x, y, speedx + x, speedy + y, paint);
	// // x += addx;
	// // y += addy;
	// // // 下面是矩形的移动路径
	// // if (x > 300) {
	// // stopTimer();
	// // }
	// // if (x < 0) {
	// // // 如果图形左边界坐标超出左屏幕则向右移动
	// // addx = Math.abs(addx);
	// // }
	// // if (x > getWidth() - speedx) {
	// // // 如果图形右边界坐标超出屏幕的宽度则向左移动
	// // addx = -Math.abs(addx);
	// // }
	// // if (y < 0) {
	// // addy = Math.abs(addy);
	// // }
	// // if (y > getHeight() - speedy) {
	// // addy = -Math.abs(addy);
	// // }
	// // }
	// // // // 锁定画布
	// // //
	// // // // 初始化画布
	// // // canvas.drawColor(Color.WHITE);
	// // // // 绘制图形，这里画个矩形
	// // // canvas.drawRect(x, y, speedx + x, speedy + y, paint);
	// // // x += addx;
	// // // y += addy;
	// // // // 下面是矩形的移动路径
	// // // if (x < 0) {
	// // // // 如果图形左边界坐标超出左屏幕则向右移动
	// // // addx = Math.abs(addx);
	// // // }
	// // // if (x > getWidth() - speedx) {
	// // // // 如果图形右边界坐标超出屏幕的宽度则向左移动
	// // // addx = -Math.abs(addx);
	// // // }
	// // // if (y < 0) {
	// // // addy = Math.abs(addy);
	// // // }
	// // // if (y > getHeight() - speedy) {
	// // // addy = -Math.abs(addy);
	// // // }
	// // // 解锁画布
	// getHolder().unlockCanvasAndPost(canvas);
	// }
	// }

	// /**
	// * 启动定时器后台线程
	// */
	// public void startTimer() {
	// timer = new Timer();
	// task = new TimerTask() {
	// @Override
	// public void run() {
	// // 在定时器线程中调用绘图方法
	// draw();
	// }
	// };
	// // 设置定时器每隔0.1秒启动这个task,实现动画效果
	// timer.schedule(task, 100, 100);
	// }
	//
	// /**
	// * 停止定时器线程的方法
	// */
	// public void stopTimer() {
	// timer.cancel();
	// }
	//
	// @Override
	// public void surfaceCreated(SurfaceHolder holder) {
	// // 一定要在SurfaceView创建之后启动线程
	// startTimer();
	// }
	//
	// @Override
	// public void surfaceChanged(SurfaceHolder holder, int format, int width,
	// int height) {
	// }
	//
	// @Override
	// public void surfaceDestroyed(SurfaceHolder holder) {
	// // 一定要在SurfaceView销毁之前结束线程
	// stopTimer();
	// }
}