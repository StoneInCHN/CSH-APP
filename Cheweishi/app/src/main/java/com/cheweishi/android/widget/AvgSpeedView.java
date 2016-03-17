package com.cheweishi.android.widget;

import java.util.ArrayList;
import java.util.List;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.Speed;
import com.cheweishi.android.fragement.CarReportOilFrament;
import com.cheweishi.android.fragement.CarReportSpeedFragment;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author 阳光小强 http://blog.csdn.net/dawanganban
 * 
 */
public class AvgSpeedView extends View {
	private int XPoint = 80;
	private int YPoint = 100;
	private float XScale = 80;
	private float YScale = (float) 40 / (float) 30;
	private int XLength = 960;
	private int YLength = 200;
	private Paint paint = new Paint();
	private List<Speed> listSpeed = new ArrayList<Speed>();
	// private String[] YLabel = new String[YLength / YScale];
	private boolean flag = false;
	private static int TEXT_SIZE = 34;
	private float RATIO;

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// setMeasuredDimension(
		// (getScreenWidth() * 5 / 4 - getFontWidth(TEXT_SIZE)),
		// getScreenHeight() / 4 + getFontHeight(TEXT_SIZE) + 6);
		setMeasuredDimension(
				((int) ((getScreenWidth() - 60 * RATIO) * 10 / 7)),
				getScreenHeight() / 4 + getFontHeight(TEXT_SIZE) + 6);
	}

	public void setListSpeed(List<Speed> listSpeed) {
		this.listSpeed = listSpeed;
		// Toast.makeText(getContext(), "good", Toast.LENGTH_LONG).show();
		AvgSpeedView.this.invalidate();
	}

	private int newIndex;

	public void setNewData(int newIndex) {
		this.newIndex = newIndex;
		// Toast.makeText(getContext(), getFontWidth(TEXT_SIZE)+"",
		// Toast.LENGTH_LONG).show();
		this.flag = true;
		XLength = ((int) ((getScreenWidth() - 60 * RATIO) * 10 / 7))
				- getFontWidth(TEXT_SIZE) - 3;
		XScale = (float) XLength / (3600 * 24);

		XLength = (int) (XLength + XLength / 4);
		if (speedFlag == false) {
			CarReportSpeedFragment.XLength = XLength;
			CarReportSpeedFragment.XScale = XScale;
		} else {
			CarReportOilFrament.XLength = XLength;
			CarReportOilFrament.XScale = XScale;
		}
		setMeasuredDimension(XLength, getScreenHeight() / 4);
		this.invalidate();
		requestLayout();
	}

	private boolean speedFlag = false;

	public AvgSpeedView(Context context, List<Speed> listSpeed,
			boolean speedFlag, ListView speedListView, TextView left_zhanwei,
			TextView tv_imgRemind) {
		super(context);
		int screenWidth = getScreenWidth();
		int screenHeight = getScreenHeight();
		float ratioWidth = (float) screenWidth / 720;
		float ratioHeight = (float) screenHeight / 1280;
		RATIO = Math.min(ratioWidth, ratioHeight);
		TEXT_SIZE = Math.round(28 * RATIO);
		this.listSpeed = listSpeed;
		this.speedFlag = speedFlag;
		XPoint = 0;
		YPoint = getScreenHeight() / 4;
		YLength = getScreenHeight() / 4;
		YScale = (float) YLength / (float) (5 * 30);
		XLength = ((int) ((getScreenWidth() - 60 * RATIO) * 5 / 7))
				- getFontWidth(TEXT_SIZE);
		if (speedFlag == false) {
			CarReportSpeedFragment.XLength = XLength;
			CarReportSpeedFragment.XScale = XScale;
			CarReportSpeedFragment.RATIO = RATIO;
		} else {
			CarReportOilFrament.XLength = XLength;
			CarReportOilFrament.XScale = XScale;
			CarReportOilFrament.RATIO = RATIO;
		}
		XLength += 1;
		XScale = (float) XLength / (3600 * 24);
		setMeasuredDimension(XLength, getScreenHeight() / 4);

	}

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

	public int getFontWidth(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		return getStringWidth("24时", paint);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (canvas != null) {
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setColor(getContext().getResources().getColor(
					R.color.gray_backgroud));
			RectF rg2 = new RectF(XPoint, YPoint - YLength,
					getScreenWidth() * 2, YPoint);
			canvas.drawRect(rg2, paint);
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.STROKE);
			paint.setAntiAlias(true); //
			paint.setColor(Color.BLACK);
			paint.setTextSize(TEXT_SIZE);
			paint.setStrokeWidth(1);

			Paint paint2 = new Paint();
			paint2.setColor(Color.BLUE);
			paint2.setStyle(Paint.Style.FILL);
			if (listSpeed.size() > 0) {
				for (int i = 0; i < listSpeed.size(); i++) {
					if (listSpeed.size() > 0
							&& listSpeed.get(i).getListSubSpeed().size() > 1) {
						if (i == newIndex && flag == true) {
							paint.setColor(getContext().getResources()
									.getColor(R.color.main_blue));
						} else {
							paint.setColor(getContext().getResources()
									.getColor(R.color.light_blue));
						}
						//Toast.makeText(getContext(), XLength+ "_" + , duration)
						if (Math.abs(XLength
								- ((int) ((getScreenWidth() - 60 * RATIO) * 5 / 7))
								+ getFontWidth(TEXT_SIZE)) < 30) {
							paint.setColor(getContext().getResources()
									.getColor(R.color.main_blue));
						}
						System.out
								.println(listSpeed.get(i).getStart() + "_"
										+ listSpeed.get(i).getEnd() + "_"
										+ XPoint
										+ (listSpeed.get(i).getStart())
										* XScale + "_" + XPoint
										+ (listSpeed.get(i).getEnd()) * XScale);
						paint.setStyle(Paint.Style.FILL_AND_STROKE);
						RectF rg = new RectF((listSpeed.get(i).getStart())
								* XScale, YPoint
								- (listSpeed.get(i).getAvgCurrent()) * YScale,
								(int) (listSpeed.get(i).getEnd()) * XScale,
								YPoint);
						canvas.drawRect(rg, paint);
					}
				}
			}

			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setColor(getContext().getResources().getColor(R.color.white));
			RectF rg = new RectF(XPoint, YPoint, getScreenWidth() * 2,
					getScreenHeight() / 2);
			canvas.drawRect(rg, paint);
			paint.setStyle(Paint.Style.FILL);
			if (listSpeed.size() > 0) {
				for (int i = 0; i < 5; i++) {
					paint.setColor(getContext().getResources().getColor(
							R.color.gray_normal));
					if (i == 4) {
						canvas.drawText("" + i * 6 + "时", XPoint + i * 6 * 3600
								* XScale - 3, YPoint + getFontHeight(TEXT_SIZE)
								+ 3, paint);// 文字
					} else {
						canvas.drawText("" + i * 6 + "时", XPoint + i * 6 * 3600
								* XScale,
								YPoint + getFontHeight(TEXT_SIZE) + 3, paint);// 文字
					}
					// }
				}
			}
		}
	}

	private int getStringWidth(String str, Paint paint) {
		return (int) paint.measureText(str);
	}
}