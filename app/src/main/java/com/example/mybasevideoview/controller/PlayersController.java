package com.example.mybasevideoview.controller;

import com.example.mybasevideoview.model.PlayData;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * p3是优先播放的，如果p3没有，就p4 p5 ....p16这样子播放
 */

public class PlayersController extends Thread{

    private WeakReference<List<PlayData>> listWeakReference;

    PlayersController(List<PlayData> lst) {
        listWeakReference = new WeakReference<List<PlayData>>(lst);
    }

    @Override
    public void run() {

    }
}
