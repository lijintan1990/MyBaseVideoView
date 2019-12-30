package com.example.mybasevideoview.controller;

import com.example.mybasevideoview.model.TimeLineInfo;

public interface OnPlayCtrlEventListener {
    int SET_DATA_SOURCE_CTRL = 0;
    //中间窗口开始播放
    int PLAY_CTRL = 1;
    //中间窗口结束播放
    int STOP_CTRL = 2;
    int PAUSE_CTRL = 3;
    int RESUME_CTRL = 4;
    int SEEK_CTRL = 5;
    int PLAY_TIMELINE_CHANGE = 6;
    int PLAY_CHAPTER_CHANGE = 7;

    int PLAY_TIME_SET_CTRL = 13;
    int PLAY_ALL_CTRL = 14;


    //字幕更新
    int SUBTITLE_UPDATE = 100;

    //关联类型
    int RELATIVE_NONE = 0;
    int RELATIVE_HORIZON = 2;
    int RELATIVE_VERTICAL = 1;

    void onPlayCtrlCallback(int action, int startTime, int id);
    void onPlayRelateVideos(int action, int id1, int id2, int startTime, int duration);
    void onPlayTimeCallback(int action, int duration, int curTime);

    /**
     * 关闭关联视频UI
     * @param action
     * @param id1
     * @param id2
     */
    void onRelateUIClose(int action, int id1,int id2);

    void onSubtitleUpdate(int pts);
}

