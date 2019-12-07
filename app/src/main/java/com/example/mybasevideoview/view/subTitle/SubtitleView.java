package com.example.mybasevideoview.view.subTitle;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextClock;

import com.example.mybasevideoview.R;
import com.example.mybasevideoview.controller.ISubtitleControl;
import com.example.mybasevideoview.model.SubtitlesModel;
import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * @date 2017/9/20
 * @Auther jixiongxu
 * @descraptio 显示字幕的图层
 */
public class SubtitleView extends LinearLayout implements ISubtitleControl, SubtitleClickListener
{
    /**
     * 显示中文
     */
    public final static int LANGUAGE_TYPE_CHINA = 0;

    /**
     * 粤语显示
     */
    public final static int LANGUAGE_TYPE_CANTONESE = LANGUAGE_TYPE_CHINA + 1;

    /**
     * 显示英语
     */
    public final static int LANGUAGE_TYPE_ENGLISH = LANGUAGE_TYPE_CANTONESE + 1;

    /**
     * 当前使用的语言
     */
    private ArrayList<SubtitlesModel> dataCur = null;
    /**
     * 中文
     */
    private ArrayList<SubtitlesModel> dataCN = new ArrayList<SubtitlesModel>();
    //粤语
    private ArrayList<SubtitlesModel> dataCA = new ArrayList<SubtitlesModel>();
    //英语
    private ArrayList<SubtitlesModel> dataEN = new ArrayList<SubtitlesModel>();

    /**
     * 字幕显示控件
     */
    private SubtitleTextView textView;

    /**
     * 当前显示节点
     */
    private View subTitleView;

    /**
     * 单条字幕数据
     */
    private SubtitlesModel model = null;

    /**
     * 后台播放
     */
    private boolean palyOnBackground = false;

    private int language = LANGUAGE_TYPE_CHINA;

    private Context context;
    private Handler UIHandler;

    public SubtitleView(Context context)
    {
        this(context, null);
    }

    public SubtitleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs);
    }

    public SubtitleView(final Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        subTitleView = View.inflate(context, R.layout.layout_subtitle, null);
        textView = subTitleView.findViewById(R.id.subTitle);
        textView.setSubtitleOnTouchListener(this);
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.BOTTOM);
        this.addView(subTitleView);
    }

    @Override
    public void setItemSubtitle(String item)
    {
        //Log.d("subtitle", "setText:" + item);
        textView.setText(item);
    }

    @Override
    synchronized public void seekTo(int position)
    {
        if (dataCur != null && !dataCur.isEmpty()) {
            model = searchSub(dataCur, position);
            //Log.d("Subtitle", "" + position + "/" + dataCur.get(dataCur.size() - 1).end);
        }

        //Log.d("subTitle", "subtitle pts:" + position);
        if (model != null) {
            setItemSubtitle(model.contextC);
        } else {
            setItemSubtitle("");
        }
    }

    @Override
    public void setData(ArrayList<SubtitlesModel> list, int languageType)
    {
        if (list == null || list.size() <= 0) {
            Log.e("subtitle", "subtitle data is empty");
            return;
        }

        if (languageType == LANGUAGE_TYPE_CHINA) {
            dataCN = list;
        } else if (languageType == LANGUAGE_TYPE_CANTONESE) {
            dataCA = list;
        } else {
            dataEN = list;
        }
    }

    @Override
    synchronized public void setLanguage(int type)
    {
        textView.setVisibility(View.VISIBLE);
        if (type == LANGUAGE_TYPE_CHINA) {
            dataCur = dataCN;
            Log.d("Subtitle", "change langugue to chinese dataCn size"+dataCN.size() + "CA size:"+dataCA.size());
        } else if (type == LANGUAGE_TYPE_ENGLISH) {
            dataCur = dataEN;
            Log.d("Subtitle", "change langugue to english dataEn size"+dataEN.size());
        } else {
            Log.d("Subtitle", "change langugue to cantons dataCa size"+dataCA.size() + " CN size:"+dataCN.size());
            dataCur = dataCA;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.d("subtitle", "onWindowFocusChanged:" + hasWindowFocus);
    }

    @Override
    public void setPause(boolean pause)
    {

    }

    @Override
    public void setStart(boolean start)
    {

    }

    @Override
    public void setStop(boolean stop)
    {

    }

    @Override
    public void setPlayOnBackground(boolean pb)
    {
        this.palyOnBackground = pb;
    }

    /**
     * 采用二分法去查找当前应该播放的字幕
     *
     * @param list 全部字幕
     * @param key 播放的时间点
     * @return
     */
    public static SubtitlesModel searchSub(ArrayList<SubtitlesModel> list, int key)
    {
        int start = 0;
        int end = list.size() - 1;
        while (start <= end)
        {
            int middle = (start + end) / 2;
            if (key < list.get(middle).star)
            {
                if (key > list.get(middle).end)
                {
                    return list.get(middle);
                }
                end = middle - 1;
            }
            else if (key > list.get(middle).end)
            {
                if (key < list.get(middle).star)
                {
                    return list.get(middle);
                }
                start = middle + 1;
            }
            else if (key >= list.get(middle).star && key <= list.get(middle).end)
            {
                return list.get(middle);
            }
        }
        return null;
    }

    @Override
    public void ClickDown()
    {
        language++;
        setLanguage(language % 3);
    }

    @Override
    public void ClickUp()
    {
    }
}
