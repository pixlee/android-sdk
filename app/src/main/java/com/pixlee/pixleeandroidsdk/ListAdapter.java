package com.pixlee.pixleeandroidsdk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;

import java.util.ArrayList;

class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
    private ArrayList<PXLPhoto> galleryList;
    private Context context;
    private SampleActivity saRef;

    public ListAdapter(Context context, ArrayList<PXLPhoto> galleryList, SampleActivity sa) {
        this.galleryList = galleryList;
        this.context = context;
        this.saRef = sa;
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
                .load(photo.cdnPhotos.mediumUrl)
                .centerCrop()
                .into(viewHolder.netImg);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saRef.switchVisibilities(photo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }
}
