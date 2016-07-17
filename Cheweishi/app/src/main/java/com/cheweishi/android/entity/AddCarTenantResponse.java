package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

/**
 * Created by Administrator on 2016/7/16.
 */
public class AddCarTenantResponse extends BaseResponse {

//    {"msg":{"vehicleId":60},"token":"1eac41b1-127f-4295-ac59-c390c7a2f3c3__1468671609130","desc":null,"code":"0000"}
    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        int vehicleId;

        public int getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(int vehicleId) {
            this.vehicleId = vehicleId;
        }
    }
}
