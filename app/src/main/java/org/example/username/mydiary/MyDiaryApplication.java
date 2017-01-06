package org.example.username.mydiary;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hiroaki on 2016/11/07.
 */

public class MyDiaryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig
                = new RealmConfiguration.Builder().build();
        // テスト時にDBの構造を変更した時やデータを一度クリアしたい時などは一旦DBを削除すると良い。
        //Realm.deleteRealm(realmConfig);
        Realm.setDefaultConfiguration(realmConfig);

    }
}
