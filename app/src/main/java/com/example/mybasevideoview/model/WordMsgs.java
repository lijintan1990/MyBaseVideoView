package com.example.mybasevideoview.model;

import java.util.List;

public class WordMsgs {

    /**
     * status : 0
     * msg : string
     * data : [{"content":"string","id":0,"imgUrl":"string","sort":0,"textId":0,"type":0}]
     */

    private int status;
    private String msg;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * content : string
         * id : 0
         * imgUrl : string
         * sort : 0
         * textId : 0
         * type : 0
         */

        private String content;
        private int id;
        private String imgUrl;
        private int sort;
        private int textId;
        private int type;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getTextId() {
            return textId;
        }

        public void setTextId(int textId) {
            this.textId = textId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
