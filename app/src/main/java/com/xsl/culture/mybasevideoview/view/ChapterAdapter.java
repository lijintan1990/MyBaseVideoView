package com.xsl.culture.mybasevideoview.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xsl.culture.R;
import com.xsl.culture.mybasevideoview.MainActivity;
import com.xsl.culture.mybasevideoview.controller.NetworkReq;
import com.xsl.culture.mybasevideoview.model.ChapterListInfo;

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
        //修改子项的显示背景
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

    public String getIndex(int chapter) {
        String title = "";

        switch (chapter) {
            case 1:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "一";
                } else {
                    title += "01";
                }
                break;
            case 2:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "二";
                } else {
                    title += "02";
                }
                break;
            case 3:
                if (MainActivity.curLangugue != langugueActivity.english) {

                    title += "三";
                } else {
                    title += "03";
                }
                break;
            case 4:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "四";
                } else {
                    title += "04";
                }
                break;
            case 5:
                if (MainActivity.curLangugue != langugueActivity.english) {

                    title += "五";
                }
                break;
            case 6:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "六";
                } else {
                    title += "06";
                }
                break;
            case 7:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "七";
                } else {
                    title += "07";
                }
                break;
            case 8:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "八";
                } else {
                    title += "08";
                }
                break;
            case 9:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "九";
                }
                else {
                    title += "09";
                }
                break;
            case 10:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "十";
                } else {
                    title += "10";
                }
                break;
            case 11:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "十一";
                }  else {
                    title += "11";
                }
                break;
            case 12:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "十二";
                } else {
                    title += "12";
                }
                break;
            case 13:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "十三";
                } else {
                    title += "13";
                }
                break;
            case 14:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "十四";
                } else {
                    title += "14";
                }
                break;
            case 15:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "十五";
                } else {
                    title += "15";
                }
                break;
            case 16:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "十六";
                } else {
                    title += "16";
                }
                break;
            case 17:
                if (MainActivity.curLangugue != langugueActivity.english) {
                    title += "十七";
                } else {
                    title += "17";
                }
                break;
        }
        return title;
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) {
            mDatas = new ArrayList<String>();

            List<ChapterListInfo.DataBean> data  = NetworkReq.getInstance().getChapterListInfo().getData();
            if (data != null) {
                int mid = (data.size() + 1) / 2;
                int right;
                int chapterNum;
                String strChapterNum;
                for (int i=0; i!=mid; i++) {
                    String str = "";
                    if (MainActivity.curLangugue != langugueActivity.english)  {
                        str = "第";
                    }

                    chapterNum = i+1;
                    strChapterNum = getIndex(chapterNum);
                    if (MainActivity.curLangugue != langugueActivity.english) {
                        if (i < 10) {
                            str = str + strChapterNum + "章   ";
                        } else {
                            str = str + strChapterNum + "章   ";
                        }
                    } else {
                        str = "Chapter " + strChapterNum + " ";
                    }

                    if (MainActivity.curLangugue == langugueActivity.chinese) {
                        str = str + data.get(i).getName();

                    } else if (MainActivity.curLangugue == langugueActivity.english) {
                        str = str + data.get(i).getEnName();
                    } else {
                        str = str + data.get(i).getTcName();
                    }

                    mDatas.add(str);

                    String strRight = "";
                    if (MainActivity.curLangugue != langugueActivity.english) {
                        strRight = "第";
                    }

                    right = mid + i;
                    if (right >= data.size())
                        break;
                    chapterNum = right + 1;
                    strChapterNum = getIndex(chapterNum);
                    if (MainActivity.curLangugue != langugueActivity.english) {
                        strRight = strRight + strChapterNum + "章   ";
                    } else {
                        strRight = "Chapter " + strChapterNum + " ";
                    }

                    if (MainActivity.curLangugue == langugueActivity.chinese) {
                        strRight = strRight + data.get(right).getName();

                    } else if (MainActivity.curLangugue == langugueActivity.english) {
                        strRight = strRight + data.get(right).getEnName();

                    } else {
                        strRight = strRight + data.get(right).getTcName();
                    }
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
