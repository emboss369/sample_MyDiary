package com.example.username.mydiary;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by user.name on 2017/01/29.
 */

public class MyDiaryApplication  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig
                = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);

    }

}
