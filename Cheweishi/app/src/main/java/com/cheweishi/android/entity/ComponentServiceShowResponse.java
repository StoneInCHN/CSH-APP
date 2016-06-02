package com.cheweishi.android.entity;

/**
 * Created by tangce on 6/1/2016.
 */
public class ComponentServiceShowResponse {

    private String serviceName;

    private String totalMoney;

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    private ComponentServiceShowResponse.MsgBean msg;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private String name;
        private String price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }


}
