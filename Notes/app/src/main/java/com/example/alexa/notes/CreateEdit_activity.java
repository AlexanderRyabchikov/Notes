package com.example.alexa.notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    public static final String intentUpdateMain = "update_main";
    private static final String SuccessMsgDB = "Запись успешно сохранена";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_activity);
        getSupportActionBar().hide();

        intent = getIntent();

        Button cancelButton = findViewById(R.id.cancelBt);
        cancelButton.setOnClickListener(this);
        Button saveButton = findViewById(R.id.saveBt);
        saveButton.setOnClickListener(this);

        if(!intent.getBooleanExtra(MainActivity.intentCreateNote, false)){
            // Здесь заполнение данными если был вызван для правки
            bFlagCheckCreate = false;
            //id_edit_note
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancelBt:
                setResult(RESULT_OK);
                Intent intent = new Intent();
                intent.putExtra(intentUpdateMain, true);
                setResult(RESULT_OK, intent);
                finish();

                break;
            case R.id.saveBt:
                SaveToDB();
                Toast.makeText(getBaseContext(), SuccessMsgDB, Toast.LENGTH_SHORT).show();
                cleanAllForm();
            default:
                break;
        }
    }

    private void SaveToDB(){

        TextView textViewTitle = findViewById(R.id.editTextTitle);
        TextView textViewContent = findViewById(R.id.editTextContent);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy GGG hh:mm aaa");
        String date = simpleDateFormat.format(calendar.getTime());

        dataBase = new DataBase(this);
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
}
