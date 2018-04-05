package com.example.alexa.notes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import helpers.constants.Constants;
import helpers.custom_class.CustomAdapter;
import helpers.data_base.Notes;
import helpers.data_base.RoomDB;
import helpers.interfaces.IDataBaseApi;

public class MainActivity extends Activity implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private IDataBaseApi dataBase;
    private List<Notes>notes;
    CustomAdapter customAdapter;
    ListView listView;
    @SuppressLint({"WrongConstant", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.PERMISSION_REQUEST_CAMERA);
            }
        }
        MainActivity_create();

    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.createNote:
                Intent intentCreateEdit = new Intent(this, CreateEdit_activity.class);
                intentCreateEdit.putExtra(Constants.INTENT_CREATE_NOTE, true);
                startActivityForResult(intentCreateEdit, 1);
                break;
            case R.id.runMap:
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

                customAdapter.remove(adapterContextMenuInfo.position);
                customAdapter.notifyDataSetChanged();
                dataBase.deleteDB(adapterContextMenuInfo.id);
                Constants.ToastMakeText(getBaseContext(), Constants.DELETE_SUCCESS_MSG, Constants.TYPE_MESSAGE_SUCCESS);
                if(customAdapter.isEmpty()){
                    messageEmptyNotes();
                }
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
        dataBase.close_connection();
    }

    @Override
    protected void onStop(){
        super.onStop();
        dataBase.close_connection();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        view.setEnabled(false);
        long positionId = notes.get(position)._id;
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
        listView = findViewById(R.id.lvData);
        listView.setOnItemClickListener(this);
        dataBase = new RoomDB();
        dataBase.open_connection();

        notes = dataBase.getEntries();
        if(notes.isEmpty()){
            messageEmptyNotes();
        } else {
            customAdapter = new CustomAdapter(this, notes);
            listView.setAdapter(customAdapter);
            registerForContextMenu(listView);
        }
    }

    private void messageEmptyNotes() {
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayMain);
        TextView textView = new TextView(this);
        textView.setText(R.string.text_not_found_entry);
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        textView.setTextSize(20);
        textView.setLayoutParams(relativeLayout.getLayoutParams());
        relativeLayout.addView(textView);
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
