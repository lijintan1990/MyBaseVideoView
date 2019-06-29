package com.example.mybasevideoview.controller;

import com.example.mybasevideoview.model.TimeLineInfo;

public interface OnPlayCtrlEventListener {
    int SET_DATA_SOURCE_CTRL = 0;
    int PLAY_CTRL = 1;
    int STOP_CTRL = 2;
    int PAUSE_CTRL = 3;
    int RESUME_CTRL = 4;
    int PLAY_RELATE_HORIZON_CTRL = 5;
    int PLAY_RELATE_VERTICAL_CTRL = 6;
    int PLAY_TIME_SET_CTRL = 7;
    int PLAY_ALL_CTRL = 8;

    //在中间显示的标识号
    int CENTER_NONE = 0;
    int CENTER_FULL = 1;
    int CENTER_LEFT = 2;
    int CENTER_RIGHT = 3;
    int CENTER_TOP = 4;
    int CENTER_BOTTOM = 5;

    //关联类型
    int RELATIVE_NONE = -1;
    int RELATIVE_HORIZON = 2;
    int RELATIVE_VERTICAL = 1;

    void onPlayCtrlCallback(int action, TimeLineInfo.DataBean dataBean, int videoViewIndex, int centerType);
    void onPlayRelateVideos(int action, int id1, int id2, String uri_1, String uri_2);
    void onPlayTimeCallback(int action, int duration, int curTime);
}
