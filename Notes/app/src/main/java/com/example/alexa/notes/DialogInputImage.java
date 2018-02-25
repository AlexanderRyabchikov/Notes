package com.example.alexa.notes;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import java.util.Random;

/**
 * Created by alexa on 20.02.2018.
 */

public class DialogInputImage implements View.OnClickListener {
    private String title;
    private Context context;
    private Activity activity;


    public DialogInputImage(Context ctx, String title, Activity activity){
        this.context = ctx;
        this.title = title;
        this.activity = activity;
    }

    void createDialog(){

        C.dialogImage = new Dialog(context);
        C.dialogImage.setContentView(R.layout.add_image_dialog);
        C.dialogImage.setTitle(title);

        C.dialogImage.findViewById(R.id.btnExit).setOnClickListener(this);
        C.dialogImage.findViewById(R.id.btnChoosePath).setOnClickListener(this);
        C.dialogImage.findViewById(R.id.btnTakePhoto).setOnClickListener(this);

        C.dialogImage.show();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnChoosePath:
                activeGallery();
                break;
            case R.id.btnTakePhoto:
                activeTakePhoto();
                break;
            case R.id.btnExit:
                C.dialogImage.dismiss();
                break;
        }

    }

    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            String fileName = "notes_" + new Random().nextInt() + ".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            C.mCapturedImageURI = context.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
            takePictureIntent
                    .putExtra(MediaStore.EXTRA_OUTPUT, C.mCapturedImageURI);
            activity.startActivityForResult(takePictureIntent, C.REQUEST_IMAGE_CAPTURE);
        }
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, C.RESULT_LOAD_IMAGE);
    }
}
