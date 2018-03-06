package Helpers.CustomClass;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.alexa.notes.R;

import Helpers.Constants.C;
import Helpers.DataBase.DataBase;

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
                        textView.setBackgroundResource(R.drawable.border_edit_text);
                        break;
                }
                break;
            case R.id.ivImg:
                ImageView imageView = (ImageView)view;
                Bitmap bitmap = C.getImage(cursor.getBlob(cursor.getColumnIndex(DataBase.COLUMN_IMAGE_SMALL)));
                if(bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }else{
                    imageView.setImageBitmap(null);
                }
                break;
        }
        return true;
    }
}
