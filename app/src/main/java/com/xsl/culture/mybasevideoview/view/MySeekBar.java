package com.xsl.culture.mybasevideoview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.xsl.culture.mybasevideoview.model.ChapterListInfo;
import com.xsl.culture.mybasevideoview.utils.DisplayUtils;

import static com.xsl.culture.mybasevideoview.MainPlayerActivity.TAG;

public class MySeekBar extends AppCompatSeekBar {
    private Paint whitePaint;
    //单位是秒
    private int mDuration;
    ChapterListInfo chapterListInfo = null;
    int[] chapterPoint;

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
        int w = getWidth();
        for (ChapterListInfo.DataBean data : chapterListInfo.getData()) {
            if (data.getStartTime() > mDuration)
                break;

            int posX = data.getStartTime() * w / mDuration;
            //seekBar左边总是会有预留空间，容错
            if (posX < 10)
                posX = 10;
            //Log.d(TAG,"小白点位置:" + posX);
            int h = DisplayUtils.dp2px(getContext(), 4);
            int height = getHeight();
            Log.d(TAG, "xxx h:" + h + " height:"+ height);
            canvas.drawCircle(posX, h / 2, 6, whitePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            int x = (int) event.getX();
            for (ChapterListInfo.DataBean data : chapterListInfo.getData()) {
                if (data.getStartTime() > mDuration)
                    break;

                int posX = data.getStartTime() * getWidth() / mDuration;
                //seekBar左边总是会有预留空间，容错
                if (posX < 10)
                    posX = 10;

                if (Math.abs(x - posX) < 10) {
                    Log.d(TAG, "touch  eventX: "+ event.getX() + " chanslate posX:" + posX + " time: " + data.getStartTime() + " name" + data.getName());
                    if (seekChapterListener != null) {
                        seekChapterListener.seekToChapter(data.getStartTime(), data.getCode(), data.getName());
                    }
                    return super.onTouchEvent(event);
                }
            }
        }

        return true;
    }

    private SeekChapterListener seekChapterListener;

    public void setSeekChapterListener(SeekChapterListener seekChapterListener) {
        this.seekChapterListener = seekChapterListener;
    }

    public interface SeekChapterListener {
        void seekToChapter(int time, String chapterId, String chapterTitle);
    }
}
