package com.cheweishi.android.adapter;

import com.cheweishi.android.response.BaseResponse;

/**
 * Created by tangce on 7/18/2016.
 */
public class AddCarResponse extends BaseResponse {

    /**
     * vehicleId : 73
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private String vehicleId;

        public String getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(String vehicleId) {
            this.vehicleId = vehicleId;
        }
    }
}
