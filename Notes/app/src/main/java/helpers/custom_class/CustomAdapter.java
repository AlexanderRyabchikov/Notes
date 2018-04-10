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
        return notes.get(position)._id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null){
            view = layoutInflater.inflate(R.layout.item_list_notes, parent, false);
        }
        Notes item = getNotes(position);
        View viewLine = view.findViewById(R.id.viewItemBinder);
        TextView textTitle = view.findViewById(R.id.tvText);
        textTitle.setText(item.titleDB);
        switch (item.priority){
            case Constants.LOW_PRIORITY:
                viewLine.setBackgroundResource(R.drawable.separator_low_priority);
                break;
            case Constants.MEDIUM_PRIORITY:
                viewLine.setBackgroundResource(R.drawable.separator_medium_priority);
                break;
            case Constants.HIGH_PRIORITY:
                viewLine.setBackgroundResource(R.drawable.separator_high_priority);
                break;
            default:
                viewLine.setBackgroundResource(R.drawable.separator);
                break;
        }
        ImageView imageView = view.findViewById(R.id.ivImg);
        Bitmap bitmap = Constants.getImage(item.imageSmall);
        if(bitmap != null){
            imageView.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.imageview_height);
            imageView.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.imageview_width);
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.getLayoutParams().height = 0;
            imageView.getLayoutParams().width = 0;
            imageView.setImageBitmap(null);
        }
        return view;
    }

    private Notes getNotes(int position){
        return getItem(position);
    }

    public void remove(long position){
        notes.remove(getItem((int) position));
    }

}
