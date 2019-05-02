package com.example.mybasevideoview.model;

public class PlayData {
    private long startTime;
    private long endTime;
    private long duration;
    private int cameraId;
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
}
