package com.xsl.culture.mybasevideoview.model;

import com.kk.taurus.playerbase.widget.BaseVideoView;

public class PlayData {
    private boolean isPlaying;
    private long startTime;
    private long endTime = 0;
    private long duration = 0;
    private int cameraId;
    //在列表中的位置
    private int index;
    private String uri = null;
    private BaseVideoView videoView = null;
    // 0在边缘播放， 1在中间大屏幕播放，2中间播放
    private int playType;
    // 关联节点
    private PlayData relatePlayData;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public PlayData getRelatePlayData() {
        return relatePlayData;
    }

    public void setRelatePlayData(PlayData relatePlayData) {
        this.relatePlayData = relatePlayData;
    }

    public BaseVideoView getVideoView() {
        return videoView;
    }

    public void setVideoView(BaseVideoView videoView) {
        this.videoView = videoView;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
