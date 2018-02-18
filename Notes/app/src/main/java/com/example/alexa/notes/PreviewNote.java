package com.example.alexa.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PreviewNote extends AppCompatActivity implements View.OnClickListener {

    private DataBase dataBase;
    private Intent intent;
    private Cursor cursor;

    public static String getTitleNote() {
        return title;
    }

    public static String getContentNote() {
        return content;
    }

    private static String title;
    private static String gps;
    private static String content;
    private long positionId;
    private static final int SAVE_TO_FILE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_note);
        getSupportActionBar().hide();

        RelativeLayout relativeLayout = findViewById(R.id.relativeLayPreView);
        Button backButton = findViewById(R.id.backBt);
        backButton.setOnClickListener(this);
        TextView textViewTitle = findViewById(R.id.TextPreView);
        TextView textViewContent = findViewById(R.id.TextContent);
        TextView textViewGps = findViewById(R.id.placeGsp);

        intent = getIntent();
        positionId = intent.getLongExtra(MainActivity.intentPreviewNote, -1);

        dataBase = new DataBase(this);
        dataBase.open_connection();
        cursor = dataBase.getEntry(positionId);

        if (cursor.moveToFirst()){
            do{
                title = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_TITLE));
                content = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_CONTENT));
                gps = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_GSP));

            }while(cursor.moveToNext());
        }

        textViewTitle.setText(title);
        textViewContent.setText(content);
        textViewGps.setText(gps);
        registerForContextMenu(relativeLayout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backBt:
                sendResultWithClose();
            default:
                break;
        }
    }
    private void sendResultWithClose(){
        setResult(RESULT_OK);
        Intent intent = new Intent();
        intent.putExtra(CreateEdit_activity.intentUpdateMain, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            sendResultWithClose();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onCreateContextMenu(ContextMenu contextMenu,
                                    View view,
                                    ContextMenu.ContextMenuInfo contextMenuInfo){
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        contextMenu.add(0, MainActivity.CM_EDIT_ID, 0, R.string.edit_menu);
        contextMenu.add(0, MainActivity.CM_DELETE_ID, 0, R.string.delete_menu);
        contextMenu.add(0, SAVE_TO_FILE, 0, R.string.save_to_file);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case MainActivity.CM_DELETE_ID:
                dataBase.deleteDB(positionId);
                cursor.requery();
                Toast.makeText(getBaseContext(), MainActivity.DeleteSuccessMsg, Toast.LENGTH_SHORT).show();
                sendResultWithClose();
                break;
            case MainActivity.CM_EDIT_ID:
                Intent intentCreateEdit = new Intent(this, CreateEdit_activity.class);
                intentCreateEdit.putExtra(MainActivity.intentCreateNote, false);
                intentCreateEdit.putExtra(MainActivity.intentEditNote, positionId);
                startActivityForResult(intentCreateEdit, 1);
                break;
            case SAVE_TO_FILE:
                save_to_file();
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void save_to_file() {

        DialogInputFile dialogInputFile =
                new DialogInputFile(this,
                "Name file to save...",
                        "OK",
                        "Cancel");
        dialogInputFile.createDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopManagingCursor(cursor);
        cursor.close();
        dataBase.close_connection();
    }

    @Override
    protected void onStop(){
        super.onStop();
        stopManagingCursor(cursor);
        cursor.close();
        dataBase.close_connection();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        boolean isUpdate = data.getBooleanExtra(CreateEdit_activity.intentUpdateMain, false);
        if (isUpdate){
            this.recreate();
        }

    }
}
