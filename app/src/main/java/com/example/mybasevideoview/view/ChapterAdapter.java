package com.example.mybasevideoview.view;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mybasevideoview.R;

import java.util.ArrayList;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterVideoHolder> {
    private int clickPos;
    private ArrayList<String> mDatas = null;
    @NonNull
    @Override
    public ChapterVideoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycle_view_item, viewGroup, false);
        return new ChapterVideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterVideoHolder chapterVideoHolder, int i) {
        chapterVideoHolder.textView.setText(mDatas.get(i));
        if (i == clickPos) {
            chapterVideoHolder.textView.setBackgroundResource(R.drawable.xsl_chapter_item_selected);
        } else {
            chapterVideoHolder.textView.setBackgroundResource(R.color.translucent_background);
        }

        chapterVideoHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListenner != null) {
                    int pos = chapterVideoHolder.getLayoutPosition();
                    itemClickListenner.onItemClick(chapterVideoHolder.itemView, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) {
            mDatas = new ArrayList<String>();
            mDatas.add("第一章    大神出山");
            mDatas.add("第二章    劈山杀妖");
            mDatas.add("第三章    山中遇宝");
            mDatas.add("第四章    宝剑复活");
        }

        return mDatas.size();
    }

    public static class ChapterVideoHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ChapterVideoHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.chapter_item);
        }
    }

    //点击某一条时候的回调
    public interface OnItemClickListenner {
        void onItemClick(View view, int position);
    }

    private OnItemClickListenner itemClickListenner;

    //设置监听器
    public  void setOnItemClickListenner(ChapterAdapter.OnItemClickListenner listenner) {
        itemClickListenner = listenner;
    }

    public void setThisPostion(int pos) {
        clickPos = pos;
    }
}
