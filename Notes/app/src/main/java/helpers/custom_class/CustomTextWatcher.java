package helpers.custom_class;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by alexa on 04.03.2018.
 */

public class CustomTextWatcher implements TextWatcher {
    private TextView editText;

    public CustomTextWatcher(TextView editText){

        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        editText.setError(null);
    }
}
