package com.example.alexa.notes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * Created by alexa on 25.02.2018.
 */

public final class C {

/*Constants*/
    public static final int SAVE_TO_FILE = 3;
    public static final int SIZE_IMAGE_PREVIEW = 52;
    public static final String INTENT_UPDATE_MAIN = "update_main";
    public static final String SUCCESS_MSG_DB = "Запись успешно сохранена";
    public static int RADIO_SELECT_ID = -100;
    public static final String TITLE_DIALOG_IMAGE = "Выбор изображения";
    public static final String MESSAGE_GPS_ON = "Gsp is turned on";
    public static final String MESSAGE_GPS_OFF = "Gsp is turned off";
    public static final String GPS_ERROR = "Ошибка GSP модуля";
    public static final String GPS_PLACE_NOT_FOUND = "Не могу определить местонахождение";
    public static final String GPS_PLACE_FOUND = "Координаты определены";
    public static final int LOW_PRIORITY = 200;
    public static final int MEDIUM_PRIORITY = 300;
    public static final int HIGH_PRIORITY = 400;
    public static final String FILE_SAVE = "File saved";
    public static final String FILE_NOT_SAVE = "File saved";
    public static final String FLASH_ERROR = "Flash not installed";
    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int CM_DELETE_ID = 1;
    public static final int CM_EDIT_ID = 2;
    public static final String INTENT_CREATE_NOTE = "Create_note";
    public static final String INTENT_EDIT_NOTE = "edit_note";
    public static final String INTENT_PREVIEW_NOTE = "Preview_note";
    public static final String map = "googleMaps";
    public static final String DELETE_SUCCESS_MSG = "Запись успешно удалена";
    public static final String COORDINATE_SELECT = "Координаты выбраны";
    public static final String TITLE_DIALOG_SAVE_FILE = "Name file to save...";
    public static final String NAME_POSITIVE_BUTTON = "OK";
    public static final String NAME_NEGATIVE_BUTTON = "Cancel";
    public static final String ERROR_TEXT_EMPTY = "Это поле не должно быть пустым";
/*Constants*/

/*Global variable*/
    public static Context context;
    public static ProgressBar bar;
    public static LocationManager locationManager;
    public static double lintitude;
    public static double longtitude;
    public static Dialog dialogImage;
    public static Uri mCapturedImageURI;
/*Global variable*/



/*Functions*/
    public static Bitmap getImage(byte[] image){
        if (image == null){
            return null;
        }
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static byte[] convertToSmallImage(final byte[]imageOriginal, final int size){
        if (imageOriginal == null){
            return null;
        }
        final byte[][] imageCompress = new byte[1][1];
        Runnable runnable = ()->{
                Bitmap bitmap = C.getImage(imageOriginal);
                bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream);
                imageCompress[0] = byteArrayOutputStream.toByteArray();
            };
        Thread trThread = new Thread(runnable);
        trThread.start();
        try {
            trThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return imageCompress[0];
    }
    public static void ToastMakeText(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void setupLocale(){
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
    }
/*Functions*/
}
