package com.example.mybasevideoview.model;

import java.util.List;

public class VideoListInfo {

    /**
     * status : 0
     * msg : string
     * data : [{"id":0,"index":0,"thumbnailUrl":"string","name":"string","videoUrl":"string","duration":0,"relevanceType":0}]
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
         * index : 0
         * thumbnailUrl : string
         * name : string
         * videoUrl : string
         * duration : 0
         * relevanceType : 0
         */

        private int id;
        private int index;
        private String thumbnailUrl;
        private String name;
        private String videoUrl;
        private int duration;
        private int relevanceType;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getRelevanceType() {
            return relevanceType;
        }

        public void setRelevanceType(int relevanceType) {
            this.relevanceType = relevanceType;
        }
    }
}
