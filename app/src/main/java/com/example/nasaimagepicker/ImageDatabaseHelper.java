package com.example.nasaimagepicker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * ImageDatabaseHelper is a SQLiteOpenHelper subclass that manages the database
 * used for storing and retrieving saved images in the NASA Image Picker app.
 * This database ensures that each image's URL is unique.
 */
public class ImageDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "saved_images.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_IMAGES = "images";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DESCRIPTION = "description";

    /**
     * Constructs a new instance of ImageDatabaseHelper.
     *
     * @param context The context to use for locating paths to the database.
     */
    public ImageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * This method creates the images table with a unique constraint on the URL column.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_IMAGES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_URL + " TEXT UNIQUE, " +  // Ensuring URL is unique
                COLUMN_DATE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Inserts a new image record into the database.
     *
     * @param url         The URL of the image.
     * @param date        The date the image was saved.
     * @param description The description of the image.
     * @return true if the insertion was successful, false otherwise.
     */
    public boolean insertImage(String url, String date, String description) {
        if (imageExists(url)) {
            return false; // Image already exists in the database
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_URL, url);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_IMAGES, null, contentValues);
        db.close();
        return result != -1;
    }

    /**
     * Checks if an image with the given URL already exists in the database.
     *
     * @param url The URL of the image to check.
     * @return true if the image exists, false otherwise.
     */
    private boolean imageExists(String url) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_IMAGES, new String[]{COLUMN_ID},
                COLUMN_URL + "=?", new String[]{url}, null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * Deletes an image record from the database.
     *
     * @param id The ID of the image record to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteImage(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_IMAGES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    /**
     * Retrieves all saved image records from the database.
     *
     * @return A list of ImageItem objects representing the saved images.
     */
    public List<ImageItem> getAllImages() {
        List<ImageItem> imageItemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_IMAGES, null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int urlIndex = cursor.getColumnIndex(COLUMN_URL);
            int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

            while (cursor.moveToNext()) {
                if (idIndex != -1 && urlIndex != -1 && dateIndex != -1 && descriptionIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String url = cursor.getString(urlIndex);
                    String date = cursor.getString(dateIndex);
                    String description = cursor.getString(descriptionIndex);

                    ImageItem imageItem = new ImageItem(id, url, date, description, null);
                    imageItemList.add(imageItem);
                }
            }
            cursor.close();
        }
        db.close();
        return imageItemList;
    }
}
