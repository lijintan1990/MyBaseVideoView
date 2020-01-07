package com.example.mybasevideoview.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.mybasevideoview.R;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends Activity {
    //    @BindView(R.id.close_btn)
//    Button closeBtn;
    @BindView(R.id.text_content)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        ButterKnife.bind(this);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @OnClick(R.id.close_btn)
    void closeClick() {
        setResult(RequestCode.About_req);
        finish();
    }
}
