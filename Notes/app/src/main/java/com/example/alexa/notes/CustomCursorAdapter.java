package com.example.alexa.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by alexander on 09.02.18.
 */

public class CustomCursorAdapter implements SimpleCursorAdapter.ViewBinder{

    public static final int LOW_PRIORITY = 200;
    public static final int MEDIUM_PRIORITY = 300;
    public static final int HIGH_PRIORITY = 400;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean setViewValue(View view, Cursor cursor, int i) {
        switch (view.getId()){
            case R.id.tvText:
                TextView textView = (TextView) view;
                textView.setText(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_TITLE)));
                int priority_id = cursor.getColumnIndex(DataBase.COLUMN_PRIORITY);
                switch (cursor.getInt(priority_id)){
                    case LOW_PRIORITY:
                        textView.setBackgroundColor(Color.GREEN);
                        break;
                    case MEDIUM_PRIORITY:
                        textView.setBackgroundColor(Color.YELLOW);
                        break;
                    case HIGH_PRIORITY:
                        textView.setBackgroundColor(Color.RED);
                        break;
                    default:
                        textView.setBackgroundResource(R.drawable.text_view_border);
                        break;
                }
                break;
            case R.id.ivImg:
                ImageView imageView = (ImageView)view;
                imageView.setImageBitmap(
                        PreviewNote.getImage(cursor.getBlob(cursor.getColumnIndex(DataBase.COLUMN_IMAGE_SMALL))));
                break;
        }
        return true;
    }
}
