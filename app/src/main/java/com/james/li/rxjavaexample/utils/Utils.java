package com.james.li.rxjavaexample.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by jyj-lsy on 9/12/16 in zsl-tech.
 */
public class Utils {

    public static Observable<Bitmap> getBitmap(final String iconPath) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                subscriber.onNext(BitmapFactory.decodeFile(iconPath));
                subscriber.onCompleted();
            }
        });
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void storeBitmap(final Context context, final Bitmap bitmap, final String filename) {
        Schedulers.io().createWorker()
                .schedule(new Action0() {
                    @Override
                    public void call() {
                        blockingStoreBitmap(context, bitmap, filename);
                    }
                }
        );
    }

    private static void blockingStoreBitmap(Context context, Bitmap bitmap, String filename) {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(new File(filename));
            // fOut = context.openFileOutput(filename, Context.MODE_PRIVATE);
            /**
             openFileInput() doesn't accept paths, only a file name if you want to access a path,
             use new File(path) and corresponding FileInputStream
             */
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
