package com.example.mybasevideoview;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.mybasevideoview.view.FirstPaperFragment;
import com.example.mybasevideoview.view.XslAboutFragmet;
import com.example.mybasevideoview.view.XslMainFragment;
import com.next.easynavigation.view.EasyNavigationBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    Button mStartNewAtyBtn;
    Button mButterKnifeBtn;
    private String[] tabText = {"首页", "乡射礼", "关于"};
    //未选中icon
    private int[] normalIcon = {R.mipmap.index, R.mipmap.find, R.mipmap.message};
    //选中时icon
    private int[] selectIcon = {R.mipmap.index1, R.mipmap.find1, R.mipmap.message1};

    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //hideStausbar(true);
        //ButterKnife.bind(this);
        EasyNavigationBar navigationBar = findViewById(R.id.navigationBar);

        fragments.add(new FirstPaperFragment());
        fragments.add(new XslMainFragment());
        fragments.add(new XslAboutFragmet());

        navigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .fragmentList(fragments)
                .canScroll(true)
                .fragmentManager(getSupportFragmentManager())
                .build();
    }

    //true隐藏，false显示
    private void hideStausbar(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        } else {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        }
    }
}
