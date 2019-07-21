package com.example.mybasevideoview.view.dummy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;

public class RelateButton extends android.support.v7.widget.AppCompatImageButton {
    public WeakReference<BaseVideoView> mFatherView;
    public int mResId; //设置的mimap的id

    public RelateButton(Context context, BaseVideoView videoView, int resId) {
        super(context);
        mFatherView = new WeakReference<>(videoView);
        mResId = resId;
    }

    public RelateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelateButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
