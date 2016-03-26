package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

import java.util.List;

/**
 * Created by tangce on 3/25/2016.
 */
public class AdvResponse extends BaseResponse {

    /**
     * page : null
     * msg : [{"advContentLink":"http://www.baidu1.com","id":2,"advImageUrl":"http://www.baidu2.com"}]
     */

    private Object page;
    /**
     * advContentLink : http://www.baidu1.com
     * id : 2
     * advImageUrl : http://www.baidu2.com
     */

    private List<MsgBean> msg;

    public Object getPage() {
        return page;
    }

    public void setPage(Object page) {
        this.page = page;
    }

    public List<MsgBean> getMsg() {
        return msg;
    }

    public void setMsg(List<MsgBean> msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private String advContentLink;
        private int id;
        private String advImageUrl;

        public String getAdvContentLink() {
            return advContentLink;
        }

        public void setAdvContentLink(String advContentLink) {
            this.advContentLink = advContentLink;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAdvImageUrl() {
            return advImageUrl;
        }

        public void setAdvImageUrl(String advImageUrl) {
            this.advImageUrl = advImageUrl;
        }
    }
}
