package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

import java.util.List;

/**
 * Created by tangce on 3/27/2016.
 */
public class MaintainResponse extends BaseResponse {

    /**
     * address : null
     * tenantName : 爱车帮
     * latitude : 10.000021
     * photo : null
     * id : 1
     * businessTime : null
     * contactPhone : null
     * longitude : 10.000021
     * carServices : [{"promotionPrice":30,"price":50,"serviceStatus":"ENABLED","id":1,"serviceName":"车身美容","serviceCategory":{"categoryName":"美容"}},{"promotionPrice":30,"price":50,"serviceStatus":"ENABLED","id":2,"serviceName":"打蜡","serviceCategory":{"categoryName":"美容"}}]
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private Object address;
        private String tenantName;
        private double latitude;
        private Object photo;
        private int id;
        private Object businessTime;
        private Object contactPhone;
        private double longitude;
        /**
         * promotionPrice : 30
         * price : 50
         * serviceStatus : ENABLED
         * id : 1
         * serviceName : 车身美容
         * serviceCategory : {"categoryName":"美容"}
         */

        private List<CarServicesBean> carServices;

        public Object getAddress() {
            return address;
        }

        public void setAddress(Object address) {
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

        public Object getPhoto() {
            return photo;
        }

        public void setPhoto(Object photo) {
            this.photo = photo;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getBusinessTime() {
            return businessTime;
        }

        public void setBusinessTime(Object businessTime) {
            this.businessTime = businessTime;
        }

        public Object getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(Object contactPhone) {
            this.contactPhone = contactPhone;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public List<CarServicesBean> getCarServices() {
            return carServices;
        }

        public void setCarServices(List<CarServicesBean> carServices) {
            this.carServices = carServices;
        }

        public static class CarServicesBean {
            private int promotionPrice;
            private int price;
            private String serviceStatus;
            private int id;
            private String serviceName;
            /**
             * categoryName : 美容
             */

            private ServiceCategoryBean serviceCategory;

            public int getPromotionPrice() {
                return promotionPrice;
            }

            public void setPromotionPrice(int promotionPrice) {
                this.promotionPrice = promotionPrice;
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

            public ServiceCategoryBean getServiceCategory() {
                return serviceCategory;
            }

            public void setServiceCategory(ServiceCategoryBean serviceCategory) {
                this.serviceCategory = serviceCategory;
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
