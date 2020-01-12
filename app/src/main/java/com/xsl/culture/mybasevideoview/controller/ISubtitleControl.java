package com.xsl.culture.mybasevideoview.controller;

import com.xsl.culture.mybasevideoview.model.SubtitlesModel;

import java.util.ArrayList;

/**
 * @Auther jixiongxu
 * @dat 2017/9/21
 * @descraption 字幕控制接口
 */

public interface ISubtitleControl
{
    /**
     * 设置中文字幕
     *
     * @param item
     */
    void setItemSubtitle(String item);

    /**
     * 定位设置字幕
     *
     * @param position
     */
    void seekTo(int position);

    void hide();

    /**
     * 设置数据
     *
     * @param list
     */
    void setData(ArrayList<SubtitlesModel> list, int langugueType);

    /**
     * 设置显示的语言
     *
     * @param type
     */
    void setLanguage(int type);

    /**
     * 暂停
     *
     * @param pause
     */
    void setPause(boolean pause);

    /**
     * 开始
     *
     * @param start
     */
    void setStart(boolean start);

    /**
     * 停止
     *
     * @param stop
     */
    void setStop(boolean stop);

    /**
     * 后台播放
     *
     * @param pb
     */
    void setPlayOnBackground(boolean pb);
}
