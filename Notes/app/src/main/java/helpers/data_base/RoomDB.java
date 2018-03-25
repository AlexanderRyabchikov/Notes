package helpers.data_base;

import java.util.List;

import helpers.interfaces.IDataBaseApi;
import helpers.interfaces.IRoomInterface;

/**
 * Created by alexa on 13.03.2018.
 */

public class RoomDB implements IDataBaseApi {
    IRoomInterface iRoomInterface;
    NotesDataBase notesDataBase;
    @Override
    public void open_connection() {
        notesDataBase = AppDataBaseSingleton.getInstance().getDataBase();
        iRoomInterface = notesDataBase.getIRoomInterface();
    }

    @Override
    public void close_connection() {
        notesDataBase = null;
        iRoomInterface = null;
    }

    @Override
    public void addToDB(String title,
                        String content,
                        int priority,
                        double lintitude,
                        double longtitude,
                        byte[] image,
                        byte[] image_small,
                        String date) {
        Notes notes = new Notes();
        notes.titleDB = title;
        notes.contentDB = content;
        notes.priority = priority;
        notes.latitude = lintitude;
        notes.longtitude = longtitude;
        notes.image = image;
        notes.imageSmall = image_small;
        notes.date = date;
        iRoomInterface.insert(notes);

    }

    @Override
    public void updateDB(int id,
                         String title,
                         String content,
                         int priority,
                         double lintitude,
                         double longtitude,
                         byte[] image,
                         byte[] image_small,
                         String date) {

        Notes notes = new Notes();
        notes._id = id;
        notes.titleDB = title;
        notes.contentDB = content;
        notes.priority = priority;
        notes.latitude = lintitude;
        notes.longtitude = longtitude;
        notes.image = image;
        notes.imageSmall = image_small;
        notes.date = date;
        iRoomInterface.updateDB(notes);
    }

    @Override
    public void deleteDB(long id) {
        Notes notes = new Notes();
        notes._id = (int)id;
        iRoomInterface.delete(notes);
    }

    @Override
    public Notes getEntry(long id) {
        return iRoomInterface.getNoteById((int)id);
    }

    @Override
    public List<Notes> getEntries() {
        return iRoomInterface.getAllNotes();
    }
}
