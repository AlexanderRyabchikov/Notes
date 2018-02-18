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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final int CM_DELETE_ID = 1;
    public static final int CM_EDIT_ID = 2;
    public static final String intentCreateNote = "Create_note";
    public static final String intentEditNote = "edit_note";
    public static final String intentPreviewNote = "Preview_note";
    public static final String DeleteSuccessMsg = "Запись успешно удалена";
    ListView listView;
    DataBase dataBase;
    CustomCursorAdapter simpleCursorAdapter;
    Cursor cursor;
    @SuppressLint({"WrongConstant", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Button createNote = findViewById(R.id.createNote);
        createNote.setOnClickListener(this);
        Button mapButton = findViewById(R.id.runMap);
        mapButton.setOnClickListener(this);

        dataBase = new DataBase(this);
        dataBase.open_connection();

        cursor = dataBase.getEntries();
        if (cursor.getCount() <= 0){
            RelativeLayout relativeLayout = findViewById(R.id.relativeLayMain);
            TextView textView = new TextView(this);
            textView.setText(R.string.text_not_found_entry);
            textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            textView.setTextSize(20);
            textView.setLayoutParams(relativeLayout.getLayoutParams());
            relativeLayout.addView(textView);
        }
        else{
            startManagingCursor(cursor);
            String[] from = new String[] {DataBase.COLUMN_TITLE};
            int[] to = new int[] {R.id.tvText};
            simpleCursorAdapter = new CustomCursorAdapter(this,
                                                          R.layout.item_list_notes,
                                                          cursor,
                                                          from,
                                                          to);

            listView = findViewById(R.id.lvData);
            listView.setAdapter(simpleCursorAdapter);
            listView.setOnItemClickListener(this);

            registerForContextMenu(listView);
        }
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.createNote:
                Intent intentCreateEdit = new Intent(this, CreateEdit_activity.class);
                intentCreateEdit.putExtra(intentCreateNote, true);
                startActivityForResult(intentCreateEdit, 1);
                break;
            case R.id.runMap:
                Intent intentMaps = new Intent(this, MapsActivity.class);
                startActivityForResult(intentMaps, 10);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        boolean isUpdate = data.getBooleanExtra(CreateEdit_activity.intentUpdateMain, false);
        if (isUpdate){
            this.recreate();
        }

    }

    public void onCreateContextMenu(ContextMenu contextMenu,
                                    View view,
                                    ContextMenu.ContextMenuInfo contextMenuInfo){
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        contextMenu.add(0, CM_EDIT_ID, 0, R.string.edit_menu);
        contextMenu.add(0, CM_DELETE_ID, 0, R.string.delete_menu);
    }
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case CM_DELETE_ID:
                AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.deleteDB(adapterContextMenuInfo.id);

                cursor.requery();
                this.recreate();
                Toast.makeText(getBaseContext(), DeleteSuccessMsg, Toast.LENGTH_SHORT).show();
                break;
            case CM_EDIT_ID:
                AdapterView.AdapterContextMenuInfo adapterContextMenuInfoEdit =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Intent intentCreateEdit = new Intent(this, CreateEdit_activity.class);
                intentCreateEdit.putExtra(intentCreateNote, false);
                intentCreateEdit.putExtra(intentEditNote, adapterContextMenuInfoEdit.id);
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
        intentPreviewNote.putExtra(MainActivity.intentPreviewNote, positionId);
        startActivityForResult(intentPreviewNote, 2);
    }
}
