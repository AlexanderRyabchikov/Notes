package com.example.alexa.notes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import Helpers.Constants.Constants;
import Helpers.CustomClass.CustomTextWatcher;
import Helpers.DataBase.DataBase;
import Helpers.CustomDialog.DialogInputImage;
import Helpers.AsyncTasks.GpsTask;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEdit_activity extends Activity implements View.OnClickListener {

    private Intent intent;
    private DataBase dataBase;
    private boolean bFlagCheckCreate = true;
    private int id_edit_note = -1;
    private EditText editTextTitle;
    private EditText editTextContent;
    private CheckBox checkBoxAuto;
    private double lintitude = 0;
    private double longtitude = 0;
    private CheckBox checkBoxManual;
    public static Button saveButton;
    private RadioGroup radioGroup;
    private String picturePath = null;
    private ProgressBar bar;
    private byte[] image = null;
    private byte[] imageSmall = null;
    private Dialog dialogImage = null;
    DialogInputImage dialogInputImage;

    private GpsTask gpsTask;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_activity);
        Constants.setupLocale();
        initActivity();
        EditActivity_create();
    }


    private void EditActivity_create(){
        if(!intent.getBooleanExtra(Constants.INTENT_CREATE_NOTE, false)){
            // Здесь заполнение данными если был вызван для правки
            bFlagCheckCreate = false;
            long positionId = intent.getLongExtra(Constants.INTENT_EDIT_NOTE, -1);
            String title;
            String content;
            int radioButtonSelectId = 0;
            dataBase.open_connection();
            Cursor cursor = dataBase.getEntry(positionId);

            if (cursor.moveToFirst()){
                do{
                    id_edit_note = cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_ID));
                    title = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_TITLE));
                    content = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_CONTENT));
                    lintitude = cursor.getDouble(cursor.getColumnIndex(DataBase.COLUMN_LINTITIDE));
                    longtitude = cursor.getDouble(cursor.getColumnIndex(DataBase.COLUMN_LONGTITUDE));
                    image = cursor.getBlob(cursor.getColumnIndex(DataBase.COLUMN_IMAGE));
                    imageSmall = cursor.getBlob(cursor.getColumnIndex(DataBase.COLUMN_IMAGE_SMALL));
                    radioButtonSelectId = cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_PRIORITY));
                }while(cursor.moveToNext());

                editTextTitle.setText(title);
                editTextContent.setText(content);
                setCheckedRadioButton(radioButtonSelectId);
            }
            stopManagingCursor(cursor);
            cursor.close();
            dataBase.close_connection();

        }
        editTextTitle.requestFocus(); // выставляем фокус на заголовок

    }
    /**
    *   Метод для выставление ранее выбранного приоритета
    *   при редактировании заметки
    */
    private void setCheckedRadioButton(int radioButtonSelectId) {
        switch (radioButtonSelectId){
            case Constants.LOW_PRIORITY:
                radioGroup.check(R.id.lowPriority);
                break;
            case Constants.MEDIUM_PRIORITY:
                radioGroup.check(R.id.mediumPriority);
                break;
            case Constants.HIGH_PRIORITY:
                radioGroup.check(R.id.highPriority);
                break;
            default:
                break;
        }
    }

    private void initActivity(){
        dataBase = new DataBase(this);
        intent = getIntent();

        bar = findViewById(R.id.progressBar);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        radioGroup = findViewById(R.id.radioGroup);
        GetSelectedRadioButton();
        checkBoxAuto = findViewById(R.id.gpsCheckedAuto);
        checkBoxManual = findViewById(R.id.gpsCheckedMaps);
        saveButton = findViewById(R.id.saveBt);

        editTextTitle.addTextChangedListener(new CustomTextWatcher(editTextTitle));
        editTextContent.addTextChangedListener(new CustomTextWatcher(editTextContent));
        checkBoxManual.setOnClickListener(this);
        checkBoxAuto.setOnClickListener(this);
        findViewById(R.id.cancelBt).setOnClickListener(this);
        saveButton.setOnClickListener(this);
        findViewById(R.id.addImageButton).setOnClickListener(this);

        locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    /**
    *  Метод для получения Ид выбранного приоритета
    *  в блоке radioGroup
    */
    private void GetSelectedRadioButton() {
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
               switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.lowPriority:
                        Constants.RADIO_SELECT_ID = Constants.LOW_PRIORITY;
                        break;
                    case R.id.mediumPriority:
                        Constants.RADIO_SELECT_ID = Constants.MEDIUM_PRIORITY;
                        break;
                    case R.id.highPriority:
                        Constants.RADIO_SELECT_ID = Constants.HIGH_PRIORITY;
                        break;
                    default:
                        Constants.RADIO_SELECT_ID = -100;
                        break;


                }
            });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancelBt:
                sendResultWithClose();
                break;
            case R.id.saveBt:
                if(SaveToDB()){
                    Constants.ToastMakeText(getBaseContext(), Constants.SUCCESS_MSG_DB);
                    cleanAllForm();
                }
                break;
            case R.id.addImageButton:
                dialogInputImage = new DialogInputImage(this,
                        Constants.TITLE_DIALOG_IMAGE,
                        CreateEdit_activity.this);
                dialogInputImage.createDialog();
                dialogImage = dialogInputImage.getDialogImage();
                break;
            case R.id.gpsCheckedAuto:
                /* Используется для скрытия клавиатуры по завершению ввода*/
                InputMethodManager inputMethodManager = (InputMethodManager)
                        CreateEdit_activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow
                        (CreateEdit_activity.this.getCurrentFocus().getWindowToken(), 0);

                if (checkBoxAuto.isChecked()){
                    saveButton.setEnabled(false);
                    checkBoxManual.setChecked(false);
                    checkBoxManual.setSelected(false);
                    gpsTask = new GpsTask();
                    gpsTask.setContext(this);
                    gpsTask.setProgressBar(bar);
                    gpsTask.setLocationManager(locationManager);
                    gpsTask.execute();
                }else{
                    lintitude = 0;
                    longtitude = 0;
                    saveButton.setEnabled(true);
                    cancelTask();
                }

                break;
            case R.id.gpsCheckedMaps:
                inputMethodManager = (InputMethodManager)
                        CreateEdit_activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow
                        (CreateEdit_activity.this.getCurrentFocus().getWindowToken(), 0);

                if (checkBoxManual.isChecked()){
                    bar.setVisibility(View.INVISIBLE);
                    saveButton.setEnabled(true);
                    checkBoxAuto.setChecked(false);
                    checkBoxAuto.setSelected(false);
                    if (gpsTask != null){
                        cancelTask();
                    }
                    Intent intentMaps = new Intent(this, MapsActivity.class);
                    startActivityForResult(intentMaps, Constants.REQUEST_MAPS);
                }else{
                    lintitude = 0;
                    longtitude = 0;
                }
                break;
            default:
                break;
        }
    }

    private void cancelTask(){
        if(!gpsTask.isCancelled()) {
            gpsTask.cancel(false);
        }
        bar.setVisibility(View.INVISIBLE);
    }

    private boolean SaveToDB(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy GGG HH:mm:ss aaa");
        String date = simpleDateFormat.format(calendar.getTime());
        String textTitle = editTextTitle.getText().toString();
        String textContent = editTextContent.getText().toString();
        if (checkBoxAuto.isChecked())
        {
            longtitude = gpsTask.getLongtitude();
            lintitude = gpsTask.getLintitude();
        }
        if(TextUtils.isEmpty(textTitle)){
            editTextTitle.setError(Constants.ERROR_TEXT_EMPTY);
            return false;
        }
        if (TextUtils.isEmpty(textContent)){
            editTextContent.setError(Constants.ERROR_TEXT_EMPTY);
            return false;
        }
        if (picturePath != null) {
            if ((image = saveImage(picturePath)) != null) {
                imageSmall = Constants.convertToSmallImage(image, Constants.SIZE_IMAGE_PREVIEW);
            }
        }
        dataBase.open_connection();
        if (bFlagCheckCreate){
            dataBase.addToDB(   textTitle,
                                textContent,
                                Constants.RADIO_SELECT_ID,
                                lintitude,
                                longtitude,
                                image,
                                imageSmall,
                                date);
        }else{
            dataBase.updateDB(  id_edit_note,
                                editTextTitle.getText().toString(),
                                editTextContent.getText().toString(),
                                Constants.RADIO_SELECT_ID,
                                lintitude,
                                longtitude,
                                image,
                                imageSmall,
                                date);
        }
        dataBase.close_connection();
        return true;

    }
    private void cleanAllForm(){

        EditText editView = findViewById(R.id.editTextTitle);
        editView.setText("");
        editView = findViewById(R.id.editTextContent);
        editView.setText("");
        checkBoxManual.setChecked(false);
        checkBoxManual.setSelected(false);
        checkBoxAuto.setChecked(false);
        checkBoxAuto.setSelected(false);


        radioGroup.clearCheck();
        Constants.RADIO_SELECT_ID = -100;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            sendResultWithClose();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void sendResultWithClose(){
        setResult(RESULT_OK);
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_UPDATE_MAIN, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private byte[] saveImage(String path) {
        if (TextUtils.isEmpty(path)){
            return null;
        }
        byte[] image = new byte[0];
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            image = new byte[fileInputStream.available()];
            fileInputStream.read(image);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
    * Метод получения картинки после выбора ее в галерее или же после фото
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage,
                            filePathColumn,
                            null,
                            null,
                            null);

                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    dialogImage.dismiss();
                    Constants.ToastMakeText(getBaseContext(), Constants.SUCCESS_IMAGE_SELECT);
                    break;
                }
            case Constants.REQUEST_IMAGE_CAPTURE:
                if (requestCode == Constants.REQUEST_IMAGE_CAPTURE &&
                        resultCode == RESULT_OK) {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor =
                            managedQuery(dialogInputImage.getmCapturedImageURI(), projection, null,
                                    null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(
                            MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    picturePath = cursor.getString(column_index_data);
                    dialogImage.dismiss();
                    Constants.ToastMakeText(getBaseContext(), Constants.SUCCESS_IMAGE_SELECT);
                }
                break;
            case Constants.REQUEST_MAPS:
                Intent getMaps = getIntent();
                lintitude = getMaps.getDoubleExtra(Constants.INTENT_MAPS_WITH_COORDINATES_LAT, 0);
                longtitude = getMaps.getDoubleExtra(Constants.INTENT_MAPS_WITH_COORDINATES_LONG, 0);
                break;
        }
    }
}

