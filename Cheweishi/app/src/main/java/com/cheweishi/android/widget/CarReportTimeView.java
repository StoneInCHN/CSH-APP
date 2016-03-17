package com.cheweishi.android.widget;

import java.util.ArrayList;
import java.util.List;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.CarReportTimeInfo;
import com.cheweishi.android.entity.CarReportTimeViewInfo;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.utils.DisplayUtil;
import com.cheweishi.android.utils.StringUtil;

public class CarReportTimeView extends View {
	private Context context;
	private int gray = 0;
	private int gray_background = 0;
	private int yellow = 0;
	private int yellow_background = 0;
//	private int white = 0;
	private float width = 0;// 屏幕宽度
	private float heigth = 0;// 屏幕高度
	private float unitWidth = 0;// 表示一分钟占的像素

	private float kuangLeft = 0;// 外框左边坐标
	private float kuangTop = 0;// 外框上部坐标
	private float kuangRight = 0;// 外框右边坐标
//	private float kuangBottom = 0;// 外框底部坐标
	private float newKuangLeft = 0;// 外框左边坐标
//	private float newKuangTop = 0;// 外框上部坐标
	private float newKuangRight = 0;// 外框右边坐标
//	private float newKuangBottom = 0;// 外框底部坐标
	private float kuangWidth = 0;// 实际框宽
	private float newKuangWidth = 0;// 下一个框的宽
//	private float kuangHeigth = 0;// 实际框宽
//	private float newKuangHeigth = 0;// 下一个框的宽
	private String hisTime = "";// 历史里程数
//	private String startTime = "";// 开始时间
//	private String endTime = ""; // 结束时间

	private float XStartx = 0;// 坐标轴的起点x坐标
	private float XStarty = 0;// 坐标轴的起点y坐标
	private float XStopx = 0;// 坐标轴的起点x坐标
//	private float XStopy = 0;// 坐标轴的起点y坐标
	private float screenScaleY = 0.28f;// 占屏幕比例
	private float hisScaleY = 0.30f;// 历史里程高度比例系数
	private float hisTop = 0;// 历史里程顶部坐标
	private float hisBottom = 0;// 历史里程底部坐标
	private float hisleft = 0;// 历史里程左边坐标
	private float hisRigth = 0;// 历史里程右边坐标
	private float hisWidth = 0;// 历史里程宽度
	private float mobile = 0;// 移动距离
//	private float mobileLeft = 0;// 左边坐标移动距离
//	private float mobileRight = 0;// 右边坐标移动距离
	private List<CarReportTimeViewInfo> timeViewInfoList;// 里程矩形图信息列表
//	private List<CarReportTimeInfo> timeInfoList;// 里程单个里程信息列表
//	private String date = "";// 日期
	private int isX = 0;// 0:表示被点击位置在框左边、1表示被点击位置在框右 、2：表示被点击位置在框范围内
//	private float isY = 0;// 点击事件Y判断
	private float speed = 20;// 移动速度
	private boolean isShow = true;// 是否显示数据说明
	private boolean isHis = false;// 判断点击的是否是历史里程
	// private boolean isRun = false;//判断动画事都在运行
	private int isDown = 0;// 表示点击的是当日里程
	private int j = 0;
	private ValueAnimator animator;
	private boolean isNotData = true;
	private String status = "0";
	/** 0表示全部 ，其他表示单选 */
	private int type = 0;
	private float backgroundRectWidth = 0;
	private float rectAxesWidth = 0;

