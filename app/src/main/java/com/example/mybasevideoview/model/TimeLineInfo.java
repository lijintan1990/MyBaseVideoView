package com.example.mybasevideoview.model;

import java.util.List;

public class TimeLineInfo {

    /**
     * status : 0
     * msg : string
     * data : [{"id":0,"type":0,"startTime":0,"scale":0,"video":{"id":0,"index":0,"thumbnailUrl":"string","name":"string","videoUrl":"string","duration":0,"relevanceType":0,"relevanceVideo":{"id":3,"index":8,"thumbnailUrl":"string","name":"string","videoUrl":"string","duration":113,"relevanceType":1,"relevanceVideo":null}},"chapter":{"id":0,"code":"string","name":"string","type":0,"startTime":0,"scale":0},"text":{"id":0,"name":"string","content":"string"}}]
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
         * type : 0
         * startTime : 0
         * scale : 0
         * video : {"id":0,"index":0,"thumbnailUrl":"string","name":"string","videoUrl":"string","duration":0,"relevanceType":0,"relevanceVideo":{"id":3,"index":8,"thumbnailUrl":"string","name":"string","videoUrl":"string","duration":113,"relevanceType":1,"relevanceVideo":null}}
         * chapter : {"id":0,"code":"string","name":"string","type":0,"startTime":0,"scale":0}
         * text : {"id":0,"name":"string","content":"string"}
         */

        private int id;
        private int type;
        private int startTime;
        private int scale;
        private VideoBean video;
        private ChapterBean chapter;
        private TextBean text;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public int getScale() {
            return scale;
        }

        public void setScale(int scale) {
            this.scale = scale;
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
             * id : 0
             * index : 0
             * thumbnailUrl : string
             * name : string
             * videoUrl : string
             * duration : 0
             * relevanceType : 0
             * relevanceVideo : {"id":3,"index":8,"thumbnailUrl":"string","name":"string","videoUrl":"string","duration":113,"relevanceType":1,"relevanceVideo":null}
             */

            private int id;
            private int index;
            private String thumbnailUrl;
            private String name;
            private String videoUrl;
            private int duration;
            private int relevanceType;
            private RelevanceVideoBean relevanceVideo;

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

            public RelevanceVideoBean getRelevanceVideo() {
                return relevanceVideo;
            }

            public void setRelevanceVideo(RelevanceVideoBean relevanceVideo) {
                this.relevanceVideo = relevanceVideo;
            }

            public static class RelevanceVideoBean {
                /**
                 * id : 3
                 * index : 8
                 * thumbnailUrl : string
                 * name : string
                 * videoUrl : string
                 * duration : 113
                 * relevanceType : 1
                 * relevanceVideo : null
                 */

                private int id;
                private int index;
                private String thumbnailUrl;
                private String name;
                private String videoUrl;
                private int duration;
                private int relevanceType;
                private Object relevanceVideo;

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

                public Object getRelevanceVideo() {
                    return relevanceVideo;
                }

                public void setRelevanceVideo(Object relevanceVideo) {
                    this.relevanceVideo = relevanceVideo;
                }
            }
        }

        public static class ChapterBean {
            /**
             * id : 0
             * code : string
             * name : string
             * type : 0
             * startTime : 0
             * scale : 0
             */

            private int id;
            private String code;
            private String name;
            private int type;
            private int startTime;
            private int scale;

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

            public int getScale() {
                return scale;
            }

            public void setScale(int scale) {
                this.scale = scale;
            }
        }

        public static class TextBean {
            /**
             * id : 0
             * name : string
             * content : string
             */

            private int id;
            private String name;
            private String content;

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

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
