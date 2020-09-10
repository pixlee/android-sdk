package com.pixlee.pixleesdk.ui.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixlee.pixleesdk.PXLProduct;
import com.pixlee.pixleesdk.R;
import com.pixlee.pixleesdk.databinding.ItemProductBinding;

import java.text.DecimalFormat;

/**
 * Created by sungjun on 6/2/20.
 */
public class ProductViewHolder extends RecyclerView.ViewHolder {
    public ItemProductBinding binding;

    DecimalFormat formatter = new DecimalFormat("#,##0.##");

    public ProductViewHolder(ItemProductBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(PXLProduct product, Boolean isBookmarked) {
        Glide.with(binding.imageView.getContext())
                .load(product.imageThumb)
                .centerCrop()
                .into(binding.imageView);
        binding.tvMessage.setText(product.title);
        if (product.price != null) {
            String currency = "";
            if (product.currency != null && !product.currency.isEmpty()) {
                currency = product.currency + " ";
            }

            binding.tvPrice.setText(currency + formatter.format(product.price));
        } else {
            binding.tvPrice.setText("");
        }

        if (isBookmarked != null) {
            binding.bookmark.setVisibility(View.VISIBLE);
            binding.bookmark.setChecked(isBookmarked);
        } else {
            binding.bookmark.setVisibility(View.GONE);
        }
    }

    public static ProductViewHolder create(ViewGroup parent) {
        return new ProductViewHolder(
                ItemProductBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }
}
