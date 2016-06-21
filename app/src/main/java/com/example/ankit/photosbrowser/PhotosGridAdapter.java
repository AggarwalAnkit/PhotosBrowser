package com.example.ankit.photosbrowser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotosGridAdapter extends RecyclerView.Adapter<PhotosGridAdapter.PhotoViewHolder> {

    private List<Photo> photos;

    public PhotosGridAdapter(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public PhotosGridAdapter.PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_photo_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(PhotosGridAdapter.PhotoViewHolder holder, int position) {
        Picasso.with(holder.ivPhoto.getContext())
                .load(photos.get(position).path)
                .fit()
                .centerCrop()
                .into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;
        public PhotoViewHolder(View itemView) {
            super(itemView);

            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
        }
    }
}
