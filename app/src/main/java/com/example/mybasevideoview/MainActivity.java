package com.example.mybasevideoview;

//import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.example.mybasevideoview.utils.XslUtils;
import com.kk.taurus.playerbase.widget.BaseVideoView;
//import android.widget.Button;
//
//import com.example.mybasevideoview.view.FirstPaperFragment;
//import com.example.mybasevideoview.view.XslAboutFragmet;
//import com.example.mybasevideoview.view.XslMainFragment;
//import com.next.easynavigation.view.EasyNavigationBar;
//
//import java.util.ArrayList;
//import java.util.List;
//
import java.lang.ref.WeakReference;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
//    private String[] tabText = {"首页", "乡射礼", "关于"};
//    //未选中icon
//    private int[] normalIcon = {R.mipmap.index, R.mipmap.find, R.mipmap.message};
//    //选中时icon
//    private int[] selectIcon = {R.mipmap.index1, R.mipmap.find1, R.mipmap.message1};
//
//    private List<Fragment> fragments = new ArrayList<>();
    BaseVideoView oneVideoView = null;
    BaseVideoView subVideoView = null;
    BaseVideoView wholeVideoView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        //ButterKnife.bind(this);
//        EasyNavigationBar navigationBar = findViewById(R.id.navigationBar);
//        fragments.add(new FirstPaperFragment());
//        fragments.add(new XslMainFragment());
//        fragments.add(new XslAboutFragmet());
//
//        navigationBar.titleItems(tabText)
//                .normalIconItems(normalIcon)
//                .selectIconItems(selectIcon)
//                .fragmentList(fragments)
//                .canScroll(true)
//                .fragmentManager(getSupportFragmentManager())
//                .build();
        init();
    }

    void init() {
        oneVideoView = findViewById(R.id.one);
        subVideoView = findViewById(R.id.subMovie);
        wholeVideoView = findViewById(R.id.wholeMovie);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        RelativeLayout view = (RelativeLayout) layoutInflater.inflate(R.layout.layout_sub_film, null,false);
        subVideoView.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubFilmActivity.class);
                startActivity(intent);
            }
        });

        LayoutInflater layoutInflater1 = LayoutInflater.from(this);
        View view1 = layoutInflater1.inflate(R.layout.layout_whole_file, null, false);
        wholeVideoView.addView(view1);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainPlayerActivity.class);
                startActivity(intent);
            }
        });
    }
}
