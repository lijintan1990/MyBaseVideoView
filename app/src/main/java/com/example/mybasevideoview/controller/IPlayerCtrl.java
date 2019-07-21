package com.example.mybasevideoview.controller;

import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.render.AspectRatio;
import com.kk.taurus.playerbase.render.IRender;

public interface IPlayerCtrl {
    void setDataSource(DataSource dataSource);

    void setRenderType(int renderType);
    void setAspectRatio(AspectRatio aspectRatio);
    boolean switchDecoder(int decoderPlanId);

    void setVolume(float left, float right);
    void setSpeed(float speed);

    boolean isInPlaybackState();
    boolean isPlaying();
    //单位毫秒
    int getCurrentPosition();
    //单位毫秒
    int getDuration();
    int getAudioSessionId();
    int getBufferPercentage();
    int getPlayerState();

    void start_();
    void pause_();
    void resume_();
    void seekTo_(int msc);
    // 通知controller进行seek, controller在通知
    // mainPlayerActivity去调用seekTo_进行seek
    void seekNotify(int msc);
    void stop_();
    void stopPlayback_();
}
