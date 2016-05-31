package com.cheweishi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CircleInformation implements Parcelable {
	private String id;
	private String title;
	private String pic;
	private String content;
	private String type;
	private String logo;
	private String time;
	private String details;

	private int w;
	private int h;

	/** 下面实现的是对象的序列化 */
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(pic);
		dest.writeString(content);
		dest.writeString(type);
		dest.writeString(logo);
		dest.writeString(time);
		dest.writeString(details);
		dest.writeInt(w);
		dest.writeInt(h);
	}

	public static final Parcelable.Creator<CircleInformation> CREATOR = new Creator<CircleInformation>() {
		public CircleInformation createFromParcel(Parcel source) {
			CircleInformation person = new CircleInformation();
			person.id = source.readString();
			person.title = source.readString();
			person.pic = source.readString();
			person.content = source.readString();
			person.type = source.readString();
			person.logo = source.readString();
			person.time = source.readString();
			person.w = source.readInt();
			person.h = source.readInt();
			person.details = source.readString();
			return person;
		}

		public CircleInformation[] newArray(int size) {
			return new CircleInformation[size];
		}
	};

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

}
