package com.example.alexa.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PreviewNote extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_note);
        getSupportActionBar().hide();
    }
}
