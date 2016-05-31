package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

/**
 * Created by tangce on 3/24/2016.
 */
public class DbLoginMessage extends BaseResponse {

    private int id;
    private String signature;
    private String nickName;
    private String photo;
    private String uid;
    private String userName;
    private String defaultVehicle;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
