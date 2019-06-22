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
    int getCurrentPosition();
    int getDuration();
    int getAudioSessionId();
    int getBufferPercentage();
    int getPlayerState();

    void startPlay();
    void pause();
    void resume();
    void seekTo(int msc);
    void stop();
    void stopPlayback();
}
