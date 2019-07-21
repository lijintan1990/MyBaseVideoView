package com.example.mybasevideoview.view.dummy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;

public class RelateButton extends android.support.v7.widget.AppCompatImageButton {
    private WeakReference<BaseVideoView> mFatherView = null;
    private int mResId = -1; //设置的mimap的id

    public RelateButton(Context context) {
        super(context);
    }

    public RelateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelateButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WeakReference<BaseVideoView> getmFatherView() {
        return mFatherView;
    }

    public void setmFatherView(WeakReference<BaseVideoView> mFatherView) {
        this.mFatherView = mFatherView;
    }

    public int getmResId() {
        return mResId;
    }

    public void setmResId(int mResId) {
        this.mResId = mResId;
    }
}
