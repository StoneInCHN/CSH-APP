package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

import java.util.List;

/**
 * Created by Administrator on 2016/8/25.
 */
public class ProductDetailResponse extends BaseResponse {

    /**
     * image : null
     * productImages : [{"mobileicon":null,"id":10},{"mobileicon":null,"id":11}]
     * productParam : {"重量":"10kg","大小":"大","材质":"棉质"}
     * reviews : {"score":12,"bizReply":"商家回复","member":{"userName":"15892999216","photo":"/upload/endUser/photo/src_8e97aef2-cb1d-4e71-9d83-081b96ddfe28.jpeg"},"id":2,"content":"1111111111111111","createDate":1461398979000}
     * reviewCount : 0
     * price : 601
     * cartProductCount : 0
     * fullName : 熊猫座椅
     * id : 2
     * sales : null
     * introduction : null
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private String image;
        /**
         * 重量 : 10kg
         * 大小 : 大
         * 材质 : 棉质
         */

//        private ProductParamBean productParam;
        /**
         * score : 12
         * bizReply : 商家回复
         * member : {"userName":"15892999216","photo":"/upload/endUser/photo/src_8e97aef2-cb1d-4e71-9d83-081b96ddfe28.jpeg"}
         * id : 2
         * content : 1111111111111111
         * createDate : 1461398979000
         */

        private List<ReviewsBean> reviews;
        private String reviewCount;
        private String price;
        private String cartProductCount;
        private String fullName;
        private int id;
        private String sales;
        private String introduction;
        /**
         * mobileicon : null
         * id : 10
         */

        private List<ProductImagesBean> productImages;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

//        public ProductParamBean getProductParam() {
//            return productParam;
//        }
//
//        public void setProductParam(ProductParamBean productParam) {
//            this.productParam = productParam;
//        }

        public List<ReviewsBean> getReviews() {
            return reviews;
        }

        public void setReviews(List<ReviewsBean> reviews) {
            this.reviews = reviews;
        }

        public String getReviewCount() {
            return reviewCount;
        }

        public void setReviewCount(String reviewCount) {
            this.reviewCount = reviewCount;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCartProductCount() {
            return cartProductCount;
        }

        public void setCartProductCount(String cartProductCount) {
            this.cartProductCount = cartProductCount;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSales() {
            return sales;
        }

        public void setSales(String sales) {
            this.sales = sales;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public List<ProductImagesBean> getProductImages() {
            return productImages;
        }

        public void setProductImages(List<ProductImagesBean> productImages) {
            this.productImages = productImages;
        }

        public static class ProductParamBean {
            private String 重量;
            private String 大小;
            private String 材质;

            public String get重量() {
                return 重量;
            }

            public void set重量(String 重量) {
                this.重量 = 重量;
            }

            public String get大小() {
                return 大小;
            }

            public void set大小(String 大小) {
                this.大小 = 大小;
            }

            public String get材质() {
                return 材质;
            }

            public void set材质(String 材质) {
                this.材质 = 材质;
            }
        }

        public static class ReviewsBean {
            private String score;
            private String bizReply;
            /**
             * userName : 15892999216
             * photo : /upload/endUser/photo/src_8e97aef2-cb1d-4e71-9d83-081b96ddfe28.jpeg
             */

            private MemberBean member;
            private int id;
            private String content;
            private long createDate;

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getBizReply() {
                return bizReply;
            }

            public void setBizReply(String bizReply) {
                this.bizReply = bizReply;
            }

            public MemberBean getMember() {
                return member;
            }

            public void setMember(MemberBean member) {
                this.member = member;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public long getCreateDate() {
                return createDate;
            }

            public void setCreateDate(long createDate) {
                this.createDate = createDate;
            }

            public static class MemberBean {
                private String userName;
                private String photo;

                public String getUserName() {
                    return userName;
                }

                public void setUserName(String userName) {
                    this.userName = userName;
                }

                public String getPhoto() {
                    return photo;
                }

                public void setPhoto(String photo) {
                    this.photo = photo;
                }
            }
        }

        public static class ProductImagesBean {
            private String mobileicon;
            private int id;

            public String getMobileicon() {
                return mobileicon;
            }

            public void setMobileicon(String mobileicon) {
                this.mobileicon = mobileicon;
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
