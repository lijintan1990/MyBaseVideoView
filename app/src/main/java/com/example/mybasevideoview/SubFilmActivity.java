package com.example.mybasevideoview;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;

public class SubFilmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_film);
        XslUtils.hideStausbar(new WeakReference<Activity>(this), true);
    }
}
