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

import java.util.ArrayList;

/**
 * Created by andy on 4/4/17.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private ArrayList<CreateList> galleryList;
    private Context context;
    private ImageLoader imageLoader;

    public MyRecyclerAdapter(Context context, ArrayList<CreateList> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
        this.imageLoader = PXLClient.getInstance(context).getImageLoader();
    }

    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyRecyclerAdapter.ViewHolder viewHolder, int i) {
        CreateList photo = galleryList.get(i);
        viewHolder.title.setText(photo.getImage_title());
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.img.setImageResource((photo.getImage_ID()));
        if (photo.getImagePath() !=  null) {
            viewHolder.netImg.setImageUrl(photo.getImagePath().toString(), imageLoader);
        }
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView img;
        private NetworkImageView netImg;
        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            img = (ImageView) view.findViewById(R.id.img);
            netImg = (NetworkImageView) view.findViewById(R.id.netimg);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    netImg.setLayoutParams(new LinearLayout.LayoutParams(2000, 2000));
                    img.setLayoutParams(new LinearLayout.LayoutParams(2000, 2000));
                }
            });
        }
    }
}