	public CarReportTimeView(Context context) {
		super(context);
		this.context = context;
		timeViewInfoList = new ArrayList<CarReportTimeViewInfo>();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		width = display.getWidth();
		heigth = display.getHeight();
//		white = context.getResources().getColor(R.color.white);
		yellow = context.getResources().getColor(R.color.main_blue);
		yellow_background = context.getResources().getColor(
				R.color.light_blue);
		gray = context.getResources().getColor(R.color.gray_normal);
		gray_background = context.getResources().getColor(
				R.color.gray_backgroud);
		backgroundRectWidth = width - DisplayUtil.dip2px(context, 20);
		hisBottom = heigth * screenScaleY;
		animator = ValueAnimator.ofFloat(0, 1);
		// animator.setDuration(200);
		// animator.setCurrentPlayTime(200);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				if (isX == 0) {
					speed = mobile / 10;
					kuangRight = (kuangRight - speed);
					if (kuangWidth > newKuangWidth) {
						kuangLeft = (kuangLeft - (speed - (speed
								* (kuangWidth - newKuangWidth) / mobile)));
					} else if (kuangWidth < newKuangWidth) {
						kuangLeft = (kuangLeft - (speed + speed
								* (newKuangWidth - kuangWidth) / mobile));

					} else if (kuangWidth == newKuangWidth) {
						kuangLeft = (kuangLeft - speed);
					}

					if (kuangRight <= newKuangRight + 0.5) {
						animator.cancel();
						kuangLeft = newKuangLeft;
						kuangRight = newKuangRight;
						isShow = true;
					}
				} else if (isX == 1) {
					speed = mobile / 10;
					kuangLeft = kuangLeft + speed;
					if (kuangWidth > newKuangWidth) {
						kuangRight = (kuangRight + (speed - speed
								* (kuangWidth - newKuangWidth) / mobile));
					} else if (kuangWidth < newKuangWidth) {
						kuangRight = (kuangRight + (speed + (speed
								* (newKuangWidth - kuangWidth) / mobile)));
					} else if (kuangWidth == newKuangWidth) {
						kuangRight = (kuangRight + speed);
					}

					if (kuangLeft >= newKuangLeft - 0.5) {
						animator.cancel();
						kuangLeft = newKuangLeft;
						kuangRight = newKuangRight;
						isShow = true;
					}
				} else if (isX == 2) {
					if (animator.isRunning()) {
						animator.cancel();
						isShow = true;
					}
				}
				invalidate();
			}
		});
		animator.setRepeatCount(ValueAnimator.INFINITE);
	}


	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(
				ScreenTools.getScreentWidth((Activity) context) * 2,
				ScreenTools.getScreentHeight((Activity) context) / 3
						- DisplayUtil.dip2px(context, 10));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.i("result", "result==onDraw=CarReportTimeView=");
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		// 绘制背景圆角长方形
		drawRoundRect(canvas, paint);
		if (!isNotData) {
			// 画坐标轴
			drawAxes(canvas, paint);
			// 绘制底部文字
			drawBottomText(canvas, paint);
			if (isHis) {
				// 绘制历史记录
				drawHisRecord(canvas, paint);
			}

			// 绘制当日数据矩形
			drawSamedayRect(canvas, paint);

			// 绘制数据说明
			drawDataExplain(canvas, paint);
		}
		super.onDraw(canvas);
	}

	/**
	 * 绘制横向坐标轴
	 * 
	 * @param canvas
	 * @param paint
	 */
	private void drawAxes(Canvas canvas, Paint paint) {
		paint.setStrokeWidth(2);
		paint.setColor(yellow);
		paint.setStyle(Paint.Style.STROKE);
		RectF rectF = new RectF();
		rectF.left = hisleft;
		rectF.top = hisTop;
		rectF.right = rectAxesWidth;
		rectF.bottom = hisBottom - 2;
		canvas.drawRoundRect(rectF, 5, 5, paint);
	}

	/**
	 * 绘制数据说明
	 * 
	 * @param canvas
	 * @param paint
	 */
	private void drawDataExplain(Canvas canvas, Paint paint) {
		paint.setStyle(Paint.Style.FILL);
		if (isHis == true && isShow == false) {// 历史里程说明
			// 画小圆点
			paint.setColor(gray);
			paint.setStrokeWidth(8);
			canvas.drawCircle(hisleft + hisWidth / 2,
					hisTop - DisplayUtil.dip2px(context, 5),
					DisplayUtil.dip2px(context, 3), paint);
			// 画说明线
			paint.setStrokeWidth(2);

			canvas.drawLine(hisleft + hisWidth / 2,
					hisTop - DisplayUtil.dip2px(context, 5), hisleft + hisWidth
							/ 2 + DisplayUtil.dip2px(context, 10), hisTop
							- DisplayUtil.dip2px(context, 5)
							- (hisBottom - hisTop), paint);

			canvas.drawLine(
					hisleft + hisWidth / 2 + DisplayUtil.dip2px(context, 10),
					hisTop - DisplayUtil.dip2px(context, 5)
							- (hisBottom - hisTop), hisleft + hisWidth / 2
							+ DisplayUtil.dip2px(context, 10) + hisWidth,
					hisTop - DisplayUtil.dip2px(context, 5)
							- (hisBottom - hisTop), paint);

			// 画文字说明
			paint.setStrokeWidth(1);
			paint.setTextAlign(Align.LEFT);
			paint.setTextSize(DisplayUtil.sp2px(context, 14));
			canvas.drawText(hisTime + "小时", hisleft + (hisRigth - hisleft) / 2
					+ DisplayUtil.dip2px(context, 10),
					hisTop - DisplayUtil.dip2px(context, 10)
							- (hisBottom - hisTop), paint);
		} else if (isShow == true && isHis == false) {
			// 画右边小圆点
			float kuangStartX = 0;
			paint.setColor(yellow);
			paint.setStrokeWidth(DisplayUtil.dip2px(context, 2));
			if (kuangWidth < DisplayUtil.dip2px(context, 4)) {
				kuangStartX = kuangRight - kuangWidth / 2;
			} else {
				kuangStartX = kuangRight - DisplayUtil.dip2px(context, 2);
			}
			canvas.drawCircle(kuangStartX,
					kuangTop - DisplayUtil.dip2px(context, 5),
					DisplayUtil.dip2px(context, 3), paint);

			// 画右边说明线(开始时间、终止时间)
			paint.setStrokeWidth(2);
			canvas.drawLine(
					kuangStartX,
					kuangTop - DisplayUtil.dip2px(context, 5),
					kuangRight - DisplayUtil.dip2px(context, 5)
							+ DisplayUtil.dip2px(context, 10), kuangTop
							- DisplayUtil.dip2px(context, 5)
							- (hisBottom - hisTop), paint);
			canvas.drawLine(
					kuangRight - DisplayUtil.dip2px(context, 5)
							+ DisplayUtil.dip2px(context, 10),
					kuangTop - DisplayUtil.dip2px(context, 5)
							- (hisBottom - hisTop),
					kuangRight - DisplayUtil.dip2px(context, 5)
							+ DisplayUtil.dip2px(context, 10) + hisWidth,
					kuangTop - DisplayUtil.dip2px(context, 5)
							- (hisBottom - hisTop), paint);

			// 画右边文字说明(开始时间)
			paint.setStrokeWidth(1);
			paint.setTextAlign(Align.LEFT);
			paint.setTextSize(DisplayUtil.sp2px(context, 12));
			canvas.drawText(
					"起:" + timeViewInfoList.get(isDown).getStartTime(),
					kuangRight - DisplayUtil.dip2px(context, 5)
							+ DisplayUtil.dip2px(context, 10), kuangTop
							- DisplayUtil.dip2px(context, 10)
							- (hisBottom - hisTop), paint);
			// 画右边文字说明(停止时间)
			paint.setStrokeWidth(1);
			paint.setTextSize(DisplayUtil.sp2px(context, 12));
			canvas.drawText(
					"止:" + timeViewInfoList.get(isDown).getEndTime(),
					kuangRight - DisplayUtil.dip2px(context, 5)
							+ DisplayUtil.dip2px(context, 10), kuangTop
							+ DisplayUtil.dip2px(context, 7)
							- (hisBottom - hisTop), paint);
			// 画左边小圆点
			paint.setStrokeWidth(DisplayUtil.dip2px(context, 2));
			paint.setStyle(Paint.Style.FILL);
			if (kuangWidth < DisplayUtil.dip2px(context, 4)) {
				kuangStartX = kuangLeft + kuangWidth / 2;
			} else {
				kuangStartX = kuangLeft + DisplayUtil.dip2px(context, 2);
			}
			canvas.drawCircle(kuangStartX,
					kuangTop - DisplayUtil.dip2px(context, 5),
					DisplayUtil.dip2px(context, 3), paint);

			// 画左边说明线(里程)
			paint.setStrokeWidth(2);
			canvas.drawLine(
					kuangLeft + DisplayUtil.dip2px(context, 2),
					kuangTop - DisplayUtil.dip2px(context, 5),
					kuangLeft + DisplayUtil.dip2px(context, 5)
							- DisplayUtil.dip2px(context, 10), kuangTop
							- DisplayUtil.dip2px(context, 5)
							- (hisBottom - hisTop), paint);
			canvas.drawLine(
					kuangLeft + DisplayUtil.dip2px(context, 5)
							- DisplayUtil.dip2px(context, 10),
					kuangTop - DisplayUtil.dip2px(context, 5)
							- (hisBottom - hisTop),
					kuangLeft + DisplayUtil.dip2px(context, 5)
							- DisplayUtil.dip2px(context, 10) - hisWidth,
					kuangTop - DisplayUtil.dip2px(context, 5)
							- (hisBottom - hisTop), paint);
			// 画左边文字说明(里程)
			paint.setStrokeWidth(1);
			paint.setTextAlign(Align.RIGHT);
			paint.setTextSize(DisplayUtil.sp2px(context, 14));
			canvas.drawText(
					timeViewInfoList.get(isDown).getFeeTime(),
					kuangLeft + DisplayUtil.dip2px(context, 5)
							- DisplayUtil.dip2px(context, 10), kuangTop
							- DisplayUtil.dip2px(context, 10)
							- (hisBottom - hisTop), paint);
		}
	}

	/**
	 * 绘制当日数据矩形
	 * 
	 * @param canvas
	 * @param paint
	 */
	private void drawSamedayRect(Canvas canvas, Paint paint) {
		paint.setStyle(Paint.Style.FILL);
		for (int i = 0; i < timeViewInfoList.size(); i++) {
			if (type == 0) {
				paint.setColor(yellow);
			} else {
				if (isDown == i) {
					paint.setColor(yellow);
				} else {
					paint.setColor(yellow_background);
				}
			}
			RectF rectF = new RectF();
			rectF.left = timeViewInfoList.get(i).getLeft();
			rectF.top = hisTop;
			rectF.right = timeViewInfoList.get(i).getRigth();
			rectF.bottom = hisBottom - 2;
			canvas.drawRoundRect(rectF, 0, 0, paint);
			Log.i("result", "=矩形颜色="+i+"="+paint.getColor());
		}
	}

	/**
	 * 绘制历史记录
	 * 
	 * @param canvas
	 * @param paint
	 */
	private void drawHisRecord(Canvas canvas, Paint paint) {
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(gray);
		RectF rectF = new RectF();
		rectF.left = hisleft;
		rectF.top = hisTop;
		rectF.right = hisRigth;
		rectF.bottom = hisBottom;
		canvas.drawRoundRect(rectF, 5, 5, paint);
		// canvas.drawRect(hisleft, hisTop, hisRigth, hisBottom, paint);
	}

	/**
	 * 绘制底部文字
	 * 
	 * @param canvas
	 * @param paint
	 */
	private void drawBottomText(Canvas canvas, Paint paint) {
		paint.setStyle(Paint.Style.FILL);
		int px = DisplayUtil.dip2px(context, 15);
		paint.setColor(gray);
		paint.setStrokeWidth(1);
		paint.setTextAlign(Align.LEFT);
		paint.setTextSize(DisplayUtil.sp2px(context, 12));
		if (isHis) {
			// 历史记录
			canvas.drawText("历史驾驶", hisleft, XStarty + px, paint);
		}
		// 0时
		paint.setStrokeWidth(1);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(DisplayUtil.sp2px(context, 12));
		canvas.drawText("0时", hisRigth + DisplayUtil.dip2px(context, 15),
				XStarty + px, paint);
		// 6时
		canvas.drawText("6时", DisplayUtil.dip2px(context, 15) + hisRigth + 360
				* unitWidth, XStarty + px, paint);
		// 12时
		canvas.drawText("12时", DisplayUtil.dip2px(context, 15) + hisRigth + 12
				* 60 * unitWidth, XStarty + px, paint);
		// 18时
		canvas.drawText("18时", DisplayUtil.dip2px(context, 15) + hisRigth + 18
				* 60 * unitWidth, XStarty + px, paint);
		// 24时
		canvas.drawText("24时", DisplayUtil.dip2px(context, 15) + hisRigth + 24
				* 60 * unitWidth, XStarty + px, paint);

		// // 日期
		// paint.setStrokeWidth(1);
		// paint.setTextSize(DisplayUtil.sp2px(context, 10));
		// canvas.drawText(date, XStartx, XStarty + px + px, paint);
	}

	/**
	 * 绘制圆角长方形
	 * 
	 * @param canvas
	 * @param paint
	 */
	private void drawRoundRect(Canvas canvas, Paint paint) {
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(gray_background);
		RectF rectF = new RectF();
		rectF.left = 0;
		rectF.top = 0;
		rectF.right = backgroundRectWidth;
		rectF.bottom = hisBottom;
		canvas.drawRoundRect(rectF, 5, 5, paint);
	}

	/**
	 * 数据处理
	 * 
	 * @param list
	 */
	private void getDriverMileHeigth(List<CarReportTimeInfo> list) {
		timeViewInfoList.clear();
		j = 0;
		isDown = 0;
		float hisRigth = XStartx + 5 + hisWidth;
		float width = 0;
		String feeTime = "";
		int hour = 0;
		String strhour = "";
		int Minute = 0;
		String strminute = "";
		if (list != null && list.size() > 0) {
			CarReportTimeViewInfo timeViewInfo;
			for (int i = 0; i < list.size(); i++) {
				timeViewInfo = new CarReportTimeViewInfo();
				width = list.get(i).getEnd() / 60 * unitWidth
						- list.get(i).getStart() / 60 * unitWidth;
				timeViewInfo.setWidth(width);
				timeViewInfo.setLeft(list.get(i).getStart() / 60 * unitWidth
						+ hisRigth + DisplayUtil.dip2px(context, 20));
				timeViewInfo.setRigth(list.get(i).getEnd() / 60 * unitWidth
						+ hisRigth + DisplayUtil.dip2px(context, 20));
				hour = list.get(i).getStart() / 3600;
				if (hour > 9) {
					strhour = hour + "";
				} else {
					strhour = "0" + hour;
				}
				Minute = list.get(i).getStart() % 3600 / 60;
				if (Minute > 9) {
					strminute = "" + Minute;
				} else {
					strminute = "0" + Minute;
				}
				timeViewInfo.setStartTime(strhour + "时" + strminute + "分");
				// endTime = list.get(i).getEnd() / 3600 + "时"
				// + list.get(i).getEnd() % 3600 / 60 + "分";
				hour = list.get(i).getEnd() / 3600;
				if (hour > 9) {
					strhour = hour + "";
				} else {
					strhour = "0" + hour;
				}
				Minute = list.get(i).getEnd() % 3600 / 60;
				if (Minute > 9) {
					strminute = "" + Minute;
				} else {
					strminute = "0" + Minute;
				}
				timeViewInfo.setEndTime(strhour + "时" + strminute + "分");
				if (list.get(i).getFeetime() / 60 == 0) {
					feeTime = list.get(i).getFeetime() + "分";
					timeViewInfo.setFeeTime(feeTime);
				} else {
					// feeTime = list.get(i).getFeetime() / 60 + "小时"
					// + list.get(i).getFeetime() % 60 + "分";
					hour = list.get(i).getFeetime() / 60;
					Minute = list.get(i).getFeetime() % 60;
					if (Minute > 9) {
						strminute = Minute + "分";
					} else {
						strminute = "0" + Minute + "分";
					}
					timeViewInfo.setFeeTime(hour + "小时" + strminute);
				}
				timeViewInfoList.add(timeViewInfo);
			}
		}
		if (list != null && list.size() > 0) {
			if (type == 0) {
//				kuangBottom = hisBottom;
				kuangLeft = hisleft;
				kuangRight = hisRigth;
				kuangTop = hisTop;
			} else {
				j = type - 1;
				isDown = type - 1;
//				newKuangBottom = hisBottom;
				newKuangLeft = DisplayUtil.dip2px(context, 20)
						+ list.get(j).getStart() / 60 * unitWidth + hisRigth
						- 2;
				newKuangRight = DisplayUtil.dip2px(context, 20)
						+ list.get(j).getEnd() / 60 * unitWidth + hisRigth + 2;
//				newKuangTop = hisTop;
				kuangWidth = newKuangRight - newKuangLeft;
				if (newKuangLeft > kuangLeft) {
					isX = 1;
					mobile = newKuangLeft - kuangLeft;
//					mobileRight = newKuangRight - kuangRight;
				} else if (newKuangLeft < kuangLeft) {
					isX = 0;
//					mobileLeft = newKuangLeft - kuangLeft;
					mobile = kuangRight - newKuangRight;
				} else {
					isX = 2;
				}
				animator.start();
			}
		}
	}

	/**
	 * 获取scrollView移动位置
	 * 
	 * @return
	 */
	public int getFirstRect() {
		if (type == 0) {
			return 0;
		} else {
			if ((int) timeViewInfoList.get(type - 1).getLeft() > XStopx
					* (3f / 4)) {
				return (int) (XStopx * (3f / 4f));
			} else if ((int) timeViewInfoList.get(type - 1).getLeft() <= (int) (width * 0.75f * 0.5f)) {
				return 0;
			} else {
				return (int) timeViewInfoList.get(type - 1).getLeft()
						- (int) (width * 0.75f * 0.5f);
			}
		}
	}

	public void setInvalidate(Bundle bundle,
			List<CarReportTimeInfo> timeInfoList) {
//		this.timeInfoList = timeInfoList;
		// 数据初始化
		String time = bundle.getString("hisTime");
		int hour = (int)StringUtil.getDouble(time)/60;
		int minute = (int)StringUtil.getDouble(time)%60;
		if (minute >= 30) {
			hisTime = (hour + 1) + "";
		} else {
			hisTime = hour + "";
		}
		// date = bundle.getString("date");
		status = bundle.getString("status");
		type = bundle.getInt("type");
		// XStartx = DisplayUtil.dip2px(context, 2);
		hisWidth = DisplayUtil.dip2px(context, 50);
		hisBottom = heigth * screenScaleY;
		hisTop = hisBottom - (heigth * screenScaleY * hisScaleY);
		XStarty = hisBottom;
//		XStopy = hisTop;
		XStopx = width * 2;
		hisleft = XStartx + DisplayUtil.dip2px(context, 10);
		hisRigth = hisleft + hisWidth;
		if (status.equals("0")) {
			backgroundRectWidth = width - DisplayUtil.dip2px(context, 20);
			isNotData = true;
			// unitWidth = (width * 2 - hisWidth - 2 * XStartx) / 1440;
		} else if (type == 0) {
			backgroundRectWidth = width - DisplayUtil.dip2px(context, 20);
			isShow = false;
			isNotData = false;
			isHis = true;
			rectAxesWidth = width * 0.75f - DisplayUtil.dip2px(context, 25);
			unitWidth = (width * 0.75f - DisplayUtil.dip2px(context, 40)) * 0.7f / 1440;
			getDriverMileHeigth(timeInfoList);
		} else {
			backgroundRectWidth = width * 2;
			isShow = false;
			isNotData = false;
			isHis = false;
			rectAxesWidth = width * 0.97f + width;
			unitWidth = (width * 0.75f * 0.7f + width) / 1440;
			getDriverMileHeigth(timeInfoList);
		}
		invalidate();
	}
}
