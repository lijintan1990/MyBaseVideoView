package com.xsl.culture.mybasevideoview.model;

import java.util.List;

public class VideoListInfo {

    /**
     * status : 0
     * msg : string
     * data : [{"duration":0,"videoUrl1080":"string","videoUrl360":"string","videoUrl720":"string","videoUrl90":"string","id":0,"index":0,"thumbnailUrl":"string","name":"string"}]
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
         * duration : 0
         * videoUrl1080 : string
         * videoUrl360 : string
         * videoUrl720 : string
         * videoUrl90 : string
         * id : 0
         * index : 0
         * thumbnailUrl : string
         * name : string
         */

        private int duration;
        private String videoUrl1080;
        private String videoUrl360;
        private String videoUrl720;
        private String videoUrl90;
        private int id;
        private int index;
        private String thumbnailUrl;
        private String name;

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getVideoUrl1080() {
            return videoUrl1080;
        }

        public void setVideoUrl1080(String videoUrl1080) {
            this.videoUrl1080 = videoUrl1080;
        }

        public String getVideoUrl360() {
            return videoUrl360;
        }

        public void setVideoUrl360(String videoUrl360) {
            this.videoUrl360 = videoUrl360;
        }

        public String getVideoUrl720() {
            return videoUrl720;
        }

        public void setVideoUrl720(String videoUrl720) {
            this.videoUrl720 = videoUrl720;
        }

        public String getVideoUrl90() {
            return videoUrl90;
        }

        public void setVideoUrl90(String videoUrl90) {
            this.videoUrl90 = videoUrl90;
        }

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
    }
}
