package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

/**
 * Created by tangce on 3/31/2016.
 */
public class OrderDetailResponse extends BaseResponse {


    /**
     * id : 71
     * tenantInfo : {"id":1,"contactPhone":null,"chargeStatus":null,"address":null,"businessTime":null,"praiseRate":3,"longitude":10.000021,"latitude":10.000021,"photo":"/upload/tenant/photo/12.jpg","tenantName":"爱车"}
     * price : 30
     * recordNo : 201604131626091235158001
     * finishDate : null
     * payDate : null
     * serviceFlag : 0
     * createDate : 1460535969000
     * subscribeDate : null
     * serviceName : 普通洗车
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private int id;
        /**
         * id : 1
         * contactPhone : null
         * chargeStatus : null
         * address : null
         * businessTime : null
         * praiseRate : 3
         * longitude : 10.000021
         * latitude : 10.000021
         * photo : /upload/tenant/photo/12.jpg
         * tenantName : 爱车
         */

        private TenantInfoBean tenantInfo;
        private int price;
        private String recordNo;
        private String finishDate;
        private String payDate;
        private int serviceFlag;
        private long createDate;
        private String subscribeDate;
        private String serviceName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public TenantInfoBean getTenantInfo() {
            return tenantInfo;
        }

        public void setTenantInfo(TenantInfoBean tenantInfo) {
            this.tenantInfo = tenantInfo;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getRecordNo() {
            return recordNo;
        }

        public void setRecordNo(String recordNo) {
            this.recordNo = recordNo;
        }

        public String getFinishDate() {
            return finishDate;
        }

        public void setFinishDate(String finishDate) {
            this.finishDate = finishDate;
        }

        public String getPayDate() {
            return payDate;
        }

        public void setPayDate(String payDate) {
            this.payDate = payDate;
        }

        public int getServiceFlag() {
            return serviceFlag;
        }

        public void setServiceFlag(int serviceFlag) {
            this.serviceFlag = serviceFlag;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public String getSubscribeDate() {
            return subscribeDate;
        }

        public void setSubscribeDate(String subscribeDate) {
            this.subscribeDate = subscribeDate;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public static class TenantInfoBean {
            private int id;
            private String contactPhone;
            private String chargeStatus;
            private String address;
            private String businessTime;
            private int praiseRate;
            private double longitude;
            private double latitude;
            private String photo;
            private String tenantName;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getContactPhone() {
                return contactPhone;
            }

            public void setContactPhone(String contactPhone) {
                this.contactPhone = contactPhone;
            }

            public String getChargeStatus() {
                return chargeStatus;
            }

            public void setChargeStatus(String chargeStatus) {
                this.chargeStatus = chargeStatus;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getBusinessTime() {
                return businessTime;
            }

            public void setBusinessTime(String businessTime) {
                this.businessTime = businessTime;
            }

            public int getPraiseRate() {
                return praiseRate;
            }

            public void setPraiseRate(int praiseRate) {
                this.praiseRate = praiseRate;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public String getPhoto() {
                return photo;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }

            public String getTenantName() {
                return tenantName;
            }

            public void setTenantName(String tenantName) {
                this.tenantName = tenantName;
            }
        }
    }
}
