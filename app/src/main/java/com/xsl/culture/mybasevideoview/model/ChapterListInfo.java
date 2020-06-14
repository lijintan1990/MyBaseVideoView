package com.xsl.culture.mybasevideoview.model;

import java.util.List;

public class ChapterListInfo {

    /**
     * status : 0
     * msg : string
     * data : [{"id":0,"code":"string","objId":0,"name":"string","type":0,"startTime":0,"duration":0,"scale":0,"relevanceType":0}]
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
         * id : 0
         * code : string
         * objId : 0
         * name : string
         * type : 0
         * startTime : 0
         * duration : 0
         * scale : 0
         * relevanceType : 0
         */

        private int id;
        private String code;
        private int objId;
        private String name;
        private String tcName;
        private String enName;
        private int type;
        private int startTime;
        private int duration;
        private int scale;
        private int relevanceType;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getObjId() {
            return objId;
        }

        public void setObjId(int objId) {
            this.objId = objId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getScale() {
            return scale;
        }

        public void setScale(int scale) {
            this.scale = scale;
        }

        public int getRelevanceType() {
            return relevanceType;
        }

        public void setRelevanceType(int relevanceType) {
            this.relevanceType = relevanceType;
        }

        public String getTcName() {
            return tcName;
        }

        public void setTcName(String tcName) {
            this.tcName = tcName;
        }

        public String getEnName() {
            return enName;
        }

        public void setEnName(String enName) {
            this.enName = enName;
        }
    }
}
