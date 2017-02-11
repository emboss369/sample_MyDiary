package com.example.username.mydiary;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user.name on 2017/01/29.
 */

public class Diary extends RealmObject {
    @PrimaryKey
    public long id;
    public String title;
    public String bodyText;
    public String date;
    public byte[] image;
}
