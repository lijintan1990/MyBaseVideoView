package com.xsl.culture.mybasevideoview.view;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xsl.culture.R;
import com.xsl.culture.mybasevideoview.model.WordNode;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordHolder>{
    private List<WordNode> dataLst;

    public WordAdapter(List<WordNode> list) {
        dataLst = list;
    }

    @NonNull
    @Override
    public WordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycle_word_item, viewGroup, false);
        return new WordHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordHolder wordHolder, int i) {
        wordHolder.textView.setText(dataLst.get(i).getText());
        if (dataLst.get(i).getBitmap() != null) {
            wordHolder.imageView.setBackground(new BitmapDrawable(dataLst.get(i).getBitmap()));
        } else {
            ViewGroup.LayoutParams params = wordHolder.imageView.getLayoutParams();
            params.height = 0;
            wordHolder.imageView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return dataLst.size();
    }

    public static class WordHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public WordHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.introduce_text);
            imageView = itemView.findViewById(R.id.introduce_image);
        }
    }
}
