package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

/**
 * Created by tangce on 3/23/2016.
 */
public class LoginResponse extends BaseResponse {

    /**
     * signature : null
     * nickName : null
     * photo : null
     * id : 1
     * userName : 15892999216
     * defaultVehicle : 一汽大众-奥迪A3
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private String signature;
        private String nickName;
        private String photo;
        private String id;
        private String userName;
        private String defaultVehicle;

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getDefaultVehicle() {
            return defaultVehicle;
        }

        public void setDefaultVehicle(String defaultVehicle) {
            this.defaultVehicle = defaultVehicle;
        }
    }
}
