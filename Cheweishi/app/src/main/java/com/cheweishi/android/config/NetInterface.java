package com.cheweishi.android.config;

/**
 * Created by Administrator on 2016/3/20.
 */
public class NetInterface {

//    public static final String BASE_URL = "http://127.0.0.1";// 基础路径
    public static final String BASE_URL = "http://10.50.40.56:8081";// 基础路径


    public static final String TEMP_URL = "/csh-interface/endUser/";// 中间路径

    public static final String HEADER_ALL=BASE_URL+ TEMP_URL;

    public static final String RESPONSE_SUCCESS = "0000" ; // success

    public static final String SUFFIX = ".jhtml";//后缀


    /******************接口*********************/
    public static final String  USER_LOGIN = "login";

    public static final String SMS_TOKEN ="getSmsToken"; // 验证码接口

    public static final String RESET_PASSWORD ="resetPwd"; // 修改密码

    public static final String REGISTER = "reg"; // 注册

    public static final String EDIT_USER_INFO="editUserInfo";//昵称修改
}
