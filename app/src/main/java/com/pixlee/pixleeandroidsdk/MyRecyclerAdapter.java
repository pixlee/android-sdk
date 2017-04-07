package com.pixlee.pixleeandroidsdk;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Network;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;

import java.util.ArrayList;

/**
 * Created by andy on 4/4/17.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private ArrayList<PXLPhoto> galleryList;
    private Context context;
    private ImageLoader imageLoader;

    public MyRecyclerAdapter(Context context, ArrayList<PXLPhoto> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
        this.imageLoader = PXLClient.getInstance(context).getImageLoader();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        PXLPhoto photo = galleryList.get(i);
        viewHolder.title.setText(photo.photoTitle);//.getImage_title());

        if (photo.thumbnailUrl !=  null) {
            viewHolder.netImg.setImageUrl(photo.thumbnailUrl.toString(), imageLoader);
            viewHolder.netImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.sourceIcon.setImageResource(photo.sourceIconImage());
        }
    }

    @Override
    public int getItemCount() {
        if (galleryList  != null) {
            return galleryList.size();
        }
        return 0;
    }

}
