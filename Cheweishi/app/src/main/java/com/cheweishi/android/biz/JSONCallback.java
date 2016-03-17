package com.cheweishi.android.biz;


import java.io.File;

import com.lidroid.xutils.http.ResponseInfo;
/**
 * JSON数据和下载文件回调接口
 * @author 胡健
 * @time 2015.6.15
 */
public interface JSONCallback {
	/**
	 * json数据返回
	 * @param type
	 * @param data
	 */
	public  void receive( int type,String  data);
	/**
	 * 下载文件的返回
	 * @param type
	 * @param arg0
	 */
	public  void downFile(int type,ResponseInfo<File> arg0);
}
