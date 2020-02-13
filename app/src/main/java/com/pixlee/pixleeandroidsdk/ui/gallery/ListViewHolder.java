package com.pixlee.pixleeandroidsdk.ui.gallery;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pixlee.pixleeandroidsdk.R;


public class ListViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public ImageView netImg;

    public ListViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        netImg = (ImageView) view.findViewById(R.id.netimg);
    }
}
