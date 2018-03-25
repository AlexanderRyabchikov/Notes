package helpers.data_base;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import helpers.interfaces.IRoomInterface;

/**
 * Created by alexa on 13.03.2018.
 */
@Database(entities = {Notes.class}, version = 1)
public abstract class NotesDataBase extends RoomDatabase {
    public abstract IRoomInterface getIRoomInterface();
}
