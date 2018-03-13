package Helpers.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import Helpers.DataBase.Notes;

/**
 * Created by alexa on 13.03.2018.
 */
@Dao
public interface IRoomInterface {
    @Insert
    void insert(Notes note);

    @Delete
    void delete(Notes notes);

    @Query("SELECT * FROM Notes ORDER BY date")
    List<Notes> getAllNotes();


    @Query("SELECT * FROM Notes WHERE _id LIKE :id")
    Notes getNoteById(int id);

    @Update()
    void updateDB(Notes note);

}
