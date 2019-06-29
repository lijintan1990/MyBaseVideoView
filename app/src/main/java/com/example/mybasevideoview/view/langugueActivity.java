package com.example.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.mybasevideoview.R;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class langugueActivity extends Activity {
    public static final int chinese = 1;
    public static final int cantonese = 2;
    public static final int english = 3;
    public static final String langugue_key = "langugue";
    private Intent intent = null;

    @BindViews({R.id.close_btn, R.id.chinese_btn, R.id.cantonese_btn, R.id.english_btn})
    List<Button> buttonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_langugue);
        ButterKnife.bind(this);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        intent = new Intent();
    }

    @OnClick({R.id.close_btn, R.id.chinese_btn, R.id.cantonese_btn, R.id.english_btn})
    void click(View view) {
        switch (view.getId()) {
            case R.id.close_btn:
                setResult(RequestCode.Languge_req, intent);
                finish();
                break;
            case R.id.chinese_btn:
                intent.putExtra(langugue_key, chinese);
                buttonList.get(1).setBackgroundResource(R.drawable.xsl_langugue_btn_color);
                buttonList.get(3).setBackgroundResource(R.color.translucent_background);
                buttonList.get(2).setBackgroundResource(R.color.translucent_background);
                break;
            case R.id.cantonese_btn:
                intent.putExtra(langugue_key, cantonese);
                buttonList.get(2).setBackgroundResource(R.drawable.xsl_langugue_btn_color);
                buttonList.get(3).setBackgroundResource(R.color.translucent_background);
                buttonList.get(1).setBackgroundResource(R.color.translucent_background);
                break;
            case R.id.english_btn:
                intent.putExtra(langugue_key, english);

                buttonList.get(3).setBackgroundResource(R.drawable.xsl_langugue_btn_color);
                buttonList.get(2).setBackgroundResource(R.color.translucent_background);
                buttonList.get(1).setBackgroundResource(R.color.translucent_background);
                break;
        }
    }
}
