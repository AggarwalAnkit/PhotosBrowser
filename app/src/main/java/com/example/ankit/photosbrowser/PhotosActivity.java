package com.example.ankit.photosbrowser;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PhotosActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private List<Photo> mPhotos;

    private PhotosGridAdapter mPhotosGridAdapter;
    private PhotosStaggeredGridAdapter mPhotosStaggeredGridAdapter;
    private PhotosLinearAdapter mPhotosLinearAdapter;

    private GridLayoutManager mGridLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView.LayoutManager mCurrentLayoutManager;

    private SharedPreferences mSharedPreferences;

    private RecyclerView mRvPhotos;

    private ScaleGestureDetector mScaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Fetching Photos...");
        mProgressDialog.setCancelable(false);

        mSharedPreferences = getSharedPreferences("PHOTOS_APP", MODE_PRIVATE);

        mPhotos = new ArrayList<>();

        mRvPhotos = (RecyclerView) findViewById(R.id.rv_photos);

        mGridLayoutManager = new GridLayoutManager(this, 3);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mPhotosGridAdapter = new PhotosGridAdapter(mPhotos);
        mPhotosStaggeredGridAdapter = new PhotosStaggeredGridAdapter(mPhotos);
        mPhotosLinearAdapter = new PhotosLinearAdapter(mPhotos);

        mCurrentLayoutManager = mGridLayoutManager;
        mRvPhotos.setLayoutManager(mGridLayoutManager);
        mRvPhotos.setAdapter(mPhotosGridAdapter);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if(detector.getScaleFactor() < 1) {
                    if(mCurrentLayoutManager.equals(mStaggeredGridLayoutManager)) {
                        int firstVisibleItemPosition = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(new int[2])[0];
                        mCurrentLayoutManager = mGridLayoutManager;
                        mRvPhotos.setLayoutManager(mGridLayoutManager);
                        mRvPhotos.setAdapter(mPhotosGridAdapter);
                        mRvPhotos.scrollToPosition(firstVisibleItemPosition);
                    } else if(mCurrentLayoutManager.equals(mLinearLayoutManager)) {
                        int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                        mCurrentLayoutManager = mStaggeredGridLayoutManager;
                        mRvPhotos.setLayoutManager(mStaggeredGridLayoutManager);
                        mRvPhotos.setAdapter(mPhotosStaggeredGridAdapter);
                        mRvPhotos.scrollToPosition(firstVisibleItemPosition);
                    }
                } else {
                    if(mCurrentLayoutManager.equals(mStaggeredGridLayoutManager)) {
                        int firstVisibleItemPosition = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(new int[2])[0];
                        mCurrentLayoutManager = mLinearLayoutManager;
                        mRvPhotos.setLayoutManager(mLinearLayoutManager);
                        mRvPhotos.setAdapter(mPhotosLinearAdapter);
                        mRvPhotos.scrollToPosition(firstVisibleItemPosition);
                    } else if(mCurrentLayoutManager.equals(mGridLayoutManager)) {
                        int firstVisibleItemPosition = mGridLayoutManager.findFirstVisibleItemPosition();
                        mCurrentLayoutManager = mStaggeredGridLayoutManager;
                        mRvPhotos.setLayoutManager(mStaggeredGridLayoutManager);
                        mRvPhotos.setAdapter(mPhotosStaggeredGridAdapter);
                        mRvPhotos.scrollToPosition(firstVisibleItemPosition);
                    }
                }
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });

        mRvPhotos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        new InsertPhotosTask().execute(mSharedPreferences.getLong("KEY_LAST_SEARCH_DATE", 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int firstVisibleItemPosition = 0;

        if(mCurrentLayoutManager.equals(mLinearLayoutManager)) {
            firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        } else if(mCurrentLayoutManager.equals(mStaggeredGridLayoutManager)) {
            firstVisibleItemPosition = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(new int[2])[0];
        } else if(mCurrentLayoutManager.equals(mGridLayoutManager)) {
            firstVisibleItemPosition = mGridLayoutManager.findFirstVisibleItemPosition();
        }

        switch (item.getItemId()) {
            case R.id.menu_grid:
                mCurrentLayoutManager = mGridLayoutManager;
                mRvPhotos.setLayoutManager(mGridLayoutManager);
                mRvPhotos.setAdapter(mPhotosGridAdapter);
                mRvPhotos.scrollToPosition(firstVisibleItemPosition);
                break;
            case R.id.menu_staggered:
                mCurrentLayoutManager = mStaggeredGridLayoutManager;
                mRvPhotos.setLayoutManager(mStaggeredGridLayoutManager);
                mRvPhotos.setAdapter(mPhotosStaggeredGridAdapter);
                mRvPhotos.scrollToPosition(firstVisibleItemPosition);
                break;
            case R.id.menu_linear:
                mCurrentLayoutManager = mLinearLayoutManager;
                mRvPhotos.setLayoutManager(mLinearLayoutManager);
                mRvPhotos.setAdapter(mPhotosLinearAdapter);
                mRvPhotos.scrollToPosition(firstVisibleItemPosition);
                break;
            default:
                break;
        }
        return true;
    }

    private class InsertPhotosTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Long... params) {
            long lastSearchDate = params[0];

            //get photos from last search date from gallery
            String[] projection = new String[]{
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_ADDED
            };

            String selection = MediaStore.Images.Media.DATE_ADDED + " > ?";

            // Get the base URI for the People table in the Contacts content provider.
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            // Make the query.
            Cursor cursor = managedQuery(uri, projection, selection, new String[] {lastSearchDate+""}, MediaStore.Images.Media.DATE_ADDED + " DESC");

            //add all photos in local DB
            List<Photo> newPhotos = new ArrayList<>(cursor.getCount());

            if(cursor.getCount() > 0) {

                int pathColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int dateAddedColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);

                while (cursor.moveToNext()) {
                    String path = Photo.FILE_URI_ID + cursor.getString(pathColumnIndex);
                    long dateAdded = cursor.getLong(dateAddedColumnIndex);
                    newPhotos.add(new Photo(path, getImageOrientation(path), dateAdded));
                }

                if (newPhotos.size() > 0) {
                    mSharedPreferences.edit().putLong("KEY_LAST_SEARCH_DATE", newPhotos.get(newPhotos.size() - 1).dateAdded).commit();
                }
            }

            //add these photos in local DB
            DatabaseHelper.getInstance(PhotosActivity.this).insertPhotos(newPhotos);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new FetchPhotosTask().execute();
        }
    }

    private class FetchPhotosTask extends AsyncTask<Void, Void, List<Photo>> {

        @Override
        protected List<Photo> doInBackground(Void... params) {
            return DatabaseHelper.getInstance(PhotosActivity.this).fetchAllPhotos();
        }

        @Override
        protected void onPostExecute(List<Photo> photos) {
            if(photos.size() > 0) {
                mPhotos.addAll(photos);
                mPhotosGridAdapter.notifyDataSetChanged();
                mPhotosStaggeredGridAdapter.notifyDataSetChanged();
                mPhotosLinearAdapter.notifyDataSetChanged();
            }
            mProgressDialog.dismiss();
        }
    }

    private int getImageOrientation(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(path.replaceAll(Photo.FILE_URI_ID, ""), options);

        if(options.outHeight > options.outWidth) {
            return Photo.ORIENTATION_PORTRAIT;
        } else {
            return Photo.ORIENTATION_LANDSCAPE;
        }
    }
}
