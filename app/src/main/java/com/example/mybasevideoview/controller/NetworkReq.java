package com.example.mybasevideoview.controller;

import android.util.Log;

import com.example.mybasevideoview.model.ChapterListInfo;
import com.example.mybasevideoview.model.ObtainNetWorkData;
import com.example.mybasevideoview.model.TimeLineInfo;
import com.example.mybasevideoview.model.VideoListInfo;
import com.example.mybasevideoview.model.WordMsgs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.mybasevideoview.MainPlayerActivity.TAG;

public class NetworkReq {
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

    public void getWordMsgs(int index) {
        ObtainNetWorkData.getWordListData(new Callback<WordMsgs>() {
            @Override
            public void onResponse(Call<WordMsgs> call, Response<WordMsgs> response) {
                wordMsgs = response.body();
                Log.d(TAG, "get word list ok. info:" + response.toString());
            }

            @Override
            public void onFailure(Call<WordMsgs> call, Throwable t) {
                Log.w(TAG, "get word list failed, "+t.toString());
            }
        }, index);
    }

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