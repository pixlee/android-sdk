package com.pixlee.pixleeandroidsdk;

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
    public String username;
    public TextView title;
    public NetworkImageView netImg;
    public Date updatedAt;

    public MyViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        netImg = (NetworkImageView) view.findViewById(R.id.netimg);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                netImg.setLayoutParams(new LinearLayout.LayoutParams(2000, 2000));
            }
        });
    }
}
