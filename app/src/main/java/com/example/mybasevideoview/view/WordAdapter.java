package com.example.mybasevideoview.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mybasevideoview.R;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordHolder>{


    @NonNull
    @Override
    public WordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull WordHolder wordHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
