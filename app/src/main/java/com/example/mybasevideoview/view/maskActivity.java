package com.example.mybasevideoview.view;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.example.mybasevideoview.R;
import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class maskActivity extends Activity {

    @BindView(R.id.mask_view)
    RelativeLayout layoutView;

    int pagerTag = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mask);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.next)
    void next(View view) {
        pagerTag++;
        if (pagerTag == 2) {
            layoutView.setBackgroundResource(R.mipmap.mask_2);
            view.setBackgroundColor(Color.TRANSPARENT);
        } else if (pagerTag == 3) {
            layoutView.setBackgroundResource(R.mipmap.mask_3);
            view.setBackgroundColor(Color.TRANSPARENT);

        } else {
            finish();
        }
    }
}
