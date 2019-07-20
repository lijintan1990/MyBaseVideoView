package com.example.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mybasevideoview.R;
import com.example.mybasevideoview.model.HomePageInfo;
import com.example.mybasevideoview.model.ObtainNetWorkData;
import com.example.mybasevideoview.utils.XslUtils;

import java.io.IOException;

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
    @BindView(R.id.introduce_text)
    TextView contentView;
    @BindView(R.id.introduce_image)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_word);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String title = (String) intent.getSerializableExtra(String.valueOf(R.string.word_name));
        String content = (String) intent.getSerializableExtra(String.valueOf(R.string.word_content));
        String imageUrl = (String) intent.getSerializableExtra(String.valueOf(R.string.word_image_url));
        titleTextView.setText(title);
        contentView.setText(content);
        updateImageView(imageUrl);
    }

    void updateImageView(String uri) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = ObtainNetWorkData.getBitmap(uri);
                    Message msg = handler.obtainMessage();
                    msg.obj = bmp;
                    handler.sendMessage(msg);
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
            imageView.setBackground(new BitmapDrawable((Bitmap)msg.obj));
        }
    };

    @OnClick(R.id.close_btn)
    void close() {
        finish();
    }
}