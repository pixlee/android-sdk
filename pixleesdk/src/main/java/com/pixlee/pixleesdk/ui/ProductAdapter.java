package com.pixlee.pixleesdk.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.pixlee.pixleesdk.PXLProduct;

import java.util.List;

/**
 * Created by sungjun on 6/2/20.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    public List<PXLProduct> list;

    public interface ProductListener{
        void onClicked(PXLProduct product);
    }

    ProductListener listener;
    public ProductAdapter(List<PXLProduct> list, ProductListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ProductViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        holder.bind(list.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.onClicked(list.get(holder.getAdapterPosition()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}