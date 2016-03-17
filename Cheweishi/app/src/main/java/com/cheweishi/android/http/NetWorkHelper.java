package com.cheweishi.android.http;

/**
 * Created by tangce on 3/16/2016.
 */
public class NetWorkHelper {

    private static NetWorkHelper instance;

    private NetWorkHelper() {
    }

    public static NetWorkHelper getInstance() {
        if (null == instance) {
            synchronized (NetWorkHelper.class) {
                if (null == instance)
                    instance = new NetWorkHelper();
            }
        }

        return instance;
    }




}
