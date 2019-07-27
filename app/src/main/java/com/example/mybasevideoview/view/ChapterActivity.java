package com.example.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.mybasevideoview.R;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

public class ChapterActivity extends Activity {
    RecyclerView recyclerView;
    int selectedIndex = 0;
    public static final String chapter_key = "chapter";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chapter);
        XslUtils.hideStausbar(new WeakReference<>(this), true);

        init();
    }

    void init() {
        recyclerView = findViewById(R.id.chapter_recycle_view);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setAlpha(0.5f);
        ChapterAdapter chapterAdapter = new ChapterAdapter();
        chapterAdapter.setOnItemClickListenner(new ChapterAdapter.OnItemClickListenner() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(getBaseContext(),"long click " + position + " item", Toast.LENGTH_SHORT).show();
                //view.setBackground(R.drawable.xsl_chapter_item_selected);
                chapterAdapter.setThisPostion(position);
                chapterAdapter.notifyDataSetChanged();
                //这里可以直接改变颜色，但是你不知道哪个需要变回去
                //view.setBackgroundResource(R.drawable.xsl_chapter_item_selected);
                selectedIndex = position;
            }
        });
        recyclerView.setAdapter(chapterAdapter);


        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.putExtra(chapter_key, selectedIndex);
                setResult(RequestCode.Chapter_req, intent);
                finish();
            }
        });
    }
}
