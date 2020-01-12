package com.xsl.culture.mybasevideoview.model;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ObtainNetWorkData {
    public static void getHomeData(Callback<HomePageInfo> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.hongmingyuan.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetDataInterface api = retrofit.create(NetDataInterface.class);
        Call<HomePageInfo> call = api.getIndexData();
        call.enqueue(callback);
//        call.enqueue(new Callback<HomePageData>() {
//            @Override
//            public void onResponse(Call<HomePageData> call, Response<HomePageData> response) {
//                HomePageData homePageData = response.body();
//            }
//
//            @Override
//            public void onFailure(Call<HomePageData> call, Throwable t) {
//
//            }
//        });
    }

    public static void getPayInfo(Callback<PayInfo> callback, String key, int payType) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.hongmingyuan.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetDataInterface api = retrofit.create(NetDataInterface.class);
        Call<PayInfo> call = api.getPayInfo(key, payType);
        call.enqueue(callback);
    }

    public static void getWeixinPayInfo(Callback<WeinxinPayInfo> callback, String key, int payType) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.hongmingyuan.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetDataInterface api = retrofit.create(NetDataInterface.class);
        Call<WeinxinPayInfo> call = api.getWeixinPayInfo(key, payType);
        call.enqueue(callback);
    }

    public static void getPayResult(Callback<PayResult> callback, String key) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.hongmingyuan.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetDataInterface api = retrofit.create(NetDataInterface.class);
        Call<PayResult> call = api.getPayResult(key);
        call.enqueue(callback);
    }

    public static void getTimelineData(Callback<TimeLineInfo> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.hongmingyuan.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetDataInterface api = retrofit.create(NetDataInterface.class);
        Call<TimeLineInfo> call = api.getTimelineInfo();
        call.enqueue(callback);
    }

    public static void getChapterListData(Callback<ChapterListInfo> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.hongmingyuan.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetDataInterface api = retrofit.create(NetDataInterface.class);
        Call<ChapterListInfo> call = api.getChapterList();
        call.enqueue(callback);
    }

    public static void getWordListData(Callback<WordMsgs> callback, int index, int langugueType) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.hongmingyuan.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetDataInterface api = retrofit.create(NetDataInterface.class);
        Call<WordMsgs> call = api.getWordMsgs(index, langugueType);
        call.enqueue(callback);
    }

    public static void getVideoListData(Callback<VideoListInfo> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.hongmingyuan.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetDataInterface api = retrofit.create(NetDataInterface.class);
        Call<VideoListInfo> call = api.getVideoListInfo();
        call.enqueue(callback);
    }

    public static Bitmap getBitmap(String path) throws IOException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200){
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }
}
