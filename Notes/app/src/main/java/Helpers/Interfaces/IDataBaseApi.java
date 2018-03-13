package Helpers.Interfaces;

/**
 * Created by alexa on 13.03.2018.
 */

public interface IDataBaseApi {
    void open_connection();
    void close_connection();
    void addToDB(String title,
                 String content,
                 int priority,
                 double lintitude,
                 double longtitude,
                 byte[] image,
                 byte[] image_small,
                 String date);
    void updateDB(int id,
                  String title,
                  String content,
                  int priority,
                  double lintitude,
                  double longtitude,
                  byte[] image,
                  byte[] image_small,
                  String date);

    void deleteDB(long id);

    <T> T getEntry(long id);
    <T> T getEntries();
}
