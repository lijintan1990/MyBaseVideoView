package com.xsl.culture.mybasevideoview.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public class MySeekBarWrapper extends FrameLayout {
    public MySeekBarWrapper( Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        getChildSeekBar();
    }

    private MySeekBar getChildSeekBar() {
        final View child = (getChildCount() > 0) ? getChildAt(0) : null;
        return (child instanceof MySeekBar) ? (MySeekBar) child : null;
    }
}
