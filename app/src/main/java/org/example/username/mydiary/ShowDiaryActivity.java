package org.example.username.mydiary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;

public class ShowDiaryActivity extends AppCompatActivity {

    public static final String DIARY_ID = "DIARY_ID";

    private static final long ERR_CD = -1;

    private Realm mRealm;

    private ShareActionProvider mShareActionProvider;

    private static final int PERMISSION_REQUEST_CODE = 2;

    private Bitmap mBitmap;

    private String mBodyText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // UPナビゲーション
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = createShareIntent();
                startActivity(shareIntent);
            }
        });


        mRealm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        final long diaryId = intent.getLongExtra(DIARY_ID, ERR_CD);

        TextView body = (TextView) findViewById(R.id.body);
        ImageView imageView = (ImageView) findViewById(R.id.toolbar_image);


        Diary diary = mRealm.where(Diary.class).equalTo("id", diaryId).findFirst();

        CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        // タイトルをツールバーに表示
        layout.setTitle(diary.getTitle());

        mBodyText = diary.getBodyText();



        // 本文の表示
        body.setText(diary.getBodyText());
        // ツールバー背景の変更
        byte[] bytes = diary.getImage();
        if(bytes!=null && bytes.length>0) {
            mBitmap = MyUtils.getImageFromByte(bytes);
//            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            imageView.setImageBitmap(mBitmap);

            // 代表色を抽出する
            Palette palette = Palette.from(mBitmap).generate();
            // 使用後はビットマップの解放処理を行う

            int titleColor = palette.getLightVibrantColor(Color.BLACK);
            int bodyColor = palette.getDarkMutedColor(Color.BLACK);
            int scrimColor = palette.getMutedColor(Color.DKGRAY);

            layout.setExpandedTitleColor(titleColor);
            body.setTextColor(bodyColor);
            layout.setContentScrimColor(scrimColor);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // UPナビゲーションが押された場合、画面を閉じる。
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_show_diary, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider =  (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = createShareIntent();
        mShareActionProvider.setShareIntent(shareIntent);
        // メニューを画面に表示するためTrueを返却してください
        return true;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mBodyText);
        shareIntent.setType("text/plain");
        return shareIntent;
    }


}
