/*
 * Copyright 2017 jiajunhui<junhui_jia@163.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.kk.taurus.playerbase.player;

import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;

/**
 * Created by Taurus on 2018/3/17.
 */

public interface IPlayer {

    public static int STATE_END = -2;
    public static int STATE_ERROR = -1;
    public static int STATE_IDLE = 0;
    public static int STATE_INITIALIZED = 1;
    public static int STATE_PREPARED = 2;
    public static int STATE_STARTED = 3;
    public static int STATE_PAUSED = 4;
    public static int STATE_STOPPED = 5;
    public static int STATE_PLAYBACK_COMPLETE = 6;

    /**
     * with this method, you can send some params for player init or switch some setting.
     * such as some configuration option (use mediacodec or timeout or reconnect and so on) for decoder init.
     * @param code the code value custom yourself.
     * @param bundle deliver some data if you need.
     */
    void option(int code, Bundle bundle);

    void setDataSource(DataSource dataSource);
    void setDisplay(SurfaceHolder surfaceHolder);
    void setSurface(Surface surface);
    void setVolume(float left, float right);
    void setSpeed(float speed);

    void setOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener);
    void setOnErrorEventListener(OnErrorEventListener onErrorEventListener);

    void setOnBufferingListener(OnBufferingListener onBufferingListener);

    int getBufferPercentage();

    boolean isPlaying();
    int getCurrentPosition();
    int getDuration();
    int getAudioSessionId();
    int getVideoWidth();
    int getVideoHeight();
    int getState();

    void start();
    void start(int msc);
    void pause();
    void resume();
    void seekTo(int msc);
    void stop();
    void reset();
    void destroy();

}
