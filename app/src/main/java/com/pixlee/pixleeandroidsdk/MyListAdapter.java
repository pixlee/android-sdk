package com.pixlee.pixleeandroidsdk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by andy on 4/4/17.
 */

class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private ArrayList<CreateList> galleryList;
    private Context context;

    public MyListAdapter(Context context, ArrayList<CreateList> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public MyListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        //View view = R.layout.row;
        return new MyListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyListAdapter.ViewHolder viewHolder, int i) {
        viewHolder.title.setText(galleryList.get(i).getImage_title());
        viewHolder.description.setText("Sample text hello there");
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.img.setImageResource((galleryList.get(i).getImage_ID()));
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView description;
        private ImageView img;
        public ViewHolder(View view) {
            super(view);
            description = (TextView)view.findViewById(R.id.description);
            title = (TextView)view.findViewById(R.id.title);
            img = (ImageView)view.findViewById(R.id.img);
        }
    }
    private Context mContext;
    private String[]  Title;
    private int[] imge;
}
