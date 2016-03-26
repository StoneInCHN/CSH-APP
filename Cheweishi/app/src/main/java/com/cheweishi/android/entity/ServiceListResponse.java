package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

import java.util.List;

/**
 * Created by tangce on 3/24/2016.
 */
public class ServiceListResponse extends BaseResponse {

    /**
     * total : 6
     * pageNumber : 1
     * pageSize : 5
     */

    private PageBean page;
    /**
     * tenant_name : 中和汽修
     * praiseRate : 0.4
     * address : null
     * service_category_name : 洗车
     * distance : 3.36
     * price : 50
     * photo : null
     * id : 3
     * promotion_price : 30
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

    public static class PageBean {
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

    public static class MsgBean {
        private String tenant_name;
        private int praiseRate;
        private String address;
        private String service_category_name;
        private double distance;
        private int price;
        private String photo;
        private int id;
        private int promotion_price;

        public String getTenant_name() {
            return tenant_name;
        }

        public void setTenant_name(String tenant_name) {
            this.tenant_name = tenant_name;
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

        public String getService_category_name() {
            return service_category_name;
        }

        public void setService_category_name(String service_category_name) {
            this.service_category_name = service_category_name;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
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

        public int getPromotion_price() {
            return promotion_price;
        }

        public void setPromotion_price(int promotion_price) {
            this.promotion_price = promotion_price;
        }
    }
}
