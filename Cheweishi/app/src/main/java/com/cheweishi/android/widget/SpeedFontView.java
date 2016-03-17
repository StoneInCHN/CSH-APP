package com.cheweishi.android.widget;

import java.util.ArrayList;
import java.util.List;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.Speed;
import com.cheweishi.android.tools.TextViewTools;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 
 * @author 阳光小强 http://blog.csdn.net/dawanganban
 * 
 */
public class SpeedFontView extends View {
	private int nextIndex;
	private int XPoint = 80;
	private int YPoint = 100;
	private float XScale = 80;
	private float YScale = (float) 40 / (float) 30;
	private int XLength = 960;
	private int YLength = 200;
	private Paint paint = new Paint();
	private int index;
	private List<Speed> listSpeed = new ArrayList<Speed>();
	private boolean flag = false;
	private int speedIndex;
	private static int TEXT_SIZE = 34;
	private float RATIO;
	private boolean speedFlag = false;
	private float firstX;
	private float firstY;
	private float secondX;
	private float secondY;
	private float tempX;
	private float tempY;

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(
				((int) ((getScreenWidth() - 60 * RATIO) * 10 / 7)),
				getScreenHeight() / 4 + getFontHeight(TEXT_SIZE) + 6);
	}

	public int getFontWidth(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		return getStringWidth("24时", paint);
	}

	public int getFontWidth(String str) {
		Paint paint = new Paint();
		paint.setTextSize(TEXT_SIZE);
		return getStringWidth(str, paint);
	}

	public void setNewData(int index) {
		this.index = index;
		nextIndex = 0;
		firstX = secondX;
		firstY = secondY;
		flag = true;
		XLength = ((int) ((getScreenWidth() - 60 * RATIO) * 10 / 7))
				- getFontWidth(TEXT_SIZE) - 3;
		XScale = (float) XLength / (3600 * 24);
		setMeasuredDimension(XLength, getScreenHeight() / 4);
		if (listSpeed.size() > (index)
				&& listSpeed.get(index).getListSubSpeed().size() > 1) {
			tempX = (XPoint + (int) (listSpeed.get(index).getStart()) + ((int) (listSpeed
					.get(index).getEnd()) - (int) (listSpeed.get(index)
					.getStart())) / 2);
			tempY = listSpeed.get(index).getAvgCurrent();
		}
		speedIndex = index;
		this.invalidate();
		requestLayout();
	}


	public SpeedFontView(Context context, List<Speed> listSpeed,
			boolean speedFlag) {
		super(context);
		int screenWidth = getScreenWidth();
		int screenHeight = getScreenHeight();
		float ratioWidth = (float) screenWidth / 720;
		float ratioHeight = (float) screenHeight / 1280;

		RATIO = Math.min(ratioWidth, ratioHeight);
		TEXT_SIZE = Math.round(28 * RATIO);
		this.speedFlag = speedFlag;
		this.listSpeed = listSpeed;
		if (listSpeed.size() > 0
				&& listSpeed.get(index).getListSubSpeed().size() > 1) {

			tempX = (XPoint + (int) (listSpeed.get(index).getStart()) + ((int) (listSpeed
					.get(index).getEnd()) - (int) (listSpeed.get(index)
					.getStart())) / 2);// (int)
			tempY = YPoint - listSpeed.get(index).getAvgCurrent();
		}
		YPoint = getScreenHeight() / 4 - 80;
		XPoint = 0;
		YLength = getScreenHeight() / 4 - 100;
		if (listSpeed.size() > 0
				&& listSpeed.get(index).getListSubSpeed().size() > 1) {
			tempY = YPoint - (int) listSpeed.get(index).getAvgCurrent();
		}
		XLength = getScreenWidth() * 3 / 4 - XPoint * 2;
		setMeasuredDimension(XLength, getScreenHeight() / 4);
		YPoint = getScreenHeight() / 4;
		YLength = getScreenHeight() / 4;
		YScale = (float) YLength / (float) (5 * 30);
		XLength = ((int) ((getScreenWidth() - 60 * RATIO) * 5 / 7))
				- getFontWidth(TEXT_SIZE);
		XLength += 1;
		XScale = (float) XLength / (3600 * 24);
	}

	Speed sp1;

	private int getScreenHeight() {

		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		((Activity) getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int screenHeigh = dm.heightPixels;
		return screenHeigh;
	}

	private int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		((Activity) getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}

	int drawIndex = 1;

	public int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (drawIndex == 1) {
			int screenWidth = getScreenWidth();
			int screenHeight = getScreenHeight();
			float ratioWidth = (float) screenWidth / 720;
			float ratioHeight = (float) screenHeight / 1280;
			RATIO = Math.min(ratioWidth, ratioHeight);
			TEXT_SIZE = Math.round(28 * RATIO);
			if (canvas != null) {
				if (flag == true && nextIndex < 1) {
					if (listSpeed.size() >= speedIndex && nextIndex < 1) {
						Speed sp = listSpeed.get(speedIndex);
						this.sp1 = listSpeed.get(speedIndex);
						if (nextIndex < 1) {
							firstX = (tempX - firstX) / 1 + firstX;
							firstY = (sp.getAvgCurrent());
						} else {
							secondX = tempX;
							secondY = tempY;
							firstX = tempX;
							firstY = (sp.getAvgCurrent());
						}
						Path path = new Path();
						paint.setColor(getContext().getResources().getColor(
								R.color.orange));
						paint.setStrokeWidth(2);
						paint.setTextSize(TEXT_SIZE);
						paint.setStyle(Paint.Style.STROKE);
						if (sp.getAvgCurrent() >= 75) {
							if ((sp.getStart() + (sp.getEnd() - sp.getStart()) / 2) < 72000) {
								path.moveTo(XPoint + firstX * XScale, YPoint
										- firstY * ((float) YScale) - 20
										* RATIO);
								path.lineTo(XPoint + firstX * XScale + 50
										* RATIO, YPoint - firstY
										* ((float) YScale) + 30 * RATIO);

								path.lineTo(XPoint + firstX * XScale + 250
										* RATIO, YPoint - firstY
										* ((float) YScale) + 30 * RATIO);
								canvas.drawPath(path, paint);
								paint.setStyle(Paint.Style.FILL);
								if (speedFlag == false) {
									if ((sp.getStart()
											+ (sp.getEnd() - sp.getStart()) > 72000)) {

									} else {
										canvas.drawText(
												"最高时速" + sp.getMaxCurrent(),
												XPoint + firstX * XScale + 50
														* RATIO, YPoint
														- firstY
														* ((float) YScale) + 30
														* RATIO - 10 * RATIO,
												paint);
										canvas.drawText(
												"平均时速" + sp.getAvgCurrent(),
												XPoint + firstX * XScale + 50
														* RATIO, YPoint
														- firstY
														* ((float) YScale) + 30
														* RATIO + 30 * RATIO,
												paint);
									}
								} else {
									canvas.drawText(
											"最高油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getMaxCurrent() / 6f),
											XPoint + firstX * XScale + 50
													* RATIO, YPoint - firstY
													* ((float) YScale) + 30
													* RATIO - 10 * RATIO, paint);
									canvas.drawText(
											"平均油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getAvgCurrent() / 6f),
											XPoint + firstX * XScale + 50
													* RATIO, YPoint - firstY
													* ((float) YScale) + 30
													* RATIO + 30 * RATIO, paint);
								}

								RectF rf = new RectF(XPoint + firstX * XScale
										- 5 * RATIO, YPoint - firstY
										* ((float) YScale) - 10 * RATIO - 20
										* RATIO, XPoint + firstX * XScale + 10
										* RATIO, YPoint - firstY
										* ((float) YScale) + 5 * RATIO - 20
										* RATIO);
								canvas.drawOval(rf, paint);
							} else {
								path.moveTo(XPoint + firstX * XScale, YPoint
										- firstY * ((float) YScale) - 20
										* RATIO);
								path.lineTo(XPoint + firstX * XScale - 50
										* RATIO, YPoint - firstY
										* ((float) YScale) + 30 * RATIO);

								path.lineTo(XPoint + firstX * XScale - 250
										* RATIO, YPoint - firstY
										* ((float) YScale) + 30 * RATIO);
								canvas.drawPath(path, paint);
								paint.setStyle(Paint.Style.FILL);
								if (speedFlag == false) {
									canvas.drawText(
											"最高时速" + sp.getMaxCurrent(),
											XPoint
													+ firstX
													* XScale
													- 50
													* RATIO
													- getFontWidth("最高油耗"
															+ sp.getMaxCurrent()),
											YPoint - firstY * ((float) YScale)
													+ 30 * RATIO - 10 * RATIO,
											paint);
									canvas.drawText(
											"平均时速" + sp.getAvgCurrent(),
											XPoint
													+ firstX
													* XScale
													- 50
													* RATIO
													- getFontWidth("平均油耗"
															+ sp.getAvgCurrent()),
											YPoint - firstY * ((float) YScale)
													+ 30 * RATIO + 30 * RATIO,
											paint);

								} else {
									canvas.drawText(
											"最高油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getMaxCurrent() / 6f),
											XPoint
													+ firstX
													* XScale
													- 50
													* RATIO
													- getFontWidth("最高油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getMaxCurrent() / 6f)), YPoint
													- firstY * ((float) YScale)
													+ 30 * RATIO - 10 * RATIO,
											paint);
									canvas.drawText(
											"平均油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getAvgCurrent() / 6f),
											XPoint
													+ firstX
													* XScale
													- 50
													* RATIO
													- getFontWidth("平均油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getAvgCurrent() / 6f)), YPoint
													- firstY * ((float) YScale)
													+ 30 * RATIO + 30 * RATIO,
											paint);

								}

								RectF rf = new RectF(XPoint + firstX * XScale
										- 5 * RATIO, YPoint - firstY
										* ((float) YScale) - 10 * RATIO - 20
										* RATIO, XPoint + firstX * XScale + 10
										* RATIO, YPoint - firstY
										* ((float) YScale) + 5 * RATIO - 20
										* RATIO);
								canvas.drawOval(rf, paint);
							}
						} else {
							if ((sp.getStart() + (sp.getEnd() - sp.getStart()) / 2) < 72000) {
								path.moveTo(XPoint + firstX * XScale, YPoint
										- firstY * ((float) YScale) - 20
										* RATIO);
								path.lineTo(XPoint + firstX * XScale + 50
										* RATIO, YPoint - firstY
										* ((float) YScale) - 70 * RATIO);

								path.lineTo(XPoint + firstX * XScale + 250
										* RATIO, YPoint - firstY
										* ((float) YScale) - 70 * RATIO);
								canvas.drawPath(path, paint);

								paint.setStyle(Paint.Style.FILL);
								if (speedFlag == false) {
									canvas.drawText(
											"最高时速" + sp.getMaxCurrent(), XPoint
													+ firstX * XScale + 50
													* RATIO, YPoint - firstY
													* ((float) YScale) - 70
													* RATIO - 10 * RATIO, paint);
									canvas.drawText(
											"平均时速" + sp.getAvgCurrent(), XPoint
													+ firstX * XScale + 50
													* RATIO, YPoint - firstY
													* ((float) YScale) - 70
													* RATIO + 30 * RATIO, paint);

								} else {
									canvas.drawText(
											"最高油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getMaxCurrent() / 6f),
											XPoint + firstX * XScale + 50
													* RATIO, YPoint - firstY
													* ((float) YScale) - 70
													* RATIO - 10 * RATIO, paint);
									canvas.drawText(
											"平均油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getAvgCurrent() / 6f),
											XPoint + firstX * XScale + 50
													* RATIO, YPoint - firstY
													* ((float) YScale) - 70
													* RATIO + 30 * RATIO, paint);

								}

								RectF rf = new RectF(XPoint + firstX * XScale
										- 5 * RATIO, YPoint - firstY
										* ((float) YScale) - 10 * RATIO - 20
										* RATIO, XPoint + firstX * XScale + 10
										* RATIO, YPoint - firstY
										* ((float) YScale) + 5 * RATIO - 20
										* RATIO);
								canvas.drawOval(rf, paint);
							} else {
								path.moveTo(XPoint + firstX * XScale, YPoint
										- firstY * ((float) YScale) - 20
										* RATIO);
								path.lineTo(XPoint + firstX * XScale - 50
										* RATIO, YPoint - firstY
										* ((float) YScale) - 70 * RATIO);

								path.lineTo(XPoint + firstX * XScale - 250
										* RATIO, YPoint - firstY
										* ((float) YScale) - 70 * RATIO);
								canvas.drawPath(path, paint);
								paint.setStyle(Paint.Style.FILL);
								if (speedFlag == false) {
									canvas.drawText(
											"最高时速" + sp.getMaxCurrent(),
											XPoint
													+ firstX
													* XScale
													- 50
													* RATIO
													- getFontWidth("最高时速"
															+ sp.getMaxCurrent()),
											YPoint - firstY * ((float) YScale)
													- 70 * RATIO - 10 * RATIO,
											paint);
									canvas.drawText(
											"平均时速" + sp.getAvgCurrent(),
											XPoint
													+ firstX
													* XScale
													- 50
													* RATIO
													- getFontWidth("平均时速"
															+ sp.getAvgCurrent()),
											YPoint - firstY * ((float) YScale)
													- 70 * RATIO + 30 * RATIO,
											paint);
									// }
								} else {
									canvas.drawText(
											"最高油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getMaxCurrent() / 6f),
											XPoint
													+ firstX
													* XScale
													- 50
													* RATIO
													- getFontWidth("最高油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getMaxCurrent() / 6f)), YPoint
													- firstY * ((float) YScale)
													- 70 * RATIO - 10 * RATIO,
											paint);
									canvas.drawText(
											"平均油耗"
													+ TextViewTools
															.toTwoEndFromFloat(sp
																	.getAvgCurrent() / 6f),
											XPoint
													+ firstX
													* XScale
													- 50
													* RATIO
													- getFontWidth("平均油耗"
															+ sp.getAvgCurrent()
															/ 6f), YPoint
													- firstY * ((float) YScale)
													- 70 * RATIO + 30 * RATIO,
											paint);
								}

								RectF rf = new RectF(XPoint + firstX * XScale
										- 5 * RATIO, YPoint - firstY
										* ((float) YScale) - 10 * RATIO - 20
										* RATIO, XPoint + firstX * XScale + 10
										* RATIO, YPoint - firstY
										* ((float) YScale) + 5 * RATIO - 20
										* RATIO);
								canvas.drawOval(rf, paint);
							}
						}
						nextIndex++;
					}

				}
			}
		}
	}

	private int getStringWidth(String str, Paint paint) {
		return (int) paint.measureText(str);
	}
}