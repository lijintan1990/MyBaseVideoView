package com.example.mybasevideoview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;

public class MySeekBar extends AppCompatSeekBar {
    private Paint whitePaint;

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
        int w = getWidth();
        int h = getHeight();
        Log.d("MyseekBar", "w:"+w+"  h:" + h);
        canvas.drawCircle(100, 36, 20, whitePaint);
        canvas.drawCircle(200, 36, 25, whitePaint);
        canvas.drawCircle(300, 36, 15, whitePaint);
        canvas.drawCircle(400, 36, 18, whitePaint);
    }
}
