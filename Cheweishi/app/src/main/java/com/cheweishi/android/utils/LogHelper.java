package com.cheweishi.android.utils;

import android.util.Log;

/**
 * Created by tangce on 3/23/2016.
 */
public class LogHelper {
    private static boolean DEBUG = true;

    private static String TAG = "Tanck";

    public static void d(String content) {
        if (DEBUG && null != content)
            Log.d(TAG, content);
    }


    public static void e(String content) {
        if (DEBUG && null != content)
            Log.e(TAG, content);
    }
}
