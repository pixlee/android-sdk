package com.pixlee.pixleeandroidsdk;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class GridViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public ImageView netImg;

    public GridViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        netImg = (ImageView) view.findViewById(R.id.netimg);
    }
}
