package com.example.mybasevideoview.controller;

import java.util.ArrayList;

public interface OnMaskViewListener {
    int ACTION_PLAY_MASK = 0x00ff00;
    /**
     * 设置遮罩
     */
    void setMaskViewStatus(int action, ArrayList<Integer> idLst);
}
