package com.example.alexa.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by alexa on 11.02.2018.
 */

public class DialogInputFile {
    private Context context;
    private String Title;
    private String namePositiveButton;
    private String nameNegativeButton;

    public DialogInputFile(Context context,
                           String Title,
                           String namePositiveButton,
                           String nameNegativeButton){
        this.context = context;
        this.Title = Title;
        this.nameNegativeButton = nameNegativeButton;
        this.namePositiveButton = namePositiveButton;
    }

    public void createDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View dialogView = layoutInflater.inflate(R.layout.save_file_input_name, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setIcon(R.drawable.icon);
        alertDialogBuilder.setView(dialogView);
        final EditText userInput = dialogView
                .findViewById(R.id.et_input);


        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(namePositiveButton,
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // get user input and set it to etOutput
                                // edit text
                                saveToFile(userInput.getText().toString());
                            }
                        })
                .setNegativeButton(nameNegativeButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveToFile(String fileName) {
        if(isExternalStorageWritable()){
            String root = Environment.getExternalStorageDirectory().toString();
            File notesDir = new File(root + "/save_notes");
            notesDir.mkdirs();
            File file = new File(notesDir, fileName + ".txt");
            if(file.exists()){
                file.delete();
            }

            try(FileOutputStream outputStream = new FileOutputStream(file)){

                outputStream.write(PreviewNote.getTitlePreview().getBytes());
                outputStream.write("\n".getBytes());
                outputStream.write(PreviewNote.getContentPreview().getBytes());
                outputStream.flush();
                C.ToastMakeText(context, C.FILE_SAVE);
            } catch (IOException e) {
                C.ToastMakeText(context, C.FILE_NOT_SAVE);
            }
        }
        else{
            C.ToastMakeText(context, C.FLASH_ERROR);
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
