package helpers.data_base;

import android.app.Application;
import android.arch.persistence.room.Room;

/**
 * Created by alexa on 13.03.2018.
 */

public class AppDataBaseSingleton extends Application {
    public static AppDataBaseSingleton instance;
    private NotesDataBase database;
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        database = Room
                .databaseBuilder(this, NotesDataBase.class, "database")
                .allowMainThreadQueries()
                .build();
    }

    public static AppDataBaseSingleton getInstance(){
        return instance;
    }
    public NotesDataBase getDataBase(){
        return database;
    }
}
