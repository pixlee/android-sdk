package com.pixlee.pixleeandroidsdk;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.Date;

/**
 * Created by andy on 4/7/17.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public NetworkImageView netImg;
    public ImageView sourceIcon;

    public MyViewHolder(View view, final SampleActivity sampleActivity) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        netImg = (NetworkImageView) view.findViewById(R.id.netimg);
        sourceIcon = (ImageView) view.findViewById(R.id.sourceIcon);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sampleActivity.switchVisibilities();
            }
        });
    }
}
