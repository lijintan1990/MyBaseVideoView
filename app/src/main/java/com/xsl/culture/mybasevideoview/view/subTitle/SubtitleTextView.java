package com.xsl.culture.mybasevideoview.view.subTitle;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @Auther jixiongxu
 * @date 2017/9/20
 * @descraption 显示字幕的字体样式
 */

public class SubtitleTextView extends android.support.v7.widget.AppCompatTextView implements View.OnTouchListener
{
    private Context context;

    private SubtitleClickListener listener;

    public SubtitleTextView(Context context)
    {
        this(context, null);
    }

    public SubtitleTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        // 默认白色字体
        setTextColor(Color.WHITE);
        setSingleLine(true);
        //setShadowLayer(3, 0, 0, Color.RED);
        this.setOnTouchListener(this);
    }

    public void setSubtitleOnTouchListener(SubtitleClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (listener != null)
                    listener.ClickDown();
                break;
            case MotionEvent.ACTION_UP:
                if (listener != null)
                    listener.ClickUp();
                break;
        }
        return true;
    }
}

/**
 * 对字幕进行监听的接口
 */
interface SubtitleClickListener
{
    /**
     * 按下
     */
    void ClickDown();

    /**
     * 取消
     */
    void ClickUp();
}
