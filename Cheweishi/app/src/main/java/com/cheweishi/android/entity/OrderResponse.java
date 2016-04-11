package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tangce on 3/27/2016.
 */
public class OrderResponse extends BaseResponse implements Serializable {


    /**
     * total : 1
     * pageNumber : 1
     * pageSize : 12
     */

    private PageBean page;
    /**
     * tenantName : 爱车
     * price : 20
     * carService : {"serviceName":"车身保养","serviceCategory":{"id":1,"createDate":1450860985000,"modifyDate":1450860985000,"categoryName":"保养"}}
     * chargeStatus : PAID
     * createDate : 1458724731000
     */

    private List<MsgBean> msg;

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<MsgBean> getMsg() {
        return msg;
    }

    public void setMsg(List<MsgBean> msg) {
        this.msg = msg;
    }

    public static class PageBean implements Serializable {
        private int total;
        private int pageNumber;
        private int pageSize;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }

    public static class MsgBean implements Serializable {
        private boolean isRate;
        private String tenantName;
        private int price;
        private int id;
        /**
         * serviceName : 车身保养
         * serviceCategory : {"id":1,"createDate":1450860985000,"modifyDate":1450860985000,"categoryName":"保养"}
         */
        private String tenantPhoto;
        private CarServiceBean carService;
        private String chargeStatus;
        private long createDate;

        private String tenantID;

        private long paymentDate;


        public boolean isRate() {
            return isRate;
        }

        public void setRate(boolean rate) {
            isRate = rate;
        }

        public String getTenantID() {
            return tenantID;
        }

        public void setTenantID(String tenantID) {
            this.tenantID = tenantID;
        }

        public String getTenantPhoto() {
            return tenantPhoto;
        }

        public void setTenantPhoto(String tenantPhoto) {
            this.tenantPhoto = tenantPhoto;
        }

        public long getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(long paymentDate) {
            this.paymentDate = paymentDate;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTenantName() {
            return tenantName;
        }

        public void setTenantName(String tenantName) {
            this.tenantName = tenantName;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public CarServiceBean getCarService() {
            return carService;
        }

        public void setCarService(CarServiceBean carService) {
            this.carService = carService;
        }

        public String getChargeStatus() {
            return chargeStatus;
        }

        public void setChargeStatus(String chargeStatus) {
            this.chargeStatus = chargeStatus;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public static class CarServiceBean implements Serializable {
            private String serviceName;
            /**
             * id : 1
             * createDate : 1450860985000
             * modifyDate : 1450860985000
             * categoryName : 保养
             */

            private int id;

            private ServiceCategoryBean serviceCategory;

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

            public static class ServiceCategoryBean implements Serializable {
                private int id;
                private long createDate;
                private long modifyDate;
                private String categoryName;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public long getCreateDate() {
                    return createDate;
                }

                public void setCreateDate(long createDate) {
                    this.createDate = createDate;
                }

                public long getModifyDate() {
                    return modifyDate;
                }

                public void setModifyDate(long modifyDate) {
                    this.modifyDate = modifyDate;
                }

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
