package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

/**
 * Created by tangce on 3/31/2016.
 */
public class OrderDetailResponse extends BaseResponse {


    /**
     * price : 20
     * tenantInfo : {"praiseRate":3,"address":null,"tenantName":"爱车","latitude":10.000021,"photo":"/upload/tenant/photo/12.jpg","id":1,"businessTime":null,"contactPhone":null,"longitude":10.000021}
     * id : 1
     * serviceName : 车身保养
     * recordNo : null
     * createDate : 1458724731000
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private int price;
        /**
         * praiseRate : 3
         * address : null
         * tenantName : 爱车
         * latitude : 10.000021
         * photo : /upload/tenant/photo/12.jpg
         * id : 1
         * businessTime : null
         * contactPhone : null
         * longitude : 10.000021
         */

        private TenantInfoBean tenantInfo;
        private int id;
        private String serviceName;
        private String recordNo;
        private long createDate;

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public TenantInfoBean getTenantInfo() {
            return tenantInfo;
        }

        public void setTenantInfo(TenantInfoBean tenantInfo) {
            this.tenantInfo = tenantInfo;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getRecordNo() {
            return recordNo;
        }

        public void setRecordNo(String recordNo) {
            this.recordNo = recordNo;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public static class TenantInfoBean {
            private int praiseRate;
            private String address;
            private String tenantName;
            private double latitude;
            private String photo;
            private int id;
            private String businessTime;
            private String contactPhone;
            private double longitude;
            private String chargeStatus;

            public String getChargeStatus() {
                return chargeStatus;
            }

            public void setChargeStatus(String chargeStatus) {
                this.chargeStatus = chargeStatus;
            }

            public int getPraiseRate() {
                return praiseRate;
            }

            public void setPraiseRate(int praiseRate) {
                this.praiseRate = praiseRate;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getTenantName() {
                return tenantName;
            }

            public void setTenantName(String tenantName) {
                this.tenantName = tenantName;
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

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getBusinessTime() {
                return businessTime;
            }

            public void setBusinessTime(String businessTime) {
                this.businessTime = businessTime;
            }

            public Object getContactPhone() {
                return contactPhone;
            }

            public void setContactPhone(String contactPhone) {
                this.contactPhone = contactPhone;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }
        }
    }
}
