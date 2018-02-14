package com.example.alexa.notes;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private RadioGroup radioGroup;
    public static Context context;
    public static final String intentUpdateMain = "update_main";
    private static final String SuccessMsgDB = "Запись успешно сохранена";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_activity);
        getSupportActionBar().hide();

        dataBase = new DataBase(this);

        context = getBaseContext();
        intent = getIntent();

        GpsTask.bar = findViewById(R.id.progressBar);
        GpsTask.textView = findViewById(R.id.TextGps);
        GpsTask.textView.setVisibility(View.INVISIBLE);

        textViewTitle = findViewById(R.id.editTextTitle);
        textViewContent = findViewById(R.id.editTextContent);

        radioGroup = findViewById(R.id.radioGroup);

        checkBoxAuto = findViewById(R.id.gpsCheckedAuto);
        checkBoxManual = findViewById(R.id.gpsCheckedMaps);

        checkBoxManual.setOnClickListener(this);
        checkBoxAuto.setOnClickListener(this);

        GpsTask.locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Button cancelButton = findViewById(R.id.cancelBt);
        cancelButton.setOnClickListener(this);
        Button saveButton = findViewById(R.id.saveBt);
        saveButton.setOnClickListener(this);

        if(!intent.getBooleanExtra(MainActivity.intentCreateNote, false)){
            // Здесь заполнение данными если был вызван для правки
            bFlagCheckCreate = false;
            long positionId = intent.getLongExtra(MainActivity.intentEditNote, -1);
            String title;
            String content;
            int radioButtonId;
            dataBase.open_connection();
            Cursor cursor = dataBase.getEntry(positionId);

            if (cursor.moveToFirst()){
                do{
                    id_edit_note = cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_ID));
                    title = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_TITLE));
                    content = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_CONTENT));
                    radioButtonId = cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_PRIORITY));

                }while(cursor.moveToNext());

                textViewTitle.setText(title);
                textViewContent.setText(content);
                radioGroup.check(radioButtonId);

            }
            stopManagingCursor(cursor);
            cursor.close();
            dataBase.close_connection();

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancelBt:
                sendResultWithClose();

                break;
            case R.id.saveBt:
                SaveToDB();
                Toast.makeText(getBaseContext(), SuccessMsgDB, Toast.LENGTH_SHORT).show();
                cleanAllForm();
            case R.id.gpsCheckedAuto:
                if (checkBoxAuto.isChecked()){
                    checkBoxManual.setChecked(false);
                    checkBoxManual.setSelected(false);
                    new GpsTask().execute();
                }
                break;
            case R.id.gpsCheckedMaps:
                if (checkBoxManual.isChecked()){
                    checkBoxAuto.setChecked(false);
                    checkBoxAuto.setSelected(false);
                }
                break;
            default:
                break;
        }
    }

    private void SaveToDB(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy GGG hh:mm:ss aaa");
        String date = simpleDateFormat.format(calendar.getTime());
        dataBase.open_connection();
        if (bFlagCheckCreate){
            dataBase.addToDB(   textViewTitle.getText().toString(),
                                textViewContent.getText().toString(),
                                radioGroup.getCheckedRadioButtonId(),
                                date);
        }else{
            dataBase.updateDB(  id_edit_note,
                                textViewTitle.getText().toString(),
                                textViewContent.getText().toString(),
                                radioGroup.getCheckedRadioButtonId(),
                                date);
        }
        dataBase.close_connection();

    }
    private void cleanAllForm(){

        TextView textView = findViewById(R.id.editTextTitle);
        textView.setText("");
        textView = findViewById(R.id.editTextContent);
        textView.setText("");

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
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
        intent.putExtra(intentUpdateMain, true);
        setResult(RESULT_OK, intent);
        finish();
    }

}

