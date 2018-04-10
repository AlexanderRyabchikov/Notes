package com.example.alexa.notes;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import helpers.async_tasks.Gps;
import helpers.constants.Constants;
import helpers.custom_class.CustomTextWatcher;
import helpers.custom_dialog.DialogInputImage;
import helpers.data_base.Notes;
import helpers.data_base.RoomDB;
import helpers.interfaces.IDataBaseApi;

public class CreateEdit_activity extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;
    private IDataBaseApi dataBase;
    private boolean bFlagCheckCreate = true;
    private int id_edit_note = -1;
    private EditText editTextTitle;
    private EditText editTextContent;
    private CheckBox checkBoxAuto;
    private double lintitude = 0xFFFF;
    private double longtitude = 0xFFFF;
    private CheckBox checkBoxManual;
    public static ImageButton saveButton;
    private RadioGroup radioGroup;
    private String picturePath = null;
    private ProgressBar bar;
    private byte[] image = null;
    private byte[] imageSmall = null;
    private Dialog dialogImage = null;
    private DialogInputImage dialogInputImage;
    private Gps gpsLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_activity);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.create_action_bar);
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
            Notes notes = dataBase.getEntry(positionId);

            if(notes != null){
                id_edit_note = notes._id;
                title = notes.titleDB;
                content = notes.contentDB;
                lintitude = notes.latitude;
                longtitude = notes.longtitude;
                image = notes.image;
                imageSmall = notes.imageSmall;
                radioButtonSelectId = notes.priority;
                editTextTitle.setText(title);
                editTextContent.setText(content);
                setCheckedRadioButton(radioButtonSelectId);
            }
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
        dataBase = new RoomDB();
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
                if (gpsLocation != null) {
                    gpsLocation.cancelFindLocation();
                    gpsLocation = null;
                }
                sendResultWithClose();
                break;
            case R.id.saveBt:
                if(SaveToDB()){
                    Constants.ToastMakeText(getBaseContext(), Constants.SUCCESS_MSG_DB, Constants.TYPE_MESSAGE_SUCCESS);
                    if(!bFlagCheckCreate) {
                        sendResultWithClose();
                    } else{
                        cleanAllForm();
                    }
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

                if (checkBoxAuto.isChecked()){
                    saveButton.setVisibility(View.INVISIBLE);
                    checkBoxManual.setChecked(false);
                    checkBoxManual.setSelected(false);
                    gpsLocation = new Gps(this, bar, saveButton);
                    gpsLocation.findLocation();

                }else{
                    lintitude = 0xFFFF;
                    longtitude = 0xFFFF;
                    gpsLocation.cancelFindLocation();
                    gpsLocation = null;
                }

                break;
            case R.id.gpsCheckedMaps:

                if (checkBoxManual.isChecked()){
                    bar.setVisibility(View.INVISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    checkBoxAuto.setChecked(false);
                    checkBoxAuto.setSelected(false);
                    if (gpsLocation != null){
                        gpsLocation.cancelFindLocation();
                        gpsLocation = null;
                    }
                    Intent intentMaps = new Intent(this, MapsActivity.class);
                    startActivityForResult(intentMaps, Constants.REQUEST_MAPS);
                    checkBoxManual.setChecked(false);
                }else{
                    lintitude = 0xFFFF;
                    longtitude = 0xFFFF;
                }
                break;
            default:
                break;
        }
    }

    private boolean SaveToDB(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy GGG HH:mm:ss aaa");
        String date = simpleDateFormat.format(calendar.getTime());
        String textTitle = editTextTitle.getText().toString();
        String textContent = editTextContent.getText().toString();
        if (checkBoxAuto.isChecked())
        {
            if (gpsLocation != null){
                longtitude = gpsLocation.getLongtitude();
                lintitude = gpsLocation.getLatitude();
            }
        }
        if(TextUtils.isEmpty(textTitle.trim())){
            editTextTitle.setError(Constants.ERROR_TEXT_EMPTY);
            if (TextUtils.isEmpty(textContent.trim())){
                editTextContent.setError(Constants.ERROR_TEXT_EMPTY);
                return false;
            }
            return false;
        }
        if (TextUtils.isEmpty(textContent.trim())){
            editTextContent.setError(Constants.ERROR_TEXT_EMPTY);
            if(TextUtils.isEmpty(textTitle.trim())){
                editTextTitle.setError(Constants.ERROR_TEXT_EMPTY);

                return false;
            }
            return false;
        }
        if (picturePath != null) {
            if ((image = saveImage(picturePath)) != null) {
                imageSmall = Constants.convertToSmallImage(image, Constants.SIZE_IMAGE_PREVIEW);
            }
        }
        dataBase.open_connection();
        if (bFlagCheckCreate){
            dataBase.addToDB(   textTitle.trim(),
                                textContent.trim(),
                                Constants.RADIO_SELECT_ID,
                                lintitude,
                                longtitude,
                                image,
                                imageSmall,
                                date);
        }else{
            dataBase.updateDB(  id_edit_note,
                                editTextTitle.getText().toString().trim(),
                                editTextContent.getText().toString().trim(),
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
        image = null;
        imageSmall = null;
        longtitude = 0xFFFF;
        lintitude = 0xFFFF;
        picturePath = null;

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
                    Constants.ToastMakeText(getBaseContext(), Constants.SUCCESS_IMAGE_SELECT, Constants.TYPE_MESSAGE_SUCCESS);
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
                    Constants.ToastMakeText(getBaseContext(), Constants.SUCCESS_IMAGE_SELECT, Constants.TYPE_MESSAGE_SUCCESS);
                }
                break;
            case Constants.REQUEST_MAPS:
                Bundle bundle = data.getExtras();
                assert bundle != null;
                checkBoxManual.setChecked(!bundle.getBoolean(Constants.INTENT_MAPS_CHEKCBOX_FLAG));
                lintitude = bundle.getDouble(Constants.INTENT_MAPS_WITH_COORDINATES_LAT);
                longtitude = bundle.getDouble(Constants.INTENT_MAPS_WITH_COORDINATES_LONG);
                if (lintitude == 0xFFFF || longtitude == 0xFFFF){
                    checkBoxManual.setChecked(false);
                }
                break;
        }
    }
}

