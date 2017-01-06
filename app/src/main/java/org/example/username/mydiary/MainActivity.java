package org.example.username.mydiary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity
        implements DiaryListFragment.OnFragmentInteractionListener
        , PasswordDialog.PasswordDialogListener {

    private static final int REQUEST_CODE = 1;
    protected static final String PASSWORD_LOCK = "password_lock";
    protected static final String PASSWORD_TEXT = "password_text";
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getDefaultInstance();

//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
//
//        // recyclerView.setHasFixedSize(true);
//
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//
//        recyclerView.setLayoutManager(llm);

//        // テストデータ生成
//        List<Diary> diaries= new ArrayList<Diary>();
//        diaries.add(new Diary("What I am grateful for?","I am awful at journaling. It's a great habit to have(especially for me because I always forget stuff), but","Apr 2017",R.drawable.cafe_img));
//        diaries.add(new Diary("Did I exercise today?","New record! Today I ran 8km in about 1 hour.","Apr 2017",R.drawable.cafe_img));
//        diaries.add(new Diary("What did I do for my family today?","Bought a pot of plant for Dad on my way to library.","Apr 2017",R.drawable.cafe_img));
//        diaries.add(new Diary("What did I do for my family today?","Bought a pot of plant for Dad on my way to library.","Apr 2017",R.drawable.cafe_img));
//        diaries.add(new Diary("What did I do for my family today?","Bought a pot of plant for Dad on my way to library.","Apr 2017",R.drawable.cafe_img));
//        diaries.add(new Diary("What did I do for my family today?","Bought a pot of plant for Dad on my way to library.","Apr 2017",R.drawable.cafe_img));
//        diaries.add(new Diary("What did I do for my family today?","Bought a pot of plant for Dad on my way to library.","Apr 2017",R.drawable.cafe_img));
//        diaries.add(new Diary("What did I do for my family today?","Bought a pot of plant for Dad on my way to library.","Apr 2017",R.drawable.cafe_img));
//        diaries.add(new Diary("What did I do for my family today?","Bought a pot of plant for Dad on my way to library.","Apr 2017",R.drawable.cafe_img));

//        DiaryAdapter adapter = new DiaryAdapter(diaries);

//        mRealm = Realm.getDefaultInstance();
//        RealmResults<Diary> diaries = mRealm.where(Diary.class).findAll();
//        DiaryRealmAdapter adapter = new DiaryRealmAdapter(this, diaries, true);
//
//
//        recyclerView.setAdapter(adapter);

        /// テストデータ作成ロジック、あとで消す

//        mRealm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                // idフィールドの最大値を取得
//                Number maxId = mRealm.where(Diary.class).max("id");
//                long nextId = 0;
//                if (maxId != null) nextId = maxId.longValue() + 1;
//                // カテゴリの追加
//                // createObjectではキー項目を渡してオブジェクトを生成する
//                Diary diary = realm.createObject(Diary.class, new Long(nextId));
//                diary.setTitle("テスト");
//                diary.setBodyText(("本文です。今日も１日お仕事頑張った！"));
//
//                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cafe_img);
//                diary.setImage(MyUtils.getByteFromImage(bmp));
//            }
//        });

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isUseLock = pref.getBoolean(PASSWORD_LOCK,false);

        if (isUseLock) {
            PasswordDialog fragment =
                    PasswordDialog.newInstance(R.layout.fragment_password_dialog,
                            R.drawable.ic_key,
                            getString(R.string.unlock), getString(R.string.enter_password_to_continue));
            android.app.FragmentManager manager = getFragmentManager();
            fragment.show(manager, "PasswordDialog");
        } else {
            showDiaryList();
        }
    }

    @Override
    public void onDialogPositiveClick(PasswordDialog dialog) {
        EditText passwd = (EditText) dialog.getDialog().findViewById(R.id.passwd);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String userPasswd = pref.getString(PASSWORD_TEXT, "mydiary");

        if (userPasswd.equals(passwd.getText().toString())) {
            showDiaryList();
        } else {
            finish();
        }
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

    @Override
    public void onDialogNegativeClick(PasswordDialog dialog) {
//        View view = findViewById(android.R.id.content);
//        if (view == null) return;
//        Snackbar.make(view, getString(R.string.canceld), Snackbar.LENGTH_LONG).show();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();

    }

    @Override
    public void onAddDiarySelected() {
        // 新しいダイアリーをDBに
        mRealm.beginTransaction();
        Number maxId = mRealm.where(Diary.class).max("id");
        long nextId = 0;
        if (maxId != null) nextId = maxId.longValue() + 1;
        Diary diary = mRealm.createObject(Diary.class, new Long(nextId));
        mRealm.commitTransaction();

        // 新しく追加したダイアリーを渡す
        InputDiaryFragment todoListFragment = InputDiaryFragment.newInstance(nextId);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content, todoListFragment, "TodoListFragment");
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
