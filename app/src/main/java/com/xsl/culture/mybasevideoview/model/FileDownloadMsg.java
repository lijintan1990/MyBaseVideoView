package com.xsl.culture.mybasevideoview.model;

import android.content.Context;
import android.os.Environment;

import com.xsl.culture.mybasevideoview.controller.NetworkReq;
import com.xsl.culture.mybasevideoview.dialog.ConfirmDialog;

import java.util.ArrayList;

public class FileDownloadMsg {
    private ArrayList<String> urls;
    private ArrayList<String> names;
    private String downloadPath;
    private String aliasName = "资料库";

    public FileDownloadMsg(Context context) {
        urls = new ArrayList<>();
        names = new ArrayList<>();
        VideoListInfo listInfo = NetworkReq.getInstance().getVideoLstInfo();
        if (listInfo == null) {
            return;
        }
        //downloadPath = context.getExternalFilesDir("") + "/360";
        downloadPath = Environment.getExternalStorageDirectory().getPath() + "/360";
        int i = 0;
        for (VideoListInfo.DataBean dataBean : listInfo.getData()) {
            urls.add(dataBean.getVideoUrl360());
            names.add("" + (dataBean.getIndex() - 1) +".mp4");
            //测试
            if (++i == 2)
                break;
            //break;
        }
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public String getAliasName() {
        return aliasName;
    }

    public String getDownloadPath() {
        return downloadPath;
    }
}
