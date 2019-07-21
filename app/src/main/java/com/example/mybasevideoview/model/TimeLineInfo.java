package com.example.mybasevideoview.model;

import java.util.List;

public class TimeLineInfo {

    /**
     * status : 0
     * msg : success
     * data : [{"id":0,"objId":0,"type":0,"startTime":0,"duration":0,"scale":0,"relevanceType":0,"relevanceVideoId":11,"video":{"duration":0,"videoUrl1080":"string","videoUrl360":"string","videoUrl720":"string","videoUrl90":"string","id":0,"index":0,"thumbnailUrl":"string","name":"string"},"chapter":{"id":0,"code":"string","objId":0,"name":"string","type":0,"startTime":0,"duration":0,"scale":0,"relevanceType":0},"text":{"content":"string","imgUrl":"string","id":0,"name":"string"}}]
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
         * objId : 0
         * type : 0
         * startTime : 0
         * duration : 0
         * scale : 0
         * relevanceType : 0
         * relevanceVideoId : 11
         * video : {"duration":0,"videoUrl1080":"string","videoUrl360":"string","videoUrl720":"string","videoUrl90":"string","id":0,"index":0,"thumbnailUrl":"string","name":"string"}
         * chapter : {"id":0,"code":"string","objId":0,"name":"string","type":0,"startTime":0,"duration":0,"scale":0,"relevanceType":0}
         * text : {"content":"string","imgUrl":"string","id":0,"name":"string"}
         */

        private int id;
        private int objId;
        private int type;
        private int startTime;
        private int duration;
        private int scale;
        private int relevanceType;
        private int relevanceVideoId;
        private VideoBean video;
        private ChapterBean chapter;
        private TextBean text;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getObjId() {
            return objId;
        }

        public void setObjId(int objId) {
            this.objId = objId;
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

        public int getRelevanceVideoId() {
            return relevanceVideoId;
        }

        public void setRelevanceVideoId(int relevanceVideoId) {
            this.relevanceVideoId = relevanceVideoId;
        }

        public VideoBean getVideo() {
            return video;
        }

        public void setVideo(VideoBean video) {
            this.video = video;
        }

        public ChapterBean getChapter() {
            return chapter;
        }

        public void setChapter(ChapterBean chapter) {
            this.chapter = chapter;
        }

        public TextBean getText() {
            return text;
        }

        public void setText(TextBean text) {
            this.text = text;
        }

        public static class VideoBean {
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

        public static class ChapterBean {
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
        }

        public static class TextBean {
            /**
             * content : string
             * imgUrl : string
             * id : 0
             * name : string
             */

            private String content;
            private String imgUrl;
            private int id;
            private String name;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
