package com.pixlee.pixleeandroidsdk.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixlee.pixleeandroidsdk.GalleryClickListener;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLPhotoSize;

import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<GridViewHolder> {
    private ArrayList<PXLPhoto> galleryList;
    private Context context;
    private GalleryClickListener listener;

    public GridAdapter(Context context, ArrayList<PXLPhoto> galleryList, GalleryClickListener listener) {
        this.galleryList = galleryList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder viewHolder, int i) {
        final PXLPhoto photo = galleryList.get(i);
        viewHolder.title.setText(photo.photoTitle);

        Glide.with(context)
                .load(photo.getUrlForSize(PXLPhotoSize.MEDIUM))
                .centerCrop()
                .into(viewHolder.netImg);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(photo);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (galleryList  != null) {
            return galleryList.size();
        }
        return 0;
    }
}
