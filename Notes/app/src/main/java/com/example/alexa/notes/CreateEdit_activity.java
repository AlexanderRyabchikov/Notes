package com.example.alexa.notes;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEdit_activity extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;
    private DataBase dataBase;
    private boolean bFlagCheckCreate = true;
    private int id_edit_note = -1;
    private TextView textViewTitle;
    private TextView textViewContent;
    private CheckBox checkBoxAuto;
    private CheckBox checkBoxManual;
    public static Button saveButton;
    private RadioGroup radioGroup;
    private String picturePath;
    private InputMethodManager inputMethodManager;
    private byte[] image;
    private byte[] imageSmall;

    private GpsTask gpsTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_activity);
        getSupportActionBar().hide();

        initActivity();

        if(!intent.getBooleanExtra(C.INTENT_CREATE_NOTE, false)){
            // Здесь заполнение данными если был вызван для правки
            bFlagCheckCreate = false;
            long positionId = intent.getLongExtra(C.INTENT_EDIT_NOTE, -1);
            String title;
            String content;
            dataBase.open_connection();
            Cursor cursor = dataBase.getEntry(positionId);

            if (cursor.moveToFirst()){
                do{
                    id_edit_note = cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_ID));
                    title = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_TITLE));
                    content = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_CONTENT));
                    C.lintitude = cursor.getDouble(cursor.getColumnIndex(DataBase.COLUMN_LINTITIDE));
                    C.longtitude = cursor.getDouble(cursor.getColumnIndex(DataBase.COLUMN_LONGTITUDE));
                    image = cursor.getBlob(cursor.getColumnIndex(DataBase.COLUMN_IMAGE));
                    imageSmall = cursor.getBlob(cursor.getColumnIndex(DataBase.COLUMN_IMAGE_SMALL));

                }while(cursor.moveToNext());

                textViewTitle.setText(title);
                textViewContent.setText(content);

            }
            stopManagingCursor(cursor);
            cursor.close();
            dataBase.close_connection();

        }
    }

    private void initActivity(){
        dataBase = new DataBase(this);
        C.context = getBaseContext();
        intent = getIntent();

        C.bar = findViewById(R.id.progressBar);
        textViewTitle = findViewById(R.id.editTextTitle);
        textViewContent = findViewById(R.id.editTextContent);
        radioGroup = findViewById(R.id.radioGroup);
        GetSelectedRadioButton();
        checkBoxAuto = findViewById(R.id.gpsCheckedAuto);
        checkBoxManual = findViewById(R.id.gpsCheckedMaps);
        saveButton = findViewById(R.id.saveBt);

        checkBoxManual.setOnClickListener(this);
        checkBoxAuto.setOnClickListener(this);
        findViewById(R.id.cancelBt).setOnClickListener(this);
        saveButton.setOnClickListener(this);
        findViewById(R.id.addImageButton).setOnClickListener(this);

        C.locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    private void GetSelectedRadioButton() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                   switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.lowPriority:
                            C.RADIO_SELECT_ID = C.LOW_PRIORITY;
                            break;
                        case R.id.mediumPriority:
                            C.RADIO_SELECT_ID = C.MEDIUM_PRIORITY;
                            break;
                        case R.id.highPriority:
                            C.RADIO_SELECT_ID = C.HIGH_PRIORITY;
                            break;
                        default:
                            C.RADIO_SELECT_ID = -100;
                            break;


                    }
                }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancelBt:
                sendResultWithClose();
                break;
            case R.id.saveBt:
                SaveToDB();
                C.ToastMakeText(getBaseContext(), C.SUCCESS_MSG_DB);
                cleanAllForm();
                break;
            case R.id.addImageButton:
                new DialogInputImage(this, C.TITLE_DIALOG_IMAGE, CreateEdit_activity.this).createDialog();
                break;
            case R.id.gpsCheckedAuto:
                inputMethodManager = (InputMethodManager)
                        CreateEdit_activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow
                        (CreateEdit_activity.this.getCurrentFocus().getWindowToken(), 0);

                if (checkBoxAuto.isChecked()){
                    saveButton.setEnabled(false);
                    checkBoxManual.setChecked(false);
                    checkBoxManual.setSelected(false);
                    gpsTask = new GpsTask();
                    gpsTask.execute();
                }else{
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
                    checkBoxAuto.setChecked(false);
                    checkBoxAuto.setSelected(false);
                    Intent intentMaps = new Intent(this, MapsActivity.class);
                    startActivityForResult(intentMaps, 11);
                }else{
                    if (gpsTask != null){
                        cancelTask();
                    }
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
        C.bar.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void SaveToDB(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy GGG hh:mm:ss aaa");
        String date = simpleDateFormat.format(calendar.getTime());
        image = saveImage(picturePath);
        imageSmall = C.convertToSmallImage(image, C.SIZE_IMAGE_PREVIEW);
        dataBase.open_connection();
        if (bFlagCheckCreate){
            dataBase.addToDB(   textViewTitle.getText().toString(),
                                textViewContent.getText().toString(),
                                C.RADIO_SELECT_ID,
                                C.lintitude,
                                C.longtitude,
                                image,
                                imageSmall,
                                date);
        }else{
            dataBase.updateDB(  id_edit_note,
                                textViewTitle.getText().toString(),
                                textViewContent.getText().toString(),
                                C.RADIO_SELECT_ID,
                                C.lintitude,
                                C.longtitude,
                                image,
                                imageSmall,
                                date);
        }
        dataBase.close_connection();

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
        C.RADIO_SELECT_ID = -100;
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
        intent.putExtra(C.INTENT_UPDATE_MAIN, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private byte[] saveImage(String path) {
        if (path == ""){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case C.RESULT_LOAD_IMAGE:
                if (requestCode == C.RESULT_LOAD_IMAGE &&
                        resultCode == RESULT_OK && null != data) {
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
                    C.dialogImage.dismiss();
                    break;
                }
            case C.REQUEST_IMAGE_CAPTURE:
                if (requestCode == C.REQUEST_IMAGE_CAPTURE &&
                        resultCode == RESULT_OK) {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor =
                            managedQuery(C.mCapturedImageURI, projection, null,
                                    null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(
                            MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    picturePath = cursor.getString(column_index_data);
                    C.dialogImage.dismiss();
                }
                break;
        }
    }
}

