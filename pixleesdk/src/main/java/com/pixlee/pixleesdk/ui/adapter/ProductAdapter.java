package com.pixlee.pixleesdk.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.pixlee.pixleesdk.PXLProduct;
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sungjun on 6/2/20.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    /**
     * String: product id
     * Boolean: is bookmarked
     */
    HashMap<String, Boolean> bookmarkMap;
    public List<PXLProduct> list;

    public interface ProductListener {
        void onClicked(PXLProduct product);
    }

    ProductListener listener;

    public ProductAdapter(List<PXLProduct> list, HashMap<String, Boolean> bookmarkMap, ProductListener listener) {
        this.list = list;
        this.bookmarkMap = bookmarkMap;
        this.listener = listener;
    }

    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ProductViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        PXLProduct product = list.get(position);
        Boolean isBookmarked = null;
        if (bookmarkMap != null) {
            isBookmarked = bookmarkMap.get(product.id);
        }

        holder.bind(product, isBookmarked);
        holder.binding.cardView.setOnClickListener(new View.OnClickListener() {
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