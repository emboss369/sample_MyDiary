package org.example.username.mydiary;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hiroaki on 2016/11/05.
 */

public class Diary  extends RealmObject{
    @PrimaryKey
    protected long id;
    protected String title;
    protected String bodyText;
    protected String date;
    protected byte[] image;
//    public Diary(String title,String bodyText,String date,byte[] image){
//        this.title = title;
//        this.bodyText = bodyText;
//        this.date = date;
//        this.image = image;
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
