package com.example.alexa.notes;

import android.app.Dialog;
import android.content.Context;
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

final class C {

    /*Constants*/
    static final int SAVE_TO_FILE = 3;
    static final int SIZE_IMAGE_PREVIEW = 52;
    static final String INTENT_UPDATE_MAIN = "update_main";
    static final String SUCCESS_MSG_DB = "Запись успешно сохранена";
    static int RADIO_SELECT_ID = -100;
    static final String TITLE_DIALOG_IMAGE = "Выбор изображения";
    static final String MESSAGE_GPS_ON = "Gsp is turned on";
    static final String MESSAGE_GPS_OFF = "Gsp is turned off";
    static final String GPS_ERROR = "Ошибка GSP модуля";
    static final String GPS_PLACE_NOT_FOUND = "Не могу определить местонахождение";
    static final String GPS_PLACE_FOUND = "Координаты определены";
    static final int LOW_PRIORITY = 200;
    static final int MEDIUM_PRIORITY = 300;
    static final int HIGH_PRIORITY = 400;
    static final String FILE_SAVE = "File saved";
    static final String FILE_NOT_SAVE = "File saved";
    static final String FLASH_ERROR = "Flash not installed";
    static final int RESULT_LOAD_IMAGE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int CM_DELETE_ID = 1;
    static final int CM_EDIT_ID = 2;
    static final String INTENT_CREATE_NOTE = "Create_note";
    static final String INTENT_EDIT_NOTE = "edit_note";
    static final String INTENT_PREVIEW_NOTE = "Preview_note";
    static final String map = "googleMaps";
    static final String DELETE_SUCCESS_MSG = "Запись успешно удалена";
    static final String COORDINATE_SELECT = "Координаты выбраны";
    static final String TITLE_DIALOG_SAVE_FILE = "Name file to save...";
    static final String NAME_POSITIVE_BUTTON = "OK";
    static final String NAME_NEGATIVE_BUTTON = "Cancel";
    static final String ERROR_TEXT_EMPTY = "Это поле не должно быть пустым";
/*Constants*/

    /*Global variable*/
    public static Context context;
    static ProgressBar bar;
    static LocationManager locationManager;
    static double lintitude;
    static double longtitude;
    static Dialog dialogImage;
    static Uri mCapturedImageURI;
    /*Global variable*/


    /*Functions*/
    static Bitmap getImage(byte[] image) {
        if (image == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    static byte[] convertToSmallImage(final byte[] imageOriginal, final int size) {
        if (imageOriginal == null) {
            return null;
        }
        final byte[][] imageCompress = new byte[1][1];
        Runnable runnable = () -> {
            Bitmap bitmap = C.getImage(imageOriginal);
            bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
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

    static void ToastMakeText(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    static void setupLocale() {
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
    }
/*Functions*/
}
