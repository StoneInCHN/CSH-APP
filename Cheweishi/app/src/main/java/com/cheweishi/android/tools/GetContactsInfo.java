package com.cheweishi.android.tools;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import com.cheweishi.android.entity.SortModel;

/**
 * 
 * 获取通讯录信息
 * 
 */
public class GetContactsInfo {
	Context context;

	public GetContactsInfo(Context context) {
		this.context = context;

	}

	List<SortModel> listSortModel = new ArrayList<SortModel>();
	SortModel sortModel;

	Cursor c;
	private String[] strArray = { "-", " ", "+86", "17951" };

	public ArrayList<SortModel> getContact() {
		System.out.println("haha");
		ArrayList<SortModel> listMembers = new ArrayList<SortModel>();
		Cursor cursor = null;

		try {

			Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			// 这里是获取联系人表的电话里的信息 包括：名字，名字拼音，联系人id,电话号码；
			// 然后在根据"sort-key"排序

			cursor = context.getContentResolver().query(

			uri,

			new String[] { "display_name", "sort_key", "contact_id",

			"data1" }, null, null, "sort_key");

			if (cursor.moveToFirst()) {

				do {
					SortModel contact = new SortModel();

					String contact_phone = cursor

							.getString(
									cursor

									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
							.replaceAll(" ", "");

					String name = cursor.getString(0);
					name = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					// String sortKey = getSortKey(cursor.getString(1));

					System.out.println("haha" + name + "_" + contact_phone);
					// int contact_id = cursor
					//
					// .getInt(cursor
					//
					// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
					if (contact_phone != null) {
						for (int i = 0; i < strArray.length; i++) {
							if (contact_phone.length() > strArray[i].length()) {
								if (contact_phone.startsWith(strArray[i])) {
									contact_phone = contact_phone.substring(
											strArray[i].length(),
											contact_phone.length());
								}
							}
						}

						if (RegularExpressionTools.isMobile(contact_phone) == true
								|| RegularExpressionTools
										.isRightPhoen(contact_phone) == true) {

							contact.setName(name);
							contact.setPhone(contact_phone);
							contact.setBitmap(null);
							listMembers.add(contact);
						}
					}

				} while (cursor.moveToNext());

				c = cursor;

			}

		} catch (Exception e) {
			if (cursor != null) {
				cursor.close();
			}
			e.printStackTrace();

		} finally {

			context = null;
			if (cursor != null) {
				cursor.close();
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return listMembers;

	}

}
