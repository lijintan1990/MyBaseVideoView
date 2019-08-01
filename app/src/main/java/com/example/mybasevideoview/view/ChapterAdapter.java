package com.example.mybasevideoview.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mybasevideoview.MainPlayerActivity;
import com.example.mybasevideoview.R;
import com.example.mybasevideoview.model.ChapterListInfo;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterVideoHolder> {
    private int clickPos;
    private ArrayList<String> mDatas = null;

    ChapterAdapter(int curChapter) {
        clickPos = curChapter;
    }

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

            List<ChapterListInfo.DataBean> data  = MainPlayerActivity.chapterListInfo.getData();
            if (data != null) {
                int mid = (data.size() + 1) / 2;
                int right;
                int chapterNum;
                for (int i=0; i!=mid; i++) {
                    String str = "第";
                    chapterNum = i+1;
                    if(i < 10) {
                        str = str + "0" + chapterNum+ "章   ";
                    } else {
                        str = str + chapterNum + "章   ";
                    }

                    str = str + data.get(i).getName();
                    mDatas.add(str);

                    String strRight = "第";
                    right = mid + i;
                    if (right >= data.size())
                        break;
                    chapterNum = right + 1;
                    if (right < 10) {
                        strRight = strRight + "0" + chapterNum + "章   ";
                    } else {
                        strRight = strRight + chapterNum + "章   ";
                    }
                    strRight = strRight + data.get(right).getName();
                    mDatas.add(strRight);
                }
            }
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
