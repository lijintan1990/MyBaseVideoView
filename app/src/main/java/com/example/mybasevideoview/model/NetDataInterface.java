package com.example.mybasevideoview.model;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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


    @FormUrlEncoded
    @POST("api/pay/appPay")
    Call<PayInfo>getPayInfo(@Field("androidId") String androidId, @Field("payType") int payType);

    @FormUrlEncoded
    @POST("api/pay/getPay")
    Call<PayResult>getPayResult(@Field("androidId") String androidId);
}
