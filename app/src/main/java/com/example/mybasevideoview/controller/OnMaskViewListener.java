package com.example.mybasevideoview.controller;

public interface OnMaskViewListener {
    int ACTION_MASK_VISIABLE = 0x00ff00;
    int ACTION_MASK_GONE = 0x00ff01;
    /**
     * 设置遮罩
     * @param index 窗口id,0开始
     * @param id 在json中的id号
     */
    void setMaskViewStatus(int action, int index, int id);
}
