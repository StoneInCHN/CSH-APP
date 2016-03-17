package com.cheweishi.android.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.navisdk.util.common.NetworkUtils;
import com.cheweishi.android.config.Constant;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangce on 3/16/2016.
 */
public class NetWorkHelper {

    private static final int TIMEOUT_POST = 10000;
    private static NetWorkHelper instance;

    private Context context;

    private String RequestTag;
    private RequestQueue requestQueue;

    private NetWorkHelper(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static NetWorkHelper getInstance(Context context) {
        if (null == instance) {
            synchronized (NetWorkHelper.class) {
                if (null == instance)
                    instance = new NetWorkHelper(context);
            }
        }

        return instance;
    }


    public void PostJson(String url, Map<String, Object> params) {

        if (!NetworkUtils.isNetworkAvailable(context)) {
//            listener.onErrorResponse("当前没有网络...");
            return;
        }

        RequestTag = null;
        if (null != params.get(Constant.PARAMETER_TAG)) {
            RequestTag = params.get(Constant.PARAMETER_TAG).toString();
            params.remove(Constant.PARAMETER_TAG);
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (null != RequestTag) {
                    // TODO 有TAG的情况
//                    listener.onResponse(jsonObject.toString(), RequestTag);
                } else {
                    // TODO 没有TAG的情况

//                    listener.onResponse(jsonObject.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d(error.getMessage());
                if (null != error.networkResponse) {
                    byte[] htmlBodyBytes = error.networkResponse.data;
                    LogUtils.d(new String(htmlBodyBytes));
                }
//                listener.onErrorResponse(error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<String, String>();

                headers.put("Accept", "application/json");

                headers.put("Content-Type", "application/json; charset=UTF-8");

                return headers;
            }
        };
        request.setRetryPolicy(getRetryPolicy());
        requestQueue.add(request);

    }


    /**
     * 重试策略
     */

    private RetryPolicy getRetryPolicy() {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(TIMEOUT_POST,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }

}
