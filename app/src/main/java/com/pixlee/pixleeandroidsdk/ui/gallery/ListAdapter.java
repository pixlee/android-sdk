package com.pixlee.pixleeandroidsdk.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLPhotoSize;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
    private ArrayList<PXLPhoto> galleryList;
    private Context context;
    private GalleryClickListener listener;

    public ListAdapter(Context context, ArrayList<PXLPhoto> galleryList, GalleryClickListener listener) {
        this.galleryList = galleryList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder viewHolder, int i) {
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
        return galleryList.size();
    }
}
