package com.xsl.culture.mybasevideoview.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @Auther jixiongxu
 * @date 2017/9/20.
 * @descraption 用于解析字幕
 */

public class SubtitlesDataCoding
{
    /**
     * 一秒=1000毫秒
     */
    private final static int oneSecond = 1000;

    private final static int oneMinute = 60 * oneSecond;

    private final static int oneHour = 60 * oneMinute;

    /**
     * 每一个数据节点
     */
    public ArrayList<SubtitlesModel> list = new ArrayList<SubtitlesModel>();

    /**
     * 正则表达式，判断是否是时间的格式
     */
    private final static String equalStringExpress = "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d --> \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";

    /**
     * 读取本地文件
     *
     * @param is
     */
    public ArrayList<SubtitlesModel> readFileStream(InputStream is)
    {
        String line;
        BufferedReader in = null;
        if (is == null) return null;

        try
        {
            in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        try
        {
            assert in != null;
            while ((line = in.readLine()) != null)
            {
                SubtitlesModel sm = new SubtitlesModel();
                // 匹配正则表达式，不符合提前结束当前行；
                if (Pattern.matches(equalStringExpress, line))
                {
                    // 填充开始时间数据
                    sm.star = getTime(line.substring(0, 12));
                    // 填充结束时间数据
                    sm.end = getTime(line.substring(17, 29));
                    // 填充中文数据
                    sm.contextC = in.readLine();
                    // 当前字幕的节点位置
                    sm.node = list.size() + 1;
                    list.add(sm);
                    Log.d("Subtitle", "add subtitle date star:"+sm.star + " end:" + sm.end + " context:" + sm.contextC + " node:" + sm.node);
                }
            }
            return getSubtitles();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (list != null)
        {
            Log.d("jixiongxu:", "open subtitle file ok");
            return getSubtitles();
        }
        return null;
    }

    /**
     * 读取本地文件
     *
     * @param path
     */
    public ArrayList<SubtitlesModel> readFile(String path)
    {
        String line;
        FileInputStream is;
        File subtitlesFile = new File(path);
        BufferedReader in = null;

        if (!subtitlesFile.exists() || !subtitlesFile.isFile())
        {
            Log.e("jixiongxu:", "open subtitle file fill");
            return null;
        }
        /**
         * 读取文件，转流，方便读取行
         */
        try
        {
            is = new FileInputStream(subtitlesFile);
            try
            {
                in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        try
        {
            assert in != null;
            while ((line = in.readLine()) != null)
            {
                SubtitlesModel sm = new SubtitlesModel();
                // 匹配正则表达式，不符合提前结束当前行；
                if (Pattern.matches(equalStringExpress, line))
                {
                    // 填充开始时间数据
                    sm.star = getTime(line.substring(0, 12));
                    // 填充结束时间数据
                    sm.end = getTime(line.substring(17, 29));
                    // 填充中文数据
                    sm.contextC = in.readLine();
                    // 当前字幕的节点位置
                    sm.node = list.size() + 1;
                    list.add(sm);
                }
            }
            return getSubtitles();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (list != null)
        {
            Log.d("jixiongxu:", "open subtitle file ok");
            return getSubtitles();
        }
        return null;
    }

    /**
     * @param line
     * @return 字幕所在的时间节点
     * @descraption 将String类型的时间转换成int的时间类型
     */
    private int getTime(String line)
    {
        try
        {
            return Integer.parseInt(line.substring(0, 2)) * oneHour// 时
                    + Integer.parseInt(line.substring(3, 5)) * oneMinute// 分
                    + Integer.parseInt(line.substring(6, 8)) * oneSecond// 秒
                    + Integer.parseInt(line.substring(9, line.length()));// 毫秒
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * @return list?null
     * @descraption 返回解析后的字幕数据
     */
    public ArrayList<SubtitlesModel> getSubtitles()
    {
        return list != null && list.size() > 0 ? list : null;
    }
}
