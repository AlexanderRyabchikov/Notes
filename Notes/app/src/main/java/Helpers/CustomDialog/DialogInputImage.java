package Helpers.CustomDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;

import com.example.alexa.notes.R;

import java.util.Random;

import Helpers.Constants.Constants;

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

    public void createDialog(){

        Constants.dialogImage = new Dialog(context);
        Constants.dialogImage.setContentView(R.layout.add_image_dialog);
        Constants.dialogImage.setTitle(title);

        Constants.dialogImage.findViewById(R.id.btnExit).setOnClickListener(this);
        Constants.dialogImage.findViewById(R.id.btnChoosePath).setOnClickListener(this);
        Constants.dialogImage.findViewById(R.id.btnTakePhoto).setOnClickListener(this);

        Constants.dialogImage.show();


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
                Constants.dialogImage.dismiss();
                break;
        }

    }

    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            String fileName = "notes_" + new Random().nextInt() + ".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            Constants.mCapturedImageURI = context.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
            takePictureIntent
                    .putExtra(MediaStore.EXTRA_OUTPUT, Constants.mCapturedImageURI);
            activity.startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
        }
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, Constants.RESULT_LOAD_IMAGE);
    }
}
