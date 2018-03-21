package helpers.constants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * Created by alexa on 25.02.2018.
 */

public final class Constants {

    /*Constants*/
    public static final int SAVE_TO_FILE = 3;
    public static final int SIZE_IMAGE_PREVIEW = 52;
    public static final String INTENT_UPDATE_MAIN = "update_main";
    public static final String SUCCESS_MSG_DB = "Запись успешно сохранена";
    public static final String SUCCESS_IMAGE_SELECT = "Изображение выбрано";
    public static int RADIO_SELECT_ID = -100;
    public static final String TITLE_DIALOG_IMAGE = "Выбор изображения";
    public static final int CM_DELETE_ID = 1;
    public static final int CM_EDIT_ID = 2;
    public static final String INTENT_CREATE_NOTE = "Create_note";
    public static final String INTENT_MAPS_WITH_COORDINATES_LONG = "maps_get_coordinate_long";
    public static final String INTENT_MAPS_WITH_COORDINATES_LAT = "maps_get_coordinate_lat";
    public static final String INTENT_MAPS_CHEKCBOX_FLAG = "maps_checkbox_flag";
    public static final String INTENT_EDIT_NOTE = "edit_note";
    public static final String INTENT_PREVIEW_NOTE = "Preview_note";
    public static final String map = "googleMaps";
    public static final String DELETE_SUCCESS_MSG = "Запись успешно удалена";
    public static final String COORDINATE_SELECT = "Координаты выбраны";
    public static final String TITLE_DIALOG_SAVE_FILE = "Default name file defaultName";
    public static final String NAME_POSITIVE_BUTTON = "OK";
    public static final String NAME_NEGATIVE_BUTTON = "Cancel";
    public static final String ERROR_TEXT_EMPTY = "Это поле не должно быть пустым";
    public static final int LOW_PRIORITY = 200;
    public static final int MEDIUM_PRIORITY = 300;
    public static final int HIGH_PRIORITY = 400;
    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int REQUEST_MAPS = 11;
    public static final String MESSAGE_GPS_ON = "Gsp is turned on";
    public static final String MESSAGE_GPS_OFF = "Gsp is turned off";
    public static final String GPS_ERROR = "Ошибка GSP модуля";
    public static final String GPS_PLACE_FOUND = "Координаты определены";
    public static final String FILE_SAVE = "File saved";
    public static final String FILE_NOT_SAVE = "File saved";
    public static final String FLASH_ERROR = "Flash not installed";
    /*Constants*/

    /*Functions*/
    public static Bitmap getImage(byte[] image) {
        if (image == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static byte[] convertToSmallImage(final byte[] imageOriginal, final int size) {
        if (imageOriginal == null) {
            return null;
        }
        final byte[][] imageCompress = new byte[1][1];
        Runnable runnable = () -> {
            Bitmap bitmap = Constants.getImage(imageOriginal);
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

    public static void ToastMakeText(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void setupLocale() {
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
    }
/*Functions*/
}
