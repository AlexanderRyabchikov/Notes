package com.example.alexa.notes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import Helpers.Constants.Constants;
import Helpers.CustomClass.CustomCursorAdapter;
import Helpers.DataBase.DataBase;
import Helpers.Interfaces.IDataBaseApi;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private IDataBaseApi dataBase;
    private Cursor cursor;
    private static Animation imageButtonAnim = null;
    @SuppressLint({"WrongConstant", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity_create();

    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.createNote:
                view.startAnimation(imageButtonAnim);
                Intent intentCreateEdit = new Intent(this, CreateEdit_activity.class);
                intentCreateEdit.putExtra(Constants.INTENT_CREATE_NOTE, true);
                startActivityForResult(intentCreateEdit, 1);
                break;
            case R.id.runMap:
                view.startAnimation(imageButtonAnim);
                Intent intentMaps = new Intent(this, MapsActivity.class);
                intentMaps.putExtra(Constants.map, true);
                startActivityForResult(intentMaps, 10);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        boolean isUpdate = data.getBooleanExtra(Constants.INTENT_UPDATE_MAIN, false);
        if (isUpdate) {
            this.recreate();
        }

    }

    public void onCreateContextMenu(ContextMenu contextMenu,
                                    View view,
                                    ContextMenu.ContextMenuInfo contextMenuInfo){
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        contextMenu.add(0, Constants.CM_EDIT_ID, 0, R.string.edit_menu);
        contextMenu.add(0, Constants.CM_DELETE_ID, 0, R.string.delete_menu);
    }
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case Constants.CM_DELETE_ID:
                AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.deleteDB(adapterContextMenuInfo.id);

                cursor.requery();
                this.recreate();
                Constants.ToastMakeText(getBaseContext(), Constants.DELETE_SUCCESS_MSG);
                break;
            case Constants.CM_EDIT_ID:
                AdapterView.AdapterContextMenuInfo adapterContextMenuInfoEdit =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Intent intentCreateEdit = new Intent(this, CreateEdit_activity.class);
                intentCreateEdit.putExtra(Constants.INTENT_CREATE_NOTE, false);
                intentCreateEdit.putExtra(Constants.INTENT_EDIT_NOTE, adapterContextMenuInfoEdit.id);
                startActivityForResult(intentCreateEdit, 1);
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        view.setEnabled(false);
        cursor.moveToPosition(position);
        long positionId = cursor.getLong(cursor.getColumnIndex(DataBase.COLUMN_ID));
        Intent intentPreviewNote = new Intent(this, PreviewNote.class);
        intentPreviewNote.putExtra(Constants.INTENT_PREVIEW_NOTE, positionId);
        startActivityForResult(intentPreviewNote, 2);
    }
    @Override
    protected void onResume(){
        super.onResume();
        MainActivity_create();
    }


    private void MainActivity_create() {
        setContentView(R.layout.activity_main);
        Constants.setupLocale();
        findViewById(R.id.createNote).setOnClickListener(this);
        findViewById(R.id.runMap).setOnClickListener(this);
        ListView listView = findViewById(R.id.lvData);
        listView.setOnItemClickListener(this);
        imageButtonAnim = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        dataBase = new DataBase(this);
        dataBase.open_connection();

        cursor = dataBase.getEntries();
        if (cursor.getCount() <= 0) {
            RelativeLayout relativeLayout = findViewById(R.id.relativeLayMain);
            TextView textView = new TextView(this);
            textView.setText(R.string.text_not_found_entry);
            textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            textView.setTextSize(20);
            textView.setLayoutParams(relativeLayout.getLayoutParams());
            relativeLayout.addView(textView);
        } else {
            startManagingCursor(cursor);
            String[] from = new String[]{DataBase.COLUMN_IMAGE, DataBase.COLUMN_TITLE};
            int[] to = new int[]{R.id.ivImg, R.id.tvText};
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,
                    R.layout.item_list_notes,
                    cursor,
                    from,
                    to);
            simpleCursorAdapter.setViewBinder(new CustomCursorAdapter());


            listView.setAdapter(simpleCursorAdapter);
            registerForContextMenu(listView);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
