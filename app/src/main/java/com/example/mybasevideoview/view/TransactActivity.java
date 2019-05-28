package com.example.mybasevideoview.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.example.mybasevideoview.R;
import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;

public class TransactActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_transact);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
    }
}
