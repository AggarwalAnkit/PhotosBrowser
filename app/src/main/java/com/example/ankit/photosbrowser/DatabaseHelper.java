package com.example.ankit.photosbrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PhotosDB";

    // Contacts table name
    private static final String TABLE_PHOTOS = "photos";

    // Contacts Table Columns names
    private static final String KEY_PATH = "path";
    private static final String KEY_ORIENTATION = "orientation";
    private static final String KEY_DATE_ADDED = "dateAdded";

    private static DatabaseHelper databaseHelperInstance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if(databaseHelperInstance == null) {
            databaseHelperInstance = new DatabaseHelper(context);
        }
        return databaseHelperInstance;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PHOTOS_TABLE = "CREATE TABLE " + TABLE_PHOTOS + "("
                + KEY_PATH + " TEXT PRIMARY KEY," + KEY_ORIENTATION + " INTEGER,"
                + KEY_DATE_ADDED + " INTEGER" + ")";
        db.execSQL(CREATE_PHOTOS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);

        // Create tables again
        onCreate(db);
    }

    //insert photos
    public void insertPhotos(List<Photo> photos) {
        SQLiteDatabase database = getReadableDatabase();

        for(int i = 0, size = photos.size(); i < size; i++) {
            Photo photo = photos.get(i);

            //check if photo is already present in DB or not
            String SELECT_PHOTO = "SELECT * FROM " + TABLE_PHOTOS + " WHERE " + KEY_PATH + " = ?";

            Cursor cursor = database.rawQuery(SELECT_PHOTO, new String[] {photo.path});

            if(cursor.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(KEY_PATH, photo.path);
                values.put(KEY_ORIENTATION, photo.orientation);
                values.put(KEY_DATE_ADDED, photo.dateAdded);
                database.insert(TABLE_PHOTOS, null, values);
            }

            cursor.close();
        }
    }

    //get photos from local DB with pagination
    public List<Photo> fetchPhotos(int limit, int offset) {
        SQLiteDatabase database = getReadableDatabase();

        String SELECT_PHOTO = "SELECT * FROM " + TABLE_PHOTOS + " ORDER BY " + KEY_DATE_ADDED + " DESC LIMIT " + limit + " OFFSET " + offset;

        Cursor cursor = database.rawQuery(SELECT_PHOTO, null);

        List<Photo> photos = new ArrayList<>(cursor.getCount());

        int pathColumnIndex = cursor.getColumnIndex(KEY_PATH);
        int orientationColumnIndex = cursor.getColumnIndex(KEY_ORIENTATION);
        int dateAddedColumnIndex = cursor.getColumnIndex(KEY_DATE_ADDED);

        while(cursor.moveToNext()) {
            String path = cursor.getString(pathColumnIndex);
            int orientation = cursor.getInt(orientationColumnIndex);
            long dateAdded = cursor.getLong(dateAddedColumnIndex);

            photos.add(new Photo(path, orientation, dateAdded));
        }

        cursor.close();

        return photos;
    }

    //get photos from local DB
    public List<Photo> fetchAllPhotos() {
        SQLiteDatabase database = getReadableDatabase();

        String SELECT_PHOTO = "SELECT * FROM " + TABLE_PHOTOS + " ORDER BY " + KEY_DATE_ADDED + " DESC";

        Cursor cursor = database.rawQuery(SELECT_PHOTO, null);

        List<Photo> photos = new ArrayList<>(cursor.getCount());

        int pathColumnIndex = cursor.getColumnIndex(KEY_PATH);
        int orientationColumnIndex = cursor.getColumnIndex(KEY_ORIENTATION);
        int dateAddedColumnIndex = cursor.getColumnIndex(KEY_DATE_ADDED);

        while(cursor.moveToNext()) {
            String path = cursor.getString(pathColumnIndex);
            int orientation = cursor.getInt(orientationColumnIndex);
            long dateAdded = cursor.getLong(dateAddedColumnIndex);

            photos.add(new Photo(path, orientation, dateAdded));
        }

        cursor.close();

        return photos;
    }
}
