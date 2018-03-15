package helpers.custom_class;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexa.notes.R;

import java.util.List;

import helpers.constants.Constants;
import helpers.data_base.Notes;

/**
 * Created by alexander on 09.02.18.
 */

public class CustomAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Notes> notes;

    public CustomAdapter(Context context, List<Notes> notes){
        this.context = context;
        this.notes = notes;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Notes getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null){
            view = layoutInflater.inflate(R.layout.item_list_notes, parent, false);
        }
        Notes item = getNotes(position);
        TextView textTitle = view.findViewById(R.id.tvText);
        textTitle.setText(item.titleDB);
        switch (item.priority){
            case Constants.LOW_PRIORITY:
                textTitle.setBackgroundColor(Color.GREEN);
                break;
            case Constants.MEDIUM_PRIORITY:
                textTitle.setBackgroundColor(Color.YELLOW);
                break;
            case Constants.HIGH_PRIORITY:
                textTitle.setBackgroundColor(Color.RED);
                break;
            default:
                textTitle.setBackgroundResource(R.drawable.border_edit_text);
                break;
        }
        ImageView imageView = view.findViewById(R.id.ivImg);
        Bitmap bitmap = Constants.getImage(item.imageSmall);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageBitmap(null);
        }
        return view;
    }

    private Notes getNotes(int position){
        return getItem(position);
    }

}
