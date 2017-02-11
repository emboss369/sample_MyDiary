package com.example.username.mydiary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements
        DiaryListFragment.OnFragmentInteractionListener {

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRealm = Realm.getDefaultInstance();

        //createTestData();

        showDiaryList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();

    }

    private void createTestData() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // idフィールドの最大値を取得
                Number maxId = mRealm.where(Diary.class).max("id");
                long nextId = 0;
                if (maxId != null) nextId = maxId.longValue() + 1;
                // カテゴリの追加
                // createObjectではキー項目を渡してオブジェクトを生成する
                Diary diary = realm.createObject(Diary.class, new Long(nextId));
                diary.title = "テストタイトル";
                diary.bodyText = "テスト本文です。";
                diary.date = "Feb 22";
            }
        });
    }

    private void showDiaryList() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("DiaryListFragment");
        if (fragment == null) {
            fragment = new DiaryListFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.content, fragment, "DiaryListFragment");
            transaction.commit();
        }
    }


    public void onAddDiarySelected() {
        // 新しいダイアリーをDBに
        mRealm.beginTransaction();
        Number maxId = mRealm.where(Diary.class).max("id");
        long nextId = 0;
        if (maxId != null) nextId = maxId.longValue() + 1;
        Diary diary = mRealm.createObject(Diary.class, new Long(nextId));
        diary.date = new SimpleDateFormat("MMM d", Locale.US).format(new Date());
        mRealm.commitTransaction();

        // 新しく追加したダイアリーを渡す
        InputDiaryFragment inputDiaryFragment = InputDiaryFragment.newInstance(nextId);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content, inputDiaryFragment, "InputDiaryFragment");
        transaction.addToBackStack(null);
        transaction.commit();

    }

}
