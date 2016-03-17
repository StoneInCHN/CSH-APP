package com.cheweishi.android.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CarReportFreeVO;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.CustomDialog.Builder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 添加、编辑、查看费用详情界面
 * 
 * @author zhangq
 * 
 */
public class CarReportFreeAddActivity extends BaseActivity {
	/**
	 * 添加标志位
	 */
	private static final int INDEX_ADD = 1001;
	/**
	 * 编辑标志位
	 */
	private static final int INDEX_EDIT = 1002;
	/**
	 * 删除标志位
	 */
	private static final int INDEX_DELETE = 1003;
	@ViewInject(R.id.title)
	private TextView tvTitle;
	@ViewInject(R.id.right_action)
	private TextView tvRight;
	@ViewInject(R.id.left_action)
	private Button btnLeft;
	@ViewInject(R.id.ed_input_money)
	private EditText edInputMoney;
	@ViewInject(R.id.rlayout_time)
	private RelativeLayout rLayoutTime;
	@ViewInject(R.id.btn_delete)
	private Button btnDelete;
	@ViewInject(R.id.tv_time)
	private TextView tvTime;

	@ViewInject(R.id.llayout_select)
	private LinearLayout lLayoutSelect;
	@ViewInject(R.id.llayout_second)
	private LinearLayout lLayoutSecond;
	private LinearLayout[] lLayouts = new LinearLayout[6];
	private ImageView[] imageViews = new ImageView[6];
	private int[] lRids = { R.id.llayout_1, R.id.llayout_2, R.id.llayout_3,
			R.id.llayout_4, R.id.llayout_5, R.id.llayout_6 };
	private int[] iRids = { R.id.img_1, R.id.img_2, R.id.img_3, R.id.img_4,
			R.id.img_5, R.id.img_6 };

	/**
	 * 输入框小数位数
	 */
	private static final int DECIMAL_DIGITS = 2;
	private String sTime;// yyyy-MM-dd HH:mm
	private String time;// yyyy-MM-dd
	private String money;
	private String index;
	private int itemId;
	private CarReportFreeVO vo;
	private String[] types = { "park", "clean", "road", "maintain",
			"insurance", "fine" };

