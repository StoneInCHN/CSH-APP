package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

/**
 * Created by tangce on 4/7/2016.
 */
public class QRServerResponse extends BaseResponse {


    /**
     * appTitleName : 车生活2
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private String appTitleName;

        public String getAppTitleName() {
            return appTitleName;
        }

        public void setAppTitleName(String appTitleName) {
            this.appTitleName = appTitleName;
        }
    }
}
