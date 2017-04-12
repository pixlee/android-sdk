package com.pixlee.pixleeandroidsdk;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by jason on 4/11/2017.
 */

public class ListViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public NetworkImageView netImg;

    public ListViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        netImg = (NetworkImageView) view.findViewById(R.id.netimg);
    }
}
