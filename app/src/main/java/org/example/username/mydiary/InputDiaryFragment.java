package org.example.username.mydiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private EditText mTitleEdit;
    private EditText mBodyEdit;
    private ImageView mDiaryImage;

    private Realm mRealm;




//    public InputDiaryFragment() {
//        // Required empty public constructor
//    }

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
        View v = inflater.inflate(R.layout.fragment_input_diary, container, false);
        mTitleEdit = (EditText) v.findViewById(R.id.title);
        mBodyEdit = (EditText) v.findViewById(R.id.body);
        mDiaryImage = (ImageView) v.findViewById(R.id.diary_photo);

        mDiaryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // http://stackoverflow.com/questions/32431723/read-external-storage-permission-for-android

                // パーミッションを持っているか確認する
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Explain to the user why we need to read the contacts
                        ActivityCompat.requestPermissions(getActivity(), new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, PERMISSION_REQUEST_CODE);
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                },
                                PERMISSION_REQUEST_CODE);
                        pickImage();
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            },
                            PERMISSION_REQUEST_CODE);
                    pickImage();
                }
            }
        });


        mTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 何もしない
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 何もしない

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Diary diary = realm.where(Diary.class).equalTo("id", mDiaryId).findFirst();
                        diary.setTitle(mTitleEdit.getText().toString());
                    }
                });
            }
        });
        mBodyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Diary diary = realm.where(Diary.class).equalTo("id", mDiaryId).findFirst();
                        diary.setBodyText(mBodyEdit.getText().toString());
                    }
                });
            }
        });


        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // superは空なので呼ぶ必要はない。
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    // パーミッションが得られなかった時
                    Snackbar.make(mDiaryImage,"パーミンションが得られませんでした",Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    private void pickImage() {
        // インテントで画像を選択可能なアプリを選択します。
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        getString(R.string.pick_image)
                ),
                REQUEST_CODE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE &&  resultCode == RESULT_OK) {

            Uri uri = (data == null) ? null : data.getData();
            if (uri != null) {
                try {
                    Bitmap img = MyUtils.getImageFromStream(getActivity().getContentResolver(),uri);
                    mDiaryImage.setImageBitmap(img);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Diary diary = realm.where(Diary.class).equalTo("id", mDiaryId).findFirst();
                        BitmapDrawable bitmap = (BitmapDrawable)mDiaryImage.getDrawable();
                        byte[] bytes = MyUtils.getByteFromImage(bitmap.getBitmap());
                        if (bytes!=null && bytes.length > 0) {
                            diary.setImage(bytes);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_input_diary, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // このメソッドは継承元は空っぽなのでsuperを呼び出す必要はありません。
        switch (item.getItemId()) {
            case R.id.menu_item_delete_diary:
                // Diaryを削除する
                final Diary diary = mRealm.where(Diary.class).equalTo("id", mDiaryId).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        diary.deleteFromRealm();
                    }
                });
                // 前画面に戻る
                getFragmentManager().popBackStack();
                return true;
        }
        return false;
    }
}
