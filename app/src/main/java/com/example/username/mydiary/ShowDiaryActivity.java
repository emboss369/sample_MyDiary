package com.example.username.mydiary;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;

public class ShowDiaryActivity extends AppCompatActivity {
    public static final String DIARY_ID = "DIARY_ID";
    private static final long ERR_CD = -1;

    private String mBodyText;
    private Realm mRealm;
    private Bitmap mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action",
//                        Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mBodyText);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ----------ここから追記
        mRealm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        final long diaryId = intent.getLongExtra(DIARY_ID, ERR_CD);

        TextView body = (TextView) findViewById(R.id.body);
        ImageView imageView = (ImageView) findViewById(R.id.toolbar_image);
        NestedScrollView scrollView =
                (NestedScrollView) findViewById(R.id.scroll_view);


        Diary diary = mRealm.where(Diary.class).equalTo("id", diaryId).findFirst();

//        Log.d("DIary", String.valueOf(diaryId));
//        Log.d("DIary", String.valueOf(diary.id));
//        Log.d("DIary", diary.title);
//        Log.d("DIary", diary.bodyText);
//        Log.d("DIary", diary.date);
//        Log.d("DIary", String.valueOf(diary.image));


        CollapsingToolbarLayout layout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        // タイトルをツールバーに表示
        layout.setTitle(diary.title);

        mBodyText = diary.bodyText;

        // 本文の表示
        body.setText(diary.bodyText);
        // ツールバー背景の変更
        byte[] bytes = diary.image;
        if (bytes != null && bytes.length > 0) {
            mBitmap = MyUtils.getImageFromByte(bytes);
//            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            imageView.setImageBitmap(mBitmap);

            // 代表色を抽出する
            Palette palette = Palette.from(mBitmap).generate();
            // 使用後はビットマップの解放処理を行う

            int titleColor = palette.getLightVibrantColor(Color.WHITE);
            int bodyColor = palette.getDarkMutedColor(Color.BLACK);
            int scrimColor = palette.getMutedColor(Color.DKGRAY);
            int iconColor = palette.getLightMutedColor(Color.LTGRAY);

            layout.setExpandedTitleColor(titleColor);
            layout.setContentScrimColor(scrimColor);
            scrollView.setBackgroundColor(bodyColor);
            body.setTextColor(titleColor);
            // fabの色を変える。　http://androhi.hatenablog.com/entry/2015/06/02/015724
            fab.setBackgroundTintList(ColorStateList.valueOf(iconColor));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
