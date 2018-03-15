package helpers.data_base;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by alexa on 13.03.2018.
 */
@Entity
public class Notes {
    public @PrimaryKey(autoGenerate = true) int _id;
    public String titleDB;
    public String contentDB;
    public int priority;
    public double latitude;
    public double longtitude;
    public byte[] image;
    public byte[] imageSmall;
    public String date;
}
