package com.example.alexa.notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Button createNote = findViewById(R.id.createNote);
        createNote.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.createNote:
                Intent intentCreateEdit = new Intent(this, CreateEdit_activity.class);
                startActivity(intentCreateEdit);
                break;
            default:
                break;
        }

    }
}
