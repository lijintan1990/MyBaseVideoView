package com.example.mybasevideoview.controller;

import android.util.Log;

import com.example.mybasevideoview.model.ChapterListInfo;
import com.example.mybasevideoview.model.ObtainNetWorkData;
import com.example.mybasevideoview.model.PayInfo;
import com.example.mybasevideoview.model.PayResult;
import com.example.mybasevideoview.model.TimeLineInfo;
import com.example.mybasevideoview.model.VideoListInfo;
import com.example.mybasevideoview.model.WeinxinPayInfo;
import com.example.mybasevideoview.model.WordMsgs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.mybasevideoview.MainPlayerActivity.TAG;

public class NetworkReq {
    private PayInterface payInterface = null;

    private TimeLineInfo mTimelineInfo = null;
    private WordMsgs wordMsgs = null;
    private VideoListInfo mVideolst = null;
    private ChapterListInfo chapterListInfo = null;
    private static NetworkReq networkReq;


    public static NetworkReq getInstance() {
        if (networkReq ==  null) {
            networkReq = new NetworkReq();
        }
        return networkReq;
    }

    public void setPayInterface(PayInterface payInterface) {
        this.payInterface = payInterface;
    }

    public interface PayInterface {
        void zhifubaoInfo(PayInfo payInfo);
        void weixinInfo(WeinxinPayInfo payInfo);

        void payResult(boolean result);
    }

    public static final int weixinType = 1;
    public static final int zhifubaoType = 2;

    public void getPayInfo(String key, int payType) {
        ObtainNetWorkData.getPayInfo(new Callback<PayInfo>() {
            @Override
            public void onResponse(Call<PayInfo> call, Response<PayInfo> response) {
                PayInfo info = response.body();
                Log.d(TAG, "zhifubao: " + info.getData().getApp());
                if (payInterface != null) {
                        payInterface.zhifubaoInfo(info);
                }
            }

            @Override
            public void onFailure(Call<PayInfo> call, Throwable t) {
                Log.d(TAG, "zhifubao failed");
            }
        }, key, payType);
    }

    public void getWeixinPayInfo(String key, int payType) {
        ObtainNetWorkData.getWeixinPayInfo(new Callback<WeinxinPayInfo>() {
            @Override
            public void onResponse(Call<WeinxinPayInfo> call, Response<WeinxinPayInfo> response) {
                WeinxinPayInfo info = response.body();
                if (payInterface != null) {
                    payInterface.weixinInfo(info);
                }
            }

            @Override
            public void onFailure(Call<WeinxinPayInfo> call, Throwable t) {
                Log.d(TAG, "weixin pay failed");
            }
        }, key, payType);
    }

    public void getPayResult(String key) {
        ObtainNetWorkData.getPayResult(new Callback<PayResult>() {
            @Override
            public void onResponse(Call<PayResult> call, Response<PayResult> response) {
                PayResult info = response.body();
                if (payInterface != null) {
                    payInterface.payResult(info.isData());
                }
            }

            @Override
            public void onFailure(Call<PayResult> call, Throwable t) {
                Log.d(TAG, "zhifubao failed");
            }
        }, key);
    }

    public void getTimeLine() {
        ObtainNetWorkData.getTimelineData(new Callback<TimeLineInfo>() {
            @Override
            public void onResponse(Call<TimeLineInfo> call, Response<TimeLineInfo> response) {
                Log.d(TAG, "get homepage data success");
                mTimelineInfo = response.body();
                Log.d(TAG, "onResponse thread id:"+Thread.currentThread().getId());
                if (mTimelineInfo.getData() == null) {
                    Log.e(TAG, "Service Error. "+ mTimelineInfo.getMsg());
                    mTimelineInfo = null;
                }
//                if (playersController != null) {
//                    int duration = playersController.getDuration();
//                    if (duration != 0) {
//                        mySeekBar.setChapterListInfo(chapterListInfo, duration);
//                    }
//                }
            }

            @Override
            public void onFailure(Call<TimeLineInfo> call, Throwable t) {
                Log.w(TAG, "get timeline failed, "+t.toString());
            }
        });
    }
    public  TimeLineInfo getTimelineInfo() {
        return mTimelineInfo;
    }

//    public void getWordMsgs(int index) {
//        ObtainNetWorkData.getWordListData(new Callback<WordMsgs>() {
//            @Override
//            public void onResponse(Call<WordMsgs> call, Response<WordMsgs> response) {
//                wordMsgs = response.body();
//                Log.d(TAG, "get word list ok. info:" + response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<WordMsgs> call, Throwable t) {
//                Log.w(TAG, "get word list failed, "+t.toString());
//            }
//        }, index);
//    }

    public void getChapter() {
        ObtainNetWorkData.getChapterListData(new Callback<ChapterListInfo>() {
            @Override
            public void onResponse(Call<ChapterListInfo> call, Response<ChapterListInfo> response) {
                chapterListInfo = response.body();
//                if (playersController != null) {
//                    int duration = playersController.getDuration();
//                    if (duration != 0) {
//                        mySeekBar.setChapterListInfo(chapterListInfo, duration);
//                    }
//                }
            }

            @Override
            public void onFailure(Call<ChapterListInfo> call, Throwable t) {
                Log.w(TAG, "get chapter info failed, "+t.toString());
            }
        });
    }

    public ChapterListInfo getChapterListInfo() {
        return chapterListInfo;
    }

    public void getVideoList() {
        ObtainNetWorkData.getVideoListData(new Callback<VideoListInfo>() {
            @Override
            public void onResponse(Call<VideoListInfo> call, Response<VideoListInfo> response) {
                Log.d(TAG, "get homepage data success");
                mVideolst = response.body();
                Log.d(TAG, "onResponse thread id:"+Thread.currentThread().getId());
            }

            @Override
            public void onFailure(Call<VideoListInfo> call, Throwable t) {
                Log.w(TAG, "get timeline failed, "+t.toString());
            }
        });
    }
    public VideoListInfo getVideoLstInfo() {
        return mVideolst;
    }
}
