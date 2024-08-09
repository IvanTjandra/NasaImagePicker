package com.example.nasaimagepicker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * HistoryDatabaseHelper is a SQLiteOpenHelper subclass that manages the database
 * used for storing and retrieving the history of accessed images in the NASA Image Picker app.
 */
public class HistoryDatabaseHelper extends SQLiteOpenHelper {

    /**
     * The name of the database.
     */
    private static final String DATABASE_NAME = "history.db";

    /**
     * The version of the database.
     */
    private static final int DATABASE_VERSION = 1;  // Only one version

    /**
     * The name of the table that stores the history records.
     */
    private static final String TABLE_HISTORY = "history";

    /**
     * The name of the column that stores the unique ID for each history record.
     */
    private static final String COLUMN_ID = "_id";

    /**
     * The name of the column that stores the URL of the image.
     */
    private static final String COLUMN_URL = "url";

    /**
     * The name of the column that stores the date the image was accessed.
     */
    private static final String COLUMN_DATE = "date";

    /**
     * The name of the column that stores the description of the image.
     */
    private static final String COLUMN_DESCRIPTION = "description";

    /**
     * The name of the column that stores the date and time when the image was accessed.
     */
    private static final String COLUMN_DATE_ACCESSED = "date_accessed";

    /**
     * Constructs a new instance of HistoryDatabaseHelper.
     *
     * @param context The context to use for locating paths to the database.
     */
    public HistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_HISTORY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_URL + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DATE_ACCESSED + " TEXT)";
        db.execSQL(createTable);
    }

    /**
     * This method is not needed for a single-version database.
     * If you plan to add versions later, you would implement upgrade logic here.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrade logic is needed since we only have one version of the database schema.
    }

    /**
     * Inserts a new history record into the database.
     *
     * @param url           The URL of the image.
     * @param date          The date the image was accessed.
     * @param description   The description of the image.
     * @param dateAccessed  The date and time the image was accessed.
     * @return true if the insertion was successful, false otherwise.
     */
    public boolean insertHistory(String url, String date, String description, String dateAccessed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_URL, url);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_DATE_ACCESSED, dateAccessed);

        long result = db.insert(TABLE_HISTORY, null, contentValues);
        db.close();
        return result != -1;
    }

    /**
     * Deletes a history record from the database.
     *
     * @param id The ID of the history record to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteHistory(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_HISTORY, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    /**
     * Retrieves all history records from the database.
     *
     * @return A list of ImageItem objects representing the history records.
     */
    public List<ImageItem> getAllHistory() {
        List<ImageItem> historyItemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int urlIndex = cursor.getColumnIndex(COLUMN_URL);
            int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int dateAccessedIndex = cursor.getColumnIndex(COLUMN_DATE_ACCESSED);

            while (cursor.moveToNext()) {
                if (idIndex != -1 && urlIndex != -1 && dateIndex != -1 && descriptionIndex != -1 && dateAccessedIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String url = cursor.getString(urlIndex);
                    String date = cursor.getString(dateIndex);
                    String description = cursor.getString(descriptionIndex);
                    String dateAccessed = cursor.getString(dateAccessedIndex);

                    ImageItem historyItem = new ImageItem(id, url, date, description, dateAccessed);
                    historyItemList.add(historyItem);
                }
            }
            cursor.close();
        }
        db.close();
        return historyItemList;
    }
}
