package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tangce on 3/24/2016.
 */
public class ServiceListResponse extends BaseResponse implements Serializable {

    /**
     * pageSize : 5
     * total : 7
     * pageNumber : 1
     */

    private PageBean page;
    /**
     * id : 1
     * distance : 3.28
     * price : 88
     * address : null
     * service_category_name : 洗车
     * praiseRate : 3
     * longitude : 10.000021
     * tenant_name : 爱车
     * latitude : 10.000021
     * photo : http://10.50.40.56:8081/csh-interface/upload/Koala.jpg
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

    public static class PageBean implements Serializable {
        private int pageSize;
        private int total;
        private int pageNumber;

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

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
    }

    public static class MsgBean  implements Serializable{
        private int id;
        private double distance;
        private int price;
        private String address;
        private String service_category_name;
        private int praiseRate;
        private double longitude;
        private String tenant_name;
        private double latitude;
        private String photo;
        private int promotion_price;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getTenant_name() {
            return tenant_name;
        }

        public void setTenant_name(String tenant_name) {
            this.tenant_name = tenant_name;
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

        public int getPromotion_price() {
            return promotion_price;
        }

        public void setPromotion_price(int promotion_price) {
            this.promotion_price = promotion_price;
        }
    }
}
