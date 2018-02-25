package com.example.alexa.notes;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by alexander on 09.02.18.
 */

public class CustomCursorAdapter implements SimpleCursorAdapter.ViewBinder{


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean setViewValue(View view, Cursor cursor, int i) {
        switch (view.getId()){
            case R.id.tvText:
                TextView textView = (TextView) view;
                textView.setText(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_TITLE)));
                int priority_id = cursor.getColumnIndex(DataBase.COLUMN_PRIORITY);
                switch (cursor.getInt(priority_id)){
                    case C.LOW_PRIORITY:
                        textView.setBackgroundColor(Color.GREEN);
                        break;
                    case C.MEDIUM_PRIORITY:
                        textView.setBackgroundColor(Color.YELLOW);
                        break;
                    case C.HIGH_PRIORITY:
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
                        C.getImage(cursor.getBlob(cursor.getColumnIndex(DataBase.COLUMN_IMAGE_SMALL))));
                break;
        }
        return true;
    }
}
