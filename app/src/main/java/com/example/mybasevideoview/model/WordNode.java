package com.example.mybasevideoview.model;

import android.graphics.Bitmap;

public class WordNode {
    private String text;
    private String url;
    private Bitmap bitmap;

    public WordNode(String txt, String url, Bitmap bmp) {
        text = txt;
        this.url = url;
        bitmap = bmp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}