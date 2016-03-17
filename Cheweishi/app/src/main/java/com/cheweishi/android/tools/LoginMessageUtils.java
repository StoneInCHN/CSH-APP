package com.cheweishi.android.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import org.apache.commons.codec.binary.Base64;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.fragement.BaseFragment;

public class LoginMessageUtils {
	public static boolean showDialogFlag = false;

	// public static LoginMessage loginMessage;

	public static synchronized String getMessage(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"loginData", Context.MODE_PRIVATE);
		String message = sharedPreferences.getString(key, null);
		return message;

	}

	public static void setLogined(Context context, boolean isLogined) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"loginData", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("isLogined", isLogined);
		editor.commit();
	}

	public static void saveProduct(LoginMessage pr, Context context) {
		BaseActivity.loginMessage = pr;
		BaseFragment.loginMessage = pr;
		SharedPreferences preferences = context.getSharedPreferences("base64",
				Context.MODE_PRIVATE);
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
			// Toast.makeText(context, "存储成功！", Toast.LENGTH_LONG).show();
			Editor editor = preferences.edit();
			editor.putString("loginMessage", productBase64);
			editor.commit();
			Log.i("ok", "存储成功");
			oos.close();
			// Toast.makeText(LoginActivity.this, "存储成功！",
			// Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			// Toast.makeText(LoginActivity.this, "存储失败！",
			// Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constant.loginMessage = getLoginMessage(context);
	}

	public static LoginMessage getLoginMessage(Context context) {
		LoginMessage loginMessageTemp = null;
		if (context != null) {
			SharedPreferences preferences = context.getSharedPreferences(
					"base64", Context.MODE_PRIVATE);
			String productBase64 = preferences.getString("loginMessage", "");
			if (productBase64 == "") {
				// init();
				// System.out.println("不好");
				return null;
			}
			System.out.println("不好");
			// 读取字节
			byte[] base64 = Base64.decodeBase64(productBase64.getBytes());

			// 封装到字节流
			ByteArrayInputStream bais = new ByteArrayInputStream(base64);
			try {
				// 再次封装
				ObjectInputStream bis = new ObjectInputStream(bais);
				try {
					// 读取对象
					loginMessageTemp = (LoginMessage) bis.readObject();
					if (loginMessageTemp != null) {
						System.out.println("很好");
					} else {
						System.out.println("不好的");
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bis.close();
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bais.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return loginMessageTemp;
	}

	public static void deleteLoginMessage(Context context) {
		BaseActivity.loginMessage = null;
		SharedPreferences preferences = context.getSharedPreferences("base64",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.remove("loginMessage");
		editor.commit();
	}

}
