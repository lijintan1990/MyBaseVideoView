package com.example.mybasevideoview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.mybasevideoview.model.HomePageInfo;
import com.example.mybasevideoview.test.GnakApi;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ButterTestActivity extends AppCompatActivity {
    public static final String TAG = "Butter Knife";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_butter_test);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.test)
    public void onClickTestButton(View view) {
        Log.d(TAG, "click test button");
        testRetrofit1();
        //testRetrofit();
    }

    @OnClick(R.id.test)
    public void sayHi(Button button) {
        button.setText("sayHi");
    }

    void testRetrofit() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://gank.io/").build();
        GnakApi api = retrofit.create(GnakApi.class);
        Call<ResponseBody> call = api.getAndroidInfo();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String result = null;
                try {
                    result = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG,  result);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    void testRetrofit1() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.hongmingyuan.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GnakApi api = retrofit.create(GnakApi.class);
        Call<HomePageInfo> call = api.getIndexData();
        call.enqueue(new Callback<HomePageInfo>() {
            @Override
            public void onResponse(Call<HomePageInfo> call, Response<HomePageInfo> response) {
                HomePageInfo homePageData = response.body();
            }

            @Override
            public void onFailure(Call<HomePageInfo> call, Throwable t) {

            }
        });
    }
}
