package com.example.mybasevideoview.test;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

public interface GnakApi {
    @GET("api/data/Android/10/1")
    Call<ResponseBody> getAndroidInfo();
}
