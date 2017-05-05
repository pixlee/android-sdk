package com.pixlee.pixleeandroidsdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pixlee.pixleesdk.PXLProduct;

public class PXLProductView extends LinearLayout {
    private NetworkImageView productImage;
    private TextView textView;
    private Uri link;

    public PXLProductView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pxl_product_view, this, true);
        LinearLayout layout = (LinearLayout) getChildAt(0);
        productImage = (NetworkImageView) layout.getChildAt(0);
        textView = (TextView) layout.getChildAt(1);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void populate(PXLProduct product, ImageLoader imageLoader) {
        if(product.imageThumb != null){
            productImage.setImageUrl(product.imageThumb.toString(), imageLoader);
        }

        link = Uri.parse(product.link.toString());
        String linkText = String.format("%s", product.linkText);
        textView.setText(linkText);
        View.OnClickListener handler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                getContext().startActivity(intent);
            }
        };
        setOnClickListener(handler);
        textView.setOnClickListener(handler);
    }
}
