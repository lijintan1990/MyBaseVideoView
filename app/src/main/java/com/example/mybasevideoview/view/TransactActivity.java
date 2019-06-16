package com.example.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.mybasevideoview.MainActivity;
import com.example.mybasevideoview.R;
import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransactActivity extends Activity {

    @BindViews({R.id.iknwn_btn, R.id.back_btn})
    public List<Button>buttonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_transact);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        ButterKnife.bind(this);
        setResult(1);
    }

    @OnClick({R.id.back_btn, R.id.iknwn_btn})
    public void onViewClicked(View view) {
        TextView textView;
    }

    @OnClick(R.id.back_btn)
    public void goBackToHomePage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.iknwn_btn)
    public void exitTransactAty() {
        setResult(1);
        super.onBackPressed();
    }
}