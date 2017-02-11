package com.example.username.mydiary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by user.name on 2017/01/29.
 */

public class DiaryRealmAdapter
        extends RealmRecyclerViewAdapter<Diary, DiaryRealmAdapter.DiaryViewHolder> {

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected TextView bodyText;
        protected TextView date;
        protected ImageView photo;

        public DiaryViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            bodyText = (TextView) itemView.findViewById(R.id.body);
            date = (TextView) itemView.findViewById(R.id.date);
            photo = (ImageView) itemView.findViewById(R.id.diary_photo);
        }
    }

    public DiaryRealmAdapter(
            @NonNull Context context,
            @Nullable
                    OrderedRealmCollection<Diary> data, boolean autoUpdate) {
        super(context, data, autoUpdate);
    }


    @Override
    public DiaryViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        final DiaryViewHolder holder = new DiaryViewHolder(itemView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Diary diary = getData().get(position);
                long diaryId = diary.id;

                Intent intent = new Intent(context, ShowDiaryActivity.class);
                intent.putExtra(ShowDiaryActivity.DIARY_ID, diaryId);
                context.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(DiaryViewHolder holder,
                                 int position) {
        Diary diary = getData().get(position);
        holder.title.setText(diary.title);
        holder.bodyText.setText(diary.bodyText);
        holder.date.setText(diary.date);
        if (diary.image != null && diary.image.length != 0) {
            Bitmap bmp = MyUtils.getImageFromByte(diary.image);
            holder.photo.setImageBitmap(bmp);
        }
    }
}
