package com.example.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mybasevideoview.MainActivity;
import com.example.mybasevideoview.R;
import com.example.mybasevideoview.model.ObtainNetWorkData;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.model.WordMsgs;
import com.example.mybasevideoview.model.WordNode;
import com.example.mybasevideoview.utils.XslUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordActivity extends Activity {
    private String TAG = "AIVideo";
    @BindView(R.id.title)
    TextView titleTextView;

    private RecyclerView recyclerView;
    private WordAdapter wordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_word);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int index = intent.getIntExtra(String.valueOf(R.string.activity_value), 0);
        getWordMsgs(index, MainActivity.curLangugue);
    }

    private ArrayList<WordNode> dataLst;
    private void initRecycleView() {
            recyclerView = findViewById(R.id.word_recycle_view);
            wordAdapter = new WordAdapter(dataLst);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(wordAdapter);
    }

    private void initRecyDataLst(WordMsgs msgs) {
        dataLst = new ArrayList<>();
        List<WordMsgs.DataBean> dataBeanLst = msgs.getData();
        for (WordMsgs.DataBean dataBean : dataBeanLst) {
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.main_player_bkg);
            dataLst.add(new WordNode(dataBean.getContent(), dataBean.getImgUrl(), null));
        }
    }

    private void getWordMsgs(int index, int langugueType) {
        ObtainNetWorkData.getWordListData(new Callback<WordMsgs>() {
            @Override
            public void onResponse(Call<WordMsgs> call, Response<WordMsgs> response) {
                WordMsgs wordMsgs = response.body();
                Log.d(TAG, "get word list ok. info:" + response.toString());
                initRecyDataLst(wordMsgs);
                initRecycleView();
                updateImageView();
            }

            @Override
            public void onFailure(Call<WordMsgs> call, Throwable t) {
                Log.w(TAG, "get word list failed, "+t.toString());
            }
        }, index, langugueType);
    }

    void updateImageView() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url;
                    for (int i=0; i!=dataLst.size(); i++) {
                        url = dataLst.get(i).getUrl();
                        if (url != null) {
                            Log.d(TAG, "get bitmapt: "+url);
                            Bitmap bmp = ObtainNetWorkData.getBitmap(url);
                            dataLst.get(i).setBitmap(bmp);

                            Message msg = handler.obtainMessage();
                            msg.obj = bmp;
                            msg.arg1 = i;
                            handler.sendMessage(msg);
                            Log.d(TAG, "update bitmap " + i + " uri:"+url);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "update bitmap " + msg.arg1);
            //更新recycleView中的图片
            wordAdapter.notifyItemChanged(msg.arg1);
        }
    };

    @OnClick(R.id.close_btn)
    void close() {
        setResult(RequestCode.Word_req);
        finish();
    }
}
