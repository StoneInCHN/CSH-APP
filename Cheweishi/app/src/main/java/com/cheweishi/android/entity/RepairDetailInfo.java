package com.cheweishi.android.entity;

public class RepairDetailInfo {
	private String id;// ": 1,
	private String des;// ": "这些经纬线是怎样定出来的呢？地球是在不停地绕地轴旋转（地轴是一根通过地球南北两极和地球中心的\r\n假想线），在地球中腰画一个与地轴垂直的大圆圈，使圈上的每一点都和南北两极的距离相等，这个圆圈\r\n就叫作“赤道”。在赤道的南北两边，画出许多和赤道平行的圆圈，就是“纬圈”；构成这些圆圈的线段，\r\n叫做纬线。我们把赤道定为纬度零度，向南向北各为90度，在赤道以南的叫南纬，在赤道以北的叫北纬。\r\n北极就是北纬90度，南极就是南纬90度。纬度的高低也标志着气候的冷热，如赤道和低纬度地地区无冬，\r\n两极和高纬度地区无夏，中纬度地区四季分明。",
	private String title;// ": "渝成代驾1",
	private String tag;// ": "商务代驾",
	private String tel;// ": "13983690946",
	private String click;// ": 0,
	private double lng;// ": 106.542852,
	private String addr;// ": "重庆市洋河中路42号",
	private Pic[] pic;// ": "",
	private String photo;// ": "service/20150412/20150412_2a0eef9865f7482d9da184ea617e8f66.jpg",
	private double lat;// ": 29.581928
	private Pic[] large;
	private Pic[] small;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getClick() {
		return click;
	}

	public void setClick(String click) {
		this.click = click;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public Pic[] getPic() {
		return pic;
	}

	public void setPic(Pic[] pic) {
		this.pic = pic;
	}

	public Pic[] getLarge() {
		return large;
	}

	public void setLarge(Pic[] large) {
		this.large = large;
	}

	public Pic[] getSmall() {
		return small;
	}

	public void setSmall(Pic[] small) {
		this.small = small;
	}

}
