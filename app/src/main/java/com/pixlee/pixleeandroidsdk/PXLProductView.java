package com.pixlee.pixleeandroidsdk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLProduct;

/**
 * Created by jason on 4/12/2017.
 */

public class PXLProductView extends LinearLayout {
    private NetworkImageView productImage;
    private TextView textView;

    public PXLProductView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pxl_product_view, this, true);
        LinearLayout layout = (LinearLayout) getChildAt(0);
        productImage = (NetworkImageView) layout.getChildAt(0);
        textView = (TextView) layout.getChildAt(1);
        textView.setTextSize(15);
    }

    public void populate(PXLProduct product, ImageLoader imageLoader) {
        productImage.setImageUrl(product.imageThumb.toString(), imageLoader);
        textView.setText(String.format("%s", product.linkText));
    }
}
