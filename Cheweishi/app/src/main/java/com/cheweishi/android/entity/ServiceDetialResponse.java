package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/3/26.
 */
public class ServiceDetialResponse extends BaseResponse implements Serializable{

    /**
     * id : 3
     * carServices : [{"id":3,"serviceCategory":{"categoryName":"洗车"},"price":50,"serviceStatus":"ENABLED","promotionPrice":30,"serviceName":"普通洗车"}]
     * contactPhone : null
     * address : null
     * businessTime : null
     * longitude : 10.000022
     * latitude : 10.000021
     * photo : http://10.50.40.56:8081/csh-interface/upload/2.jpg
     * tenantName : 中和汽修
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean implements Serializable {
        private int id;
        private Object contactPhone;
        private Object address;
        private Object businessTime;
        private double longitude;
        private double latitude;
        private String photo;
        private String tenantName;
        /**
         * id : 3
         * serviceCategory : {"categoryName":"洗车"}
         * price : 50
         * serviceStatus : ENABLED
         * promotionPrice : 30
         * serviceName : 普通洗车
         */

        private List<CarServicesBean> carServices;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(Object contactPhone) {
            this.contactPhone = contactPhone;
        }

        public Object getAddress() {
            return address;
        }

        public void setAddress(Object address) {
            this.address = address;
        }

        public Object getBusinessTime() {
            return businessTime;
        }

        public void setBusinessTime(Object businessTime) {
            this.businessTime = businessTime;
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

        public List<CarServicesBean> getCarServices() {
            return carServices;
        }

        public void setCarServices(List<CarServicesBean> carServices) {
            this.carServices = carServices;
        }

        public static class CarServicesBean implements Serializable{
            private int id;
            /**
             * categoryName : 洗车
             */

            private ServiceCategoryBean serviceCategory;
            private int price;
            private String serviceStatus;
            private int promotionPrice;
            private String serviceName;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public ServiceCategoryBean getServiceCategory() {
                return serviceCategory;
            }

            public void setServiceCategory(ServiceCategoryBean serviceCategory) {
                this.serviceCategory = serviceCategory;
            }

            public int getPrice() {
                return price;
            }

            public void setPrice(int price) {
                this.price = price;
            }

            public String getServiceStatus() {
                return serviceStatus;
            }

            public void setServiceStatus(String serviceStatus) {
                this.serviceStatus = serviceStatus;
            }

            public int getPromotionPrice() {
                return promotionPrice;
            }

            public void setPromotionPrice(int promotionPrice) {
                this.promotionPrice = promotionPrice;
            }

            public String getServiceName() {
                return serviceName;
            }

            public void setServiceName(String serviceName) {
                this.serviceName = serviceName;
            }

            public static class ServiceCategoryBean {
                private String categoryName;

                public String getCategoryName() {
                    return categoryName;
                }

                public void setCategoryName(String categoryName) {
                    this.categoryName = categoryName;
                }
            }
        }
    }
}
