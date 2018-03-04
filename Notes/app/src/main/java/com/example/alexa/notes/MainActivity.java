package com.example.alexa.notes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import Helpers.Constants.C;
import Helpers.CustomClass.CustomCursorAdapter;
import Helpers.DataBase.DataBase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private DataBase dataBase;
    private Cursor cursor;

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
                Intent intentCreateEdit = new Intent(this, CreateEdit_activity.class);
                intentCreateEdit.putExtra(C.INTENT_CREATE_NOTE, true);
                startActivityForResult(intentCreateEdit, 1);
                break;
            case R.id.runMap:
                Intent intentMaps = new Intent(this, MapsActivity.class);
                intentMaps.putExtra(C.map, true);
                startActivityForResult(intentMaps, 10);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        boolean isUpdate = data.getBooleanExtra(C.INTENT_UPDATE_MAIN, false);
        if (isUpdate) {
            this.recreate();
        }

    }

    public void onCreateContextMenu(ContextMenu contextMenu,
                                    View view,
                                    ContextMenu.ContextMenuInfo contextMenuInfo){
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        contextMenu.add(0, C.CM_EDIT_ID, 0, R.string.edit_menu);
        contextMenu.add(0, C.CM_DELETE_ID, 0, R.string.delete_menu);
    }
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case C.CM_DELETE_ID:
                AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.deleteDB(adapterContextMenuInfo.id);

                cursor.requery();
                this.recreate();
                C.ToastMakeText(getBaseContext(), C.DELETE_SUCCESS_MSG);
                break;
            case C.CM_EDIT_ID:
                AdapterView.AdapterContextMenuInfo adapterContextMenuInfoEdit =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Intent intentCreateEdit = new Intent(this, CreateEdit_activity.class);
                intentCreateEdit.putExtra(C.INTENT_CREATE_NOTE, false);
                intentCreateEdit.putExtra(C.INTENT_EDIT_NOTE, adapterContextMenuInfoEdit.id);
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
        cursor.moveToPosition(position);
        long positionId = cursor.getLong(cursor.getColumnIndex(DataBase.COLUMN_ID));
        Intent intentPreviewNote = new Intent(this, PreviewNote.class);
        intentPreviewNote.putExtra(C.INTENT_PREVIEW_NOTE, positionId);
        startActivityForResult(intentPreviewNote, 2);
    }
    @Override
    protected void onResume(){
        super.onResume();
        MainActivity_create();
    }


    private void MainActivity_create() {
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        C.setupLocale();
        findViewById(R.id.createNote).setOnClickListener(this);
        findViewById(R.id.runMap).setOnClickListener(this);
        ListView listView = findViewById(R.id.lvData);
        listView.setOnItemClickListener(this);

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
}
