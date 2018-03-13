package com.example.alexa.notes;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import Helpers.Constants.Constants;
import Helpers.DataBase.DataBase;
import Helpers.CustomDialog.DialogInputFile;
import Helpers.Interfaces.IDataBaseApi;

public class PreviewNote extends Activity implements View.OnClickListener {

    private IDataBaseApi dataBase;
    private Intent intent;
    private Cursor cursor;

    public static String getTitlePreview() {
        return title;
    }

    public static String getContentPreview() {
        return content;
    }

    private static String title;
    private static Bitmap btm;
    private static String content;
    private long positionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_note);
        Constants.setupLocale();
        PreviewActivity_create();

    }

    private void PreviewActivity_create(){
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayPreView);
        findViewById(R.id.backBt).setOnClickListener(this);
        TextView textViewTitle = findViewById(R.id.TextPreView);
        TextView textViewContent = findViewById(R.id.TextContent);
        ImageView imageView = findViewById(R.id.imageView);

        intent = getIntent();
        positionId = intent.getLongExtra(Constants.INTENT_PREVIEW_NOTE, -1);

        dataBase = new DataBase(this);
        dataBase.open_connection();
        cursor = dataBase.getEntry(positionId);

        if (cursor.moveToFirst()){
            do{
                title = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_TITLE));
                content = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_CONTENT));
                btm = Constants.getImage(cursor.getBlob(cursor.getColumnIndex(DataBase.COLUMN_IMAGE)));


            }while(cursor.moveToNext());
        }

        textViewTitle.setText(title);
        textViewContent.setText(content);
        if(btm != null) {
            imageView.setImageBitmap(btm);
        }
        registerForContextMenu(relativeLayout);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backBt:
                sendResultWithClose();
                break;
            default:
                break;
        }
    }
    private void sendResultWithClose(){

        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_UPDATE_MAIN, true);
        intent.putExtra(Constants.map, true);
        setResult(RESULT_OK, intent);
        dataBase.close_connection();
        stopManagingCursor(cursor);
        cursor.close();
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
        contextMenu.add(0, Constants.CM_EDIT_ID, 0, R.string.edit_menu);
        contextMenu.add(0, Constants.CM_DELETE_ID, 0, R.string.delete_menu);
        contextMenu.add(0, Constants.SAVE_TO_FILE, 0, R.string.save_to_file);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case Constants.CM_DELETE_ID:
                dataBase.deleteDB(positionId);
                cursor.requery();
                Constants.ToastMakeText(getBaseContext(), Constants.DELETE_SUCCESS_MSG);
                sendResultWithClose();
                break;
            case Constants.CM_EDIT_ID:
                Intent intentCreateEdit = new Intent(this, CreateEdit_activity.class);
                intentCreateEdit.putExtra(Constants.INTENT_CREATE_NOTE, false);
                intentCreateEdit.putExtra(Constants.INTENT_EDIT_NOTE, positionId);
                startActivityForResult(intentCreateEdit, 1);
                break;
            case Constants.SAVE_TO_FILE:
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
                        Constants.TITLE_DIALOG_SAVE_FILE,
                        Constants.NAME_POSITIVE_BUTTON,
                        Constants.NAME_NEGATIVE_BUTTON);
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
    protected void onResume(){
        super.onResume();
        PreviewActivity_create();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        boolean isUpdate = data.getBooleanExtra(Constants.INTENT_UPDATE_MAIN, false);
        if (isUpdate){
            this.recreate();
        }

    }
}
