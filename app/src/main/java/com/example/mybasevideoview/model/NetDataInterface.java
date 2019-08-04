package com.example.mybasevideoview.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NetDataInterface {
    /**
     * 首页数据接口
     * @return
     */
    @GET("api/index/getIndexData")
    Call<HomePageInfo> getIndexData();

    @GET("api/home/getChapterList")
    Call<ChapterListInfo> getChapterList();

    @GET("api/home/getTextItemList")
    Call<WordMsgs> getWordMsgs(@Query("textId") int textId);

    @GET("api/home/timeline")
    Call<TimeLineInfo> getTimelineInfo();

    @GET("api/home/getVideoList")
    Call<VideoListInfo> getVideoListInfo();
}
