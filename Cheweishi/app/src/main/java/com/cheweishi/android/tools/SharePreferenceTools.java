package com.cheweishi.android.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.cheweishi.android.entity.PhoneRecord;

public class SharePreferenceTools {
	public static boolean phoneFlag = false;
	/**
	 * SharedPreference名称
	 */
	private static final String PREFERENCE_FILE_NAME = "dock_preferences";

	/**
	 * 保存通话记录
	 * 
	 * @param length
	 * @param pr
	 * @param context
	 */
	public static void saveProduct(int length, PhoneRecord pr, Context context) {
		SharedPreferences preferences = context.getSharedPreferences("base64",
				Context.MODE_PRIVATE);
		int saveIndex = length;
		boolean falg = false;

		// 创建字节输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// 创建对象输出流，并封装字节流
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			// 将对象写入字节流
			oos.writeObject(pr);

			// 将字节流编码成base64的字符窜
			String productBase64 = new String(Base64.encodeBase64(baos
					.toByteArray()));

			Editor editor = preferences.edit();
			if (falg == true) {

				editor.putString("product" + saveIndex, productBase64);

			} else {
				editor.putString("product" + saveIndex, productBase64);
				editor.putInt("length1", (length + 1));
			}
			editor.commit();
			String temp = preferences.getString("product" + saveIndex, "");
			if (falg == true && (!(saveIndex == (length - 1)))) {
				Editor editor1 = preferences.edit();
				for (int i = saveIndex; i < length - 1; i++) {

					editor1.putString("product" + i,
							preferences.getString("product" + (i + 1), ""));
				}
				editor1.putString("product" + (length - 1), temp);
				editor1.commit();
			}
			Log.i("ok", "存储成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 从内存中读取通话记录
	 * 
	 * @param i
	 */
	public static void readProduct(int i, List<PhoneRecord> listPhoneRecord,
			Context context) {
		SharedPreferences preferences = context.getSharedPreferences("base64",
				Context.MODE_PRIVATE);
		String productBase64 = preferences.getString("product" + i, "");
		if (productBase64 == "") {
			return;
		}
		// 读取字节
		byte[] base64 = Base64.decodeBase64(productBase64.getBytes());

		// 封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			// 再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				// 读取对象
				PhoneRecord mProdect = (PhoneRecord) bis.readObject();
				if (mProdect != null) {
					listPhoneRecord.add(mProdect);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取通话记录条数
	 * 
	 * @param length
	 * @param pr
	 * @param context
	 */
	public static int getPhoneLength(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("base64",
				Context.MODE_PRIVATE);
		int length = preferences.getInt("length1", 0);
		return length;
	}

	public static void setNetFlag(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("base64",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("net_phone", true);
		editor.commit();
	}

	public static boolean getNetFlag(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("base64",
				Context.MODE_PRIVATE);
		return preferences.getBoolean("net_phone", true);
	}

	public static void setPhoneUrl(Context context, String url) {
		SharedPreferences sp = context.getSharedPreferences("phone_url",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("url", url);
		editor.commit();
	}

	public static String getPhoneUrl(Context context) {
		SharedPreferences sp = context.getSharedPreferences("phone_url",
				Context.MODE_PRIVATE);
		return sp.getString("url", "");
	}

	/**
	 * 保存登录的电话和密码
	 * 
	 * @param context
	 * @param tel
	 * @param pass
	 */
	public static void setUser(Context context, String tel, String pass) {
		SharedPreferences preferences = context.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("tel", tel);
		editor.putString("pass", pass);
		editor.commit();
	}

	/**
	 * 清空登录的电话和密码
	 * 
	 * @param context
	 * @param tel
	 * @param pass
	 */
	public static void clearUser(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 获取登录时保存的手机号码信息
	 * 
	 * @param context
	 * @return
	 */
	public static String getTelFromUser(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		return preferences.getString("tel", "");
	}

	/**
	 * 获取登录时保存的密码信息
	 * 
	 * @param context
	 * @return
	 */
	public static String getPassFromUser(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		return preferences.getString("pass", "");
	}

	/**
	 * 清除登录时保存的密码信息
	 * 
	 * @param context
	 * @return
	 */
	public static void clearPassFromUser(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("pass", "");
		editor.commit();
	}

	/**
	 * 判断是否推送消息
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isPushed(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("push",
				Context.MODE_PRIVATE);
		return preferences.getBoolean("isPush", false);
	}

	/**
	 * 设置推送标志位
	 * 
	 * @param context
	 */
	public static void setPushed(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("push",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isPush", true);
		editor.commit();
	}

	/**
	 * 设置推送标志位
	 * 
	 * @param context
	 */
	public static void clearPushed(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("push",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 添加String串到SharedPreference�?
	 * 
	 * @param context
	 *            Context
	 * @param key
	 * 
	 * @param value
	 * 
	 */
	public static void saveString(final Context context, final String key,
			final String value) {
		SharedPreferences preference = context.getSharedPreferences(
				PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor = preference.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 清除本地“dock_preferences”的数据
	 * 
	 * @param context
	 */
	public static void clearLocalValues(final Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor = preference.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 添加int到SharedPreference�?
	 * 
	 * @param context
	 *            Context
	 * @param key
	 * @param value
	 */
	public static void saveInt(final Context context, String key, int value) {
		SharedPreferences preference = context.getSharedPreferences(
				PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor = preference.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 添加boolean到SharedPreference�?
	 * 
	 * @param context
	 *            Context
	 * @param key
	 * @param value
	 */
	public static void saveBoolean(final Context context, String key,
			Boolean value) {
		SharedPreferences preference = context.getSharedPreferences(
				PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor = preference.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 获取String
	 * 
	 * @param context
	 * @param key
	 *            名称
	 * @return 键对应的值，如果找不到对应的值， 则返回Null
	 */
	public static String getStringValue(final Context context, final String key) {
		SharedPreferences preference = context.getSharedPreferences(
				PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		return preference.getString(key, null);
	}

	/**
	 * 获取Boolean
	 * 
	 * @param context
	 * @param key
	 *            名称
	 * @return 键对应的值，如果找不到对应的值， 则返回false
	 */
	public static boolean getBooleanValue(final Context context,
			final String key) {
		SharedPreferences preference = context.getSharedPreferences(
				PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		return preference.getBoolean(key, false);
	}

	/**
	 * 获取String
	 * 
	 * @param context
	 * @param key
	 *            名称
	 * @return 键对应的值，如果找不到对应的值， 则返�?1
	 */
	public static int getIntValue(final Context context, final String key) {
		SharedPreferences preference = context.getSharedPreferences(
				PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		return preference.getInt(key, -1);
	}

	/**
	 * 设置服务大厅保存的城市
	 * 
	 * @param context
	 * @param city
	 */
	public static void setServiceHallCity(Context context, String city) {
		SharedPreferences preferences = context.getSharedPreferences("city",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("service_city", city);
		editor.commit();
	}

	/**
	 * 保存服务大厅保存的城市
	 * 
	 * @param context
	 * @param city
	 */
	public static String getServiceHallCity(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("city",
				Context.MODE_PRIVATE);
		return preferences.getString("service_city", "");
	}
	
	public static double getLatDynamic(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("dynamic",
				Context.MODE_PRIVATE);
		return preferences.getFloat("lat", 0.00f);
	}
	
	public static void setLatDynamic(Context context,float lat){
		SharedPreferences preferences = context.getSharedPreferences("dynamic",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putFloat("lat", lat);
		editor.commit();
	}
	public static double getLngDynamic(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("dynamic",
				Context.MODE_PRIVATE);
		return preferences.getFloat("lng", 0.00f);
	}
	
	public static void setLngDynamic(Context context,float lng){
		SharedPreferences preferences = context.getSharedPreferences("dynamic",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putFloat("lng", lng);
		editor.commit();
	}
}
