package com.pixlee.pixleeandroidsdk;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

public class ListViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public NetworkImageView netImg;

    public ListViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        netImg = (NetworkImageView) view.findViewById(R.id.netimg);
    }
}
