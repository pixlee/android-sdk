package com.pixlee.pixleeandroidsdk;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by andy on 4/7/17.
 */

public class GridViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public NetworkImageView netImg;

    public GridViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        netImg = (NetworkImageView) view.findViewById(R.id.netimg);
    }
}
