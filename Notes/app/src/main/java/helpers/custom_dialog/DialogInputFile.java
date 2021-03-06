package helpers.custom_dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.alexa.notes.PreviewNote;
import com.example.alexa.notes.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import helpers.constants.Constants;

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

        @SuppressLint("InflateParams")
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
                        (dialog, id) -> {
                            // get user input and set it to etOutput
                            // edit text
                            String nameFile = userInput.getText().toString();
                            if (TextUtils.isEmpty(nameFile)){
                                nameFile = "defaultName";
                            }
                            saveToFile(nameFile);
                        })
                .setNegativeButton(nameNegativeButton,
                        (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

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
                Constants.ToastMakeText(context, Constants.FILE_SAVE + file.getAbsolutePath(), Constants.TYPE_MESSAGE_NON);
            } catch (IOException e) {
                Constants.ToastMakeText(context, Constants.FILE_NOT_SAVE, Constants.TYPE_MESSAGE_ERROR);
            }
        }
        else{
            Constants.ToastMakeText(context, Constants.FLASH_ERROR, Constants.TYPE_MESSAGE_ERROR);
        }
    }

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
