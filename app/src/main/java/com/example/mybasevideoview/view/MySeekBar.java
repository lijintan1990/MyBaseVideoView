package com.example.mybasevideoview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;

import com.example.mybasevideoview.model.ChapterListInfo;

public class MySeekBar extends AppCompatSeekBar {
    private Paint whitePaint;
    //单位是秒
    private int mDuration;
    ChapterListInfo chapterListInfo = null;

    public MySeekBar(Context context) {
        super(context);
        initPaint();
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public void setChapterListInfo(ChapterListInfo info, int totalDuration) {
        chapterListInfo = info;
        mDuration = totalDuration / 1000;
    }

    void initPaint() {
        if (whitePaint == null)
        {
            whitePaint = new Paint();
            whitePaint.setStrokeWidth(20);
            whitePaint.setColor(Color.WHITE);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDuration <= 0) {
            return;
        }
//        int w = getWidth();
//        int h = getHeight();
        //Log.d("MyseekBar", "w:"+w+"  h:" + h);
        int i = 0;
        for (ChapterListInfo.DataBean data : chapterListInfo.getData()) {
            if (data.getStartTime() > mDuration)
                break;

            // 1000会不准，1200却是准的，懒得查了
            int posX = data.getStartTime() * 1200 / mDuration;
            //seekBar左边总是会有预留空间，容错
            if (posX < 50)
                posX = 50;

            canvas.drawCircle(posX, 12, 8, whitePaint);
        }
    }
}
