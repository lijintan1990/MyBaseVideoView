package com.example.mybasevideoview.model;

/**
 * 首页返回的数据
 * {
 *   "status": 0,
 *   "msg": "string",
 *   "data": {
 *     "index": {
 *       "id": 0,
 *       "code": "string",
 *       "thumbnailUrl": "string",
 *       "videoUrl": "string",
 *       "duration": 0
 *     },
 *     "excerpts": {
 *       "id": 0,
 *       "code": "string",
 *       "thumbnailUrl": "string",
 *       "videoUrl": "string",
 *       "duration": 0
 *     },
 *     "full": {
 *       "id": 0,
 *       "code": "string",
 *       "thumbnailUrl": "string",
 *       "videoUrl": "string",
 *       "duration": 0
 *     }
 *   }
 * }
 */
public class HomePageInfo {

    /**
     * status : 0
     * msg : string
     * data : {"index":{"id":0,"code":"string","thumbnailUrl":"string","videoUrl":"string","duration":0},"excerpts":{"id":0,"code":"string","thumbnailUrl":"string","videoUrl":"string","duration":0},"full":{"id":0,"code":"string","thumbnailUrl":"string","videoUrl":"string","duration":0}}
     */

    private int status;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * index : {"id":0,"code":"string","thumbnailUrl":"string","videoUrl":"string","duration":0}
         * excerpts : {"id":0,"code":"string","thumbnailUrl":"string","videoUrl":"string","duration":0}
         * full : {"id":0,"code":"string","thumbnailUrl":"string","videoUrl":"string","duration":0}
         */

        private IndexBean index;
        private ExcerptsBean excerpts;
        private FullBean full;

        public IndexBean getIndex() {
            return index;
        }

        public void setIndex(IndexBean index) {
            this.index = index;
        }

        public ExcerptsBean getExcerpts() {
            return excerpts;
        }

        public void setExcerpts(ExcerptsBean excerpts) {
            this.excerpts = excerpts;
        }

        public FullBean getFull() {
            return full;
        }

        public void setFull(FullBean full) {
            this.full = full;
        }

        public static class IndexBean {
            /**
             * id : 0
             * code : string
             * thumbnailUrl : string
             * videoUrl : string
             * duration : 0
             */

            private int id;
            private String code;
            private String thumbnailUrl;
            private String videoUrl;
            private int duration;

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

            public String getThumbnailUrl() {
                return thumbnailUrl;
            }

            public void setThumbnailUrl(String thumbnailUrl) {
                this.thumbnailUrl = thumbnailUrl;
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
        }

        public static class ExcerptsBean {
            /**
             * id : 0
             * code : string
             * thumbnailUrl : string
             * videoUrl : string
             * duration : 0
             */

            private int id;
            private String code;
            private String thumbnailUrl;
            private String videoUrl;
            private int duration;

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

            public String getThumbnailUrl() {
                return thumbnailUrl;
            }

            public void setThumbnailUrl(String thumbnailUrl) {
                this.thumbnailUrl = thumbnailUrl;
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
        }

        public static class FullBean {
            /**
             * id : 0
             * code : string
             * thumbnailUrl : string
             * videoUrl : string
             * duration : 0
             */

            private int id;
            private String code;
            private String thumbnailUrl;
            private String videoUrl;
            private int duration;

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

            public String getThumbnailUrl() {
                return thumbnailUrl;
            }

            public void setThumbnailUrl(String thumbnailUrl) {
                this.thumbnailUrl = thumbnailUrl;
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
        }
    }
}
