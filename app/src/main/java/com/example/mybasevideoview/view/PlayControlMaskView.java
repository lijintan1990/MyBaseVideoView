package com.example.mybasevideoview.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.mybasevideoview.MainPlayerActivity;

import static com.example.mybasevideoview.MainPlayerActivity.TAG;

public class PlayControlMaskView extends AppCompatTextView {
    public PlayControlMaskView(Context context) {
        super(context);
    }

    public PlayControlMaskView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayControlMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private float lastDownPos;
    private float downPos;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDownPos = event.getX();
                downPos = lastDownPos;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "touch move event X:" + event.getX());
                int jumpTime;
                //每次seek，需要等上一次seek完毕才能进行
                if (event.getX() - lastDownPos > 20) {
                    jumpTime = 10;
                } else if (event.getX() - lastDownPos < -20) {
                    jumpTime = -10;
                } else {
                    return false;
                }
                MainPlayerActivity activity = (MainPlayerActivity) getContext();
                activity.touchMoveSeek(jumpTime);
                lastDownPos = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - downPos) < 5) {
                    MainPlayerActivity activity1 = (MainPlayerActivity) getContext();
                    activity1.touchMaskView();
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