	private String id;//
	private int rid;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat decf = new DecimalFormat("00");
	private Calendar c = Calendar.getInstance();
	private int hours;
	private int mins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_free_add);
		ViewUtils.inject(this);
		httpBiz = new HttpBiz(this);
		initData();
	}

	private void initData() {
		for (int i = 0; i < 6; i++) {
			lLayouts[i] = (LinearLayout) findViewById(lRids[i]);
			imageViews[i] = (ImageView) findViewById(iRids[i]);
			lLayouts[i].setOnClickListener(onClickListener);
		}
		btnLeft.setText(this.getString(R.string.back));
		rLayoutTime.setOnClickListener(onClickListener);
		tvRight.setOnClickListener(onClickListener);
		btnLeft.setOnClickListener(onClickListener);
		btnDelete.setOnClickListener(onClickListener);
		// edInputMoney.setFilters(new InputFilter[] { lengthFilter });

		// edInputMoney.addTextChangedListener(watcher);

		Intent intent = getIntent();
		time = intent.getStringExtra("time");
		id = intent.getStringExtra("id");
		index = intent.getStringExtra("index");
		itemId = intent.getIntExtra("itemId", -1);
		rid = intent.getIntExtra("rid", 0);
		money = intent.getStringExtra("money");
		vo = (CarReportFreeVO) intent.getSerializableExtra("vo");

		hours = c.get(Calendar.HOUR_OF_DAY);
		mins = c.get(Calendar.MINUTE);

		if ("add".equalsIgnoreCase(index)) {
			addOpreate();
		} else if ("oil".equalsIgnoreCase(index)) {
			oilOpreate();
		} else if ("edit".equalsIgnoreCase(index)) {
			editOpreate();
		}

	}

	TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
			// String regEx = "[1-9][0-9]{0,5}+?\\.[0-9]{0,2}";
			// Pattern pattern = Pattern.compile(regEx);
			// Matcher matcher = pattern.matcher(cs);
		}

		@Override
		public void beforeTextChanged(CharSequence cs, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
		}
	};

	/**
	 * 编辑界面
	 */
	private void editOpreate() {
		if (vo != null) {
			int len = vo.getMoney().length();
			edInputMoney.setText(vo.getMoney().subSequence(1, len));
			edInputMoney.setSelection(len - 1);
			tvTime.setText(vo.getTime());
			clickPosition = vo.getType();
			imageViews[clickPosition - 1].setVisibility(View.VISIBLE);
			prePosition = clickPosition;
		}
		tvTitle.setText(this.getString(R.string.report_edit_free));
		tvRight.setText(this.getString(R.string.save));
	}

	/**
	 * 油耗界面
	 */
	private void oilOpreate() {
		tvTitle.setText(this.getString(R.string.report_oil_free));
		edInputMoney.setFocusable(false);
		edInputMoney.setText(money);

		for (LinearLayout ll : lLayouts) {
			ll.setVisibility(View.INVISIBLE);
		}
		lLayoutSelect.setVisibility(View.INVISIBLE);
		lLayoutSecond.setVisibility(View.INVISIBLE);
		btnDelete.setVisibility(View.GONE);
	}

	/**
	 * 添加界面
	 */
	private void addOpreate() {
		tvTitle.setText(this.getString(R.string.report_add_free));
		tvRight.setText(this.getString(R.string.report_add));
		tvTime.setText(decf.format(hours) + ":" + decf.format(mins));

		btnDelete.setVisibility(View.GONE);
	}

	int prePosition = 0;
	int clickPosition = 0;

	/**
	 * 点击操作
	 */
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.right_action:
				saveData();
				break;
			case R.id.left_action:
				onBackPressed();
				break;
			case R.id.rlayout_time:
				selecTime();
				break;
			case R.id.btn_delete:
				showCustomDialog();
				break;
			case R.id.llayout_1:
				clickPosition = 1;
				clickItem();
				prePosition = 1;
				break;
			case R.id.llayout_2:
				clickPosition = 2;
				clickItem();
				prePosition = 2;
				break;
			case R.id.llayout_3:
				clickPosition = 3;
				clickItem();
				prePosition = 3;
				break;
			case R.id.llayout_4:
				clickPosition = 4;
				clickItem();
				prePosition = 4;
				break;
			case R.id.llayout_5:
				clickPosition = 5;
				clickItem();
				prePosition = 5;
				break;
			case R.id.llayout_6:
				clickPosition = 6;
				clickItem();
				prePosition = 6;
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 删除操作
	 * 
	 * @param clickPosition
	 */
	private void deleteItem() {
		// if (clickPosition == 0) {
		// showToast("请选择费用类型");
		// return;
		// }
		LoginMessage message = LoginMessageUtils
				.getLoginMessage(CarReportFreeAddActivity.this);

		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", message.getUid());
		params.addBodyParameter("key", message.getKey());
		params.addBodyParameter("id", String.valueOf(itemId));
		httpBiz.getPostData(INDEX_DELETE, API.REPORT_FREE_DELETE_URL, params,
				this, httpBiz.POSTNUM, this);
	}

	/**
	 * 解析删除数据
	 * 
	 * @param data
	 */
	private void parseJsonDelete(String data) {
		try {
			JSONObject json = new JSONObject(data);
			if ("SUCCESS".equalsIgnoreCase(json.getString("operationState"))) {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
				onBackPressed();
			} else if ("RELOGIN".equalsIgnoreCase(json
					.getString("operationState"))) {
				DialogTool.getInstance(CarReportFreeAddActivity.this)
						.showConflictDialog();
			} else {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
			}
		} catch (JSONException e) {
		}
	}

	private void selecTime() {
		new TimePickerDialog(this, new OnTimeSetListener() {
			private int index = -1;

			@Override
			public void onTimeSet(TimePicker tp, int hours, int mins) {
				if (index == -1) {
					if (!isAfterNow(hours, mins)) {
						CarReportFreeAddActivity.this.hours = hours;
						CarReportFreeAddActivity.this.mins = mins;
						tvTime.setText(decf.format(hours) + ":"
								+ decf.format(mins));
					} else {
						showToast(CarReportFreeAddActivity.this
								.getString(R.string.report_choose_after_now));
					}
					index *= -1;
				}
			}
		}, hours, mins, true).show();
	}

	/**
	 * 可提取
	 * 
	 * @param hour
	 * @param min
	 * @return
	 */
	private boolean isAfterNow(int hour, int min) {
		Date selectDate = StringUtil.getDate(time, "yyyy-MM-dd");
		selectDate.setHours(hour);
		selectDate.setMinutes(min);
		Calendar c = Calendar.getInstance();
		Date nowTime = c.getTime();
		if (selectDate.after(nowTime)) {
			return true;
		}
		return false;
	}

	private void clickItem() {
		if (prePosition != 0) {
			imageViews[prePosition - 1].setVisibility(View.INVISIBLE);
		}
		imageViews[clickPosition - 1].setVisibility(View.VISIBLE);
	}

	/**
	 * 保存数据
	 */
	private void saveData() {
		int value = getValue();
		if (value == 0) {
			showToast(this.getString(R.string.report_input));
			return;
		}
		if (clickPosition == 0) {
			showToast(this.getString(R.string.report_choose_type));
			return;
		}
		// 设置时间 HH:mm
		sTime = decf.format(hours) + ":" + decf.format(mins);

		// edit\add
		if ("edit".equals(index)) {
			editData(value);
			return;
		}
		RequestParams params = new RequestParams();

		LoginMessage message = LoginMessageUtils
				.getLoginMessage(CarReportFreeAddActivity.this);

		params.addBodyParameter("uid", message.getUid());
		params.addBodyParameter("key", message.getKey());
		params.addBodyParameter("id", id);
		params.addBodyParameter("cid", message.getCar().getCid());
		params.addBodyParameter("time", time);
		params.addBodyParameter("type", types[clickPosition - 1]);
		params.addBodyParameter("value", String.valueOf(value));
		params.addBodyParameter("date", sTime);
		params.addBodyParameter("rid", rid + "");
		params.addBodyParameter(
				"latlng",
				MyMapUtils.getLatitude(CarReportFreeAddActivity.this)
						+ ","
						+ MyMapUtils
								.getLongitude(CarReportFreeAddActivity.this));
		params.addBodyParameter("addr",
				MyMapUtils.getAddress(CarReportFreeAddActivity.this));
		httpBiz.getPostData(INDEX_ADD, API.REPORT_FREE_ADD_URL, params, this,
				httpBiz.POSTNUM, this);
	}

	private void parseJsonAdd(String data) {
		try {
			JSONObject json = new JSONObject(data);
			if ("SUCCESS".equalsIgnoreCase(json.getString("operationState"))) {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
				onBackPressed();
			} else if ("RELOGIN".equalsIgnoreCase(json
					.getString("operationState"))) {
				DialogTool.getInstance(CarReportFreeAddActivity.this)
						.showConflictDialog();
			} else {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * 编辑数据
	 * 
	 * @param value
	 */
	private void editData(double value) {
		LoginMessage message = LoginMessageUtils
				.getLoginMessage(CarReportFreeAddActivity.this);

		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", message.getUid());
		params.addBodyParameter("key", message.getKey());
		params.addBodyParameter("id", itemId + "");
		params.addBodyParameter("type", types[clickPosition - 1]);
		params.addBodyParameter("value", String.valueOf(value));
		params.addBodyParameter("date", sTime);
		params.addBodyParameter(
				"latlng",
				MyMapUtils.getLatitude(CarReportFreeAddActivity.this)
						+ ","
						+ MyMapUtils
								.getLongitude(CarReportFreeAddActivity.this));
		params.addBodyParameter("addr",
				MyMapUtils.getAddress(CarReportFreeAddActivity.this));

		httpBiz.getPostData(INDEX_EDIT, API.REPORT_FREE_UPDATE_URL, params,
				this, httpBiz.POSTNUM, this);
	}

	private void parseJsonEdit(String data) {
		try {
			JSONObject json = new JSONObject(data);
			if ("SUCCESS".equalsIgnoreCase(json.getString("operationState"))) {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
				onBackPressed();
			} else if ("RELOGIN".equalsIgnoreCase(json
					.getString("operationState"))) {
				DialogTool.getInstance(CarReportFreeAddActivity.this)
						.showConflictDialog();
			} else {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
			}
		} catch (JSONException e) {
		}

	}

	@Override
	public void receive(int type, String data) {
		switch (type) {
		case INDEX_ADD:
			parseJsonAdd(data);
			break;
		case INDEX_EDIT:
			parseJsonEdit(data);
			break;
		case INDEX_DELETE:
			parseJsonDelete(data);
			break;
		}
	}

	private int getValue() {
		String s = edInputMoney.getText().toString();
		int i = (int) StringUtil.getDouble(s);
		return i;
	}

	InputFilter lengthFilter = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			if ("".equals(source.toString())) {
				return null;
			}
			String dValue = dest.toString();
			String[] splitArray = dValue.split("//.");
			if (splitArray.length > 1) {
				String dotValue = splitArray[1];
				int diff = dotValue.length() + 1 - DECIMAL_DIGITS;
				if (diff > 0) {
					return source.subSequence(start, end - diff);
				}
			}
			return null;
		}
	};

	@Override
	public void onBackPressed() {
		// if ("edit".equals(index)) {
		// showBackDialog();
		// return;
		// }

		if ("add".equals(index) || "edit".equals(index)) {
			/**
			 * 添加、编辑界面返回需要刷新上个界面
			 */
			setResult(RESULT_OK);
		}
		CarReportFreeAddActivity.this.finish();
	}

	private void showBackDialog() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		lLayouts = null;
		imageViews = null;
		types = null;
	}

	private void showCustomDialog() {
		Builder builder = new CustomDialog.Builder(
				CarReportFreeAddActivity.this);
		builder.setMessage(this.getString(R.string.report_insure_delete));
		builder.setTitle(this.getString(R.string.remind));
		builder.setPositiveButton(this.getString(R.string.sure),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						deleteItem();
					}
				});
		builder.setNegativeButton(this.getString(R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

}
