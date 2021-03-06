package com.xsl.culture.mybasevideoview.controller;

public interface OnBtnStateListener {
    //主面板右侧按钮控制是否激活，可点击
    int XSL_APPLIANCE_BTN_STATE = 0xfff0;
    int XSL_WORD_BTN_STATE = 0xfff2;
    int XSL_ACTION_BTN_STATE = 0xfff4;
    int XSL_CHAPTER_BTN_STATE = 0xfff6;

    //主面板右侧按钮状态更改
    void onStateChange(int action, boolean enable, String url, String name);
    void onWordStateChange(int action, boolean enable, int objId);
    void onChapterBtnTextUpdate(int chpaterStartTime, String text, String chapterName);
    void onApplience(int action, boolean enable, String url, String name);
}