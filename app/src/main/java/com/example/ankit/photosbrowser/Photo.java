package com.example.ankit.photosbrowser;

public class Photo {

    public String path;
    public int orientation;
    public long dateAdded;

    public Photo(String path, int orientation, long dateAdded) {
        this.path = path;
        this.orientation = orientation;
        this.dateAdded = dateAdded;
    }

    public static final int ORIENTATION_PORTRAIT = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;

    public static final String FILE_URI_ID = "file://";

}
