package com.example.mybasevideoview.controller;

public interface OnBtnStateListener {
    //主面板右侧按钮控制是否激活，可点击
    int XSL_APPLIANCE_BTN_STATE = 0xfff0;
    int XSL_WORD_BTN_STATE = 0xfff2;
    int XSL_ACTION_BTN_STATE = 0xfff4;
    int XSL_CHAPTER_BTN_STATE = 0xfff6;
    //主面板右侧按钮状态更改
    void onStateChange(int action, boolean enable, String url);
    void onWordStateChange(int action, boolean enable, String name, String imageUri, String content);
}