package com.example.ankit.photosbrowser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotosStaggeredGridAdapter extends RecyclerView.Adapter<PhotosStaggeredGridAdapter.PhotoViewHolder> {

    private List<Photo> photos;

    public PhotosStaggeredGridAdapter(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public PhotosStaggeredGridAdapter.PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_photo_staggered_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(PhotosStaggeredGridAdapter.PhotoViewHolder holder, int position) {

        DynamicHeightImageView ivPhoto = holder.ivPhoto;
        Photo photo = photos.get(position);

        ivPhoto.setHeightRatio(photo.orientation == Photo.ORIENTATION_LANDSCAPE ? 0.8 : 1.2);

        Picasso.with(ivPhoto.getContext())
                .load(photo.path)
                .fit()
                .centerCrop()
                .into(ivPhoto);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        DynamicHeightImageView ivPhoto;
        public PhotoViewHolder(View itemView) {
            super(itemView);

            ivPhoto = (DynamicHeightImageView) itemView.findViewById(R.id.iv_photo);
        }
    }
}
