package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

import java.util.List;

/**
 * Created by Tanck on 10/11/2016.
 * <p>
 * Describe:
 */
public class ShopOrderListResponse extends BaseResponse {

    /**
     * total : 8
     * pageNumber : 1
     * pageSize : 2
     */

    private PageBean page;
    /**
     * amount : 601
     * consignee : andrea
     * address : 天府软件园D区
     * orderItem : [{"thumbnail":null,"quantity":1,"price":601,"name":"熊猫座椅","id":11}]
     * freight : 0
     * orderStatus : unconfirmed
     * productCount : 1
     * shippingStatus : unshipped
     * phone : 15892999216
     * areaName : 四川省成都市
     * id : 9
     * sn : 20160825202
     * paymentStatus : unpaid
     * createDate : 1472123141000
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
        private String amount;
        private String consignee;
        private String address;
        private int freight;
        private String orderStatus;
        private int productCount;
        private String shippingStatus;
        private String phone;
        private String areaName;
        private int id;
        private String sn;
        private String paymentStatus;
        private long createDate;
        /**
         * thumbnail : null
         * quantity : 1
         * price : 601
         * name : 熊猫座椅
         * id : 11
         */

        private List<OrderItemBean> orderItem;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getConsignee() {
            return consignee;
        }

        public void setConsignee(String consignee) {
            this.consignee = consignee;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getFreight() {
            return freight;
        }

        public void setFreight(int freight) {
            this.freight = freight;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public int getProductCount() {
            return productCount;
        }

        public void setProductCount(int productCount) {
            this.productCount = productCount;
        }

        public String getShippingStatus() {
            return shippingStatus;
        }

        public void setShippingStatus(String shippingStatus) {
            this.shippingStatus = shippingStatus;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public List<OrderItemBean> getOrderItem() {
            return orderItem;
        }

        public void setOrderItem(List<OrderItemBean> orderItem) {
            this.orderItem = orderItem;
        }

        public static class OrderItemBean {
            private String thumbnail;
            private int quantity;
            private int price;
            private String name;
            private int id;

            public String getThumbnail() {
                return thumbnail;
            }

            public void setThumbnail(String thumbnail) {
                this.thumbnail = thumbnail;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }

            public int getPrice() {
                return price;
            }

            public void setPrice(int price) {
                this.price = price;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
    }
}