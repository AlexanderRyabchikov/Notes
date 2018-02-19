package com.example.alexa.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by alexander on 09.02.18.
 */

public class CustomCursorAdapter extends SimpleCursorAdapter {

    public static final int LOW_PRIORITY = 200;
    public static final int MEDIUM_PRIORITY = 300;
    public static final int HIGH_PRIORITY = 400;
    public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }
    public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);


        TextView textView = view.findViewById(R.id.tvText);
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
            default:
                    textView.setBackgroundResource(R.drawable.text_view_border);
                    break;
            }
    }
}
