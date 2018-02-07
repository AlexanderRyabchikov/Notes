package com.example.alexa.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.textclassifier.TextClassifier;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
    ListView listView;
    TextView textView;
    DataBase dataBase;
    SimpleCursorAdapter simpleCursorAdapter;
    Cursor cursor;
    @SuppressLint({"WrongConstant", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Button createNote = findViewById(R.id.createNote);
        createNote.setOnClickListener(this);

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
            simpleCursorAdapter = new SimpleCursorAdapter(this,
                                                          R.layout.item_list_notes,
                                                          cursor,
                                                          from,
                                                          to);

            listView = findViewById(R.id.lvData);
            listView.setAdapter(simpleCursorAdapter);

            registerForContextMenu(listView);
        }
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
                break;
            case CM_EDIT_ID:
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
}
