package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

import java.util.List;

/**
 * Created by tangce on 5/13/2016.
 */
public class ActivityCouponResponse extends BaseResponse{


    /**
     * total : 3
     * pageNumber : 1
     * pageSize : 5
     */

    private PageBean page;
    /**
     * isGet : false
     * amount : 34
     * overDueTime : 2016-05-14
     * remainNum : 100
     * remark : 租户服务红包
     * type : COMMON
     * id : 1
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
        private boolean isGet;
        private int amount;
        private String deadlineTime;
        private int remainNum;
        private String remark;
        private String type;
        private int id;

        public boolean isIsGet() {
            return isGet;
        }

        public void setIsGet(boolean isGet) {
            this.isGet = isGet;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public boolean isGet() {
            return isGet;
        }

        public void setGet(boolean get) {
            isGet = get;
        }

        public String getDeadlineTime() {
            return deadlineTime;
        }

        public void setDeadlineTime(String deadlineTime) {
            this.deadlineTime = deadlineTime;
        }

        public int getRemainNum() {
            return remainNum;
        }

        public void setRemainNum(int remainNum) {
            this.remainNum = remainNum;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
