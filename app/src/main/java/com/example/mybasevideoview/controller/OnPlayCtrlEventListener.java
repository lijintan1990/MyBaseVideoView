package com.example.mybasevideoview.controller;

public interface OnPlayCtrlEventListener {
    int SET_DATA_SOURCE_CTRL = 0;
    int PLAY_CTRL = 1;
    int STOP_CTRL = 2;
    int PAUSE_CTRL = 3;
    int RESUME_CTRL = 4;

    //在中间显示的标识号
    int CENTER_NONE = 0;
    int CENTER_FULL = 1;
    int CENTER_LEFT = 2;
    int CENTER_RIGHT = 3;
    int CENTER_TOP = 4;
    int CENTER_BOTTOM = 5;

    void onPlayCtrlCallback(int action, int playDataIndex, int videoViewIndex, int centerType);
}
