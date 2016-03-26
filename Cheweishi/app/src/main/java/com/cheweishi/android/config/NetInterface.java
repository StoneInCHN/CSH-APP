package com.cheweishi.android.config;

/**
 * Created by Administrator on 2016/3/20.
 */
public class NetInterface {

    public static final String BASE_URL = "http://139.129.5.114";// 基础路径
//    public static final String BASE_URL = "http://10.50.40.56:8081";// 基础路径


    public static final String TEMP_URL = "/csh-interface/endUser/";// 中间路径

    public static final String TEMP_HOME_URL ="/csh-interface/tenantInfo/";//租户相关的

    public static final String TEMP_CAR_URL = "/csh-interface/vehicle/";// 车辆管理

    public static final String TEMP_ADV_URL ="/csh-interface/advertisement/"; // 广告

    public static final String HEADER_ALL=BASE_URL+ TEMP_URL;

    public static final String RESPONSE_SUCCESS = "0000" ; // success

    public static final String RESPONSE_TOKEN = "0004" ; // ERROR

    public static final String SUFFIX = ".jhtml";//后缀


    /******************接口*********************/
    public static final String  USER_LOGIN = "login";

    public static final String SMS_TOKEN ="getSmsToken"; // 验证码接口

    public static final String RESET_PASSWORD ="resetPwd"; // 修改密码

    public static final String REGISTER = "reg"; // 注册

    public static final String EDIT_USER_INFO="editUserInfo";//昵称修改

    public static final String LIST = "list";// 列表

    public static final String HOME_ADV= "getAdvImage"; // 首页广告


}
