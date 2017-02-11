package com.example.username.mydiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

import io.realm.Realm;

import static android.app.Activity.RESULT_OK;

public class InputDiaryFragment extends Fragment {
    private static final String DIARY_ID = "DIARY_ID";
    private static final int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;


    private long mDiaryId;
    private Realm mRealm;
    private EditText mTitleEdit;
    private EditText mBodyEdit;
    private ImageView mDiaryImage;

    public static InputDiaryFragment newInstance(long diaryId) {
        InputDiaryFragment fragment = new InputDiaryFragment();
        Bundle args = new Bundle();
        args.putLong(DIARY_ID, diaryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDiaryId = getArguments().getLong(DIARY_ID);
        }
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_input_diary,
                container, false);
        mTitleEdit = (EditText) v.findViewById(R.id.title);
        mBodyEdit = (EditText) v.findViewById(R.id.body);
        mDiaryImage = (ImageView) v.findViewById(R.id.diary_photo);

        mDiaryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestReadStorage(view);
            }
        });

        mTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Diary diary = realm.where(Diary.class).equalTo("id", mDiaryId)
                                .findFirst();
                        diary.title = s.toString();
                    }
                });
            }
        });

        mBodyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Diary diary = realm.where(Diary.class).equalTo("id", mDiaryId)
                                .findFirst();
                        diary.bodyText = s.toString();
                    }
                });
            }
        });
        return v;
    }

    private void requestReadStorage(View view) {                                // ⑤
        // パーミッションを持っているか確認する
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//⑥
            // 持っていない場合の処理

            // Should we show an explanation? 23未満ならFalse を返す
            // 一度パーミッションを「許可しない」が押された事がある場合にTrueを返します。
            // そのため、2回目は何にこのパーミッションを使うのか詳しく理由を説明することで、
            // ユーザーに許可してもらいやすいようにすることが可能です。
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {//⑦
                // なぜ、端末内の写真、メディアファイルへのアクセス」が必要であるか理由を説明します。
                Snackbar.make(view, R.string.rationale,
                        Snackbar.LENGTH_LONG).show();// ⑧
            }

            // パーミッションを要求する
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);// ⑨

        } else {
            // 持っている場合の処理
            pickImage();//⑩
        }
    }

    private void pickImage() {
        // インテントで画像を選択可能なアプリを選択します。
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//⑪
        intent.setType("image/*");//⑫
        startActivityForResult(intent, REQUEST_CODE);//⑬
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            Uri uri = (data == null) ? null : data.getData();// ⑭
            if (uri != null) {
                try {
                    Bitmap img = MyUtils.getImageFromStream(
                            getActivity().getContentResolver(), uri);//⑮
                    mDiaryImage.setImageBitmap(img);//⑯
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Diary diary = realm.where(Diary.class).equalTo("id", mDiaryId)
                                .findFirst();//⑰
                        BitmapDrawable bitmap =
                                (BitmapDrawable) mDiaryImage.getDrawable();//⑱
                        byte[] bytes =
                                MyUtils.getByteFromImage(bitmap.getBitmap());//⑲
                        if (bytes != null && bytes.length > 0) {
                            diary.image = bytes; //⑳
                        }
                    }
                });
            }
        }
    }

    // パーミッションを要求ダイアログで許可または不許可をおした時
    // 今後表示しないにチェックを入れて「許可しない」を選択した場合は、ダイアログは表示されずにこのメソッドが呼ばれれる。その際granResutsは不許可である。
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {//㉑
        if (requestCode == PERMISSION_REQUEST_CODE) {//㉒
            if (grantResults.length != 1 ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {//㉓
                // ダシアログで「不許可」が選択されたか、「今後表示しない」にチェックを入れたためダイアログが表示されずに自動的に「不許可」となった。
                Snackbar.make(mDiaryImage, R.string.permission_deny,
                        Snackbar.LENGTH_LONG).show();//㉔

            } else {
                // パーミッション許可が選択された。
                pickImage();//㉕
            }
        }
    }
}
