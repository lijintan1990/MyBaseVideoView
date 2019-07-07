package com.example.mybasevideoview.controller;

public interface OnBtnStateListener {
    //主面板右侧按钮控制是否激活，可点击
    int XSL_APPLIANCE_ENABLE = 0xfff0;
    int XSL_APPLIANCE_DISABLE = 0xfff1;
    int XSL_TEXT_ENABLE = 0xfff2;
    int XSL_TEXT_DISABLE = 0xfff3;
    int XSL_ACTION_ENABLE = 0xfff4;
    int XSL_ACTION_DISABLE = 0xfff5;
    //主面板右侧按钮状态更改
    void onStateChange(int action, String url);
}
