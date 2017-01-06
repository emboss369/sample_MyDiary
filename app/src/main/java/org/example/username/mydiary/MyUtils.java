package org.example.username.mydiary;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MyUtils {

    // ビットマップをバイト配列に変換します
    public static byte[] getByteFromImage(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    // バイト配列をビットマップに変換します
    public static Bitmap getImageFromByte(byte[] bytes) {
        // オプションを作成しデータのサイズ確認のみに設定
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length ,opt);
        int bitmapSize = 1;
        if((opt.outHeight * opt.outWidth) > 500000) {
            //50万ピクセル以上の場合はサイズを小さくして読み込む
            double outSize = (double) (opt.outHeight * opt.outWidth) / 500000;
            bitmapSize = (int)(Math.sqrt(outSize) + 1);
        }

        // 読み込みサイズがわかったので実際に読みこむモードに変更します。
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = bitmapSize; // inSampleSizeを2にすると、縦横のサイズが1/2、3にすると縦横のサイズが1/3になる。

        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length ,opt);
        return bmp;
    }

    public static Bitmap getImageFromStream(ContentResolver resolver, Uri uri) throws IOException {
        InputStream in;

        // オプションを作成しデータのサイズ確認のみに設定
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        in = resolver.openInputStream(uri);
        BitmapFactory.decodeStream(in,null,opt);
        in.close();
        int bitmapSize = 1;
        if((opt.outHeight * opt.outWidth) > 100000) {
            //10万ピクセル以上の場合はサイズを小さくして読み込む
            double outSize = (double) (opt.outHeight * opt.outWidth) / 100000;
            bitmapSize = (int)(Math.sqrt(outSize) + 1);
        }

        // 読み込みサイズがわかったので実際に読みこむモードに変更します。
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = bitmapSize; // inSampleSizeを2にすると、縦横のサイズが1/2、3にすると縦横のサイズが1/3になる。
        in = resolver.openInputStream(uri);
        Bitmap bmp = BitmapFactory.decodeStream(in,null,opt);
        in.close();
        return bmp;

    }

    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, color));
        item.setIcon(wrapDrawable);
    }
}
