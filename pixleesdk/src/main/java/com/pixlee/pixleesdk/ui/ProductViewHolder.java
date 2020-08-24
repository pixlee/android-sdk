package com.pixlee.pixleesdk.ui;

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

import java.text.DecimalFormat;

/**
 * Created by sungjun on 6/2/20.
 */
class ProductViewHolder extends RecyclerView.ViewHolder {
    CardView cardView;
    ImageView imageView;
    TextView tvMessage;
    TextView tvPrice;

    DecimalFormat formatter = new DecimalFormat("#,##0.##");

    public ProductViewHolder(View containerView) {
        super(containerView);
        cardView = containerView.findViewById(R.id.cardView);
        imageView = containerView.findViewById(R.id.imageView);
        tvMessage = containerView.findViewById(R.id.tvMessage);
        tvPrice = containerView.findViewById(R.id.tvPrice);
    }

    public void bind(PXLProduct product) {
        Glide.with(imageView.getContext())
                .load(product.imageThumb)
                .centerCrop()
                .into(imageView);
        tvMessage.setText(product.title);
        if (product.price != null) {
            String currency = "";
            if (product.currency != null && !product.currency.isEmpty()) {
                currency = product.currency + " ";
            }

            tvPrice.setText(currency + formatter.format(product.price));
        } else
            tvPrice.setText("");
    }

    public static ProductViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }
}
