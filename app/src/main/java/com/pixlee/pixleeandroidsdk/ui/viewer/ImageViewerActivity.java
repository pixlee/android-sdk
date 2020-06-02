package com.pixlee.pixleeandroidsdk.ui.viewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.pixlee.pixleeandroidsdk.BaseActivity;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.config.GlideApp;
import com.pixlee.pixleeandroidsdk.databinding.ActivityImageViewerBinding;
import com.pixlee.pixleeandroidsdk.databinding.ActivityMainBinding;
import com.pixlee.pixleeandroidsdk.ui.BaseFragment;
import com.pixlee.pixleeandroidsdk.ui.IndexFragment;
import com.pixlee.pixleeandroidsdk.ui.util.AssetUtil;
import com.pixlee.pixleeandroidsdk.ui.viewer.adapter.ProductAdapter;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLPhotoSize;
import com.pixlee.pixleesdk.PXLProduct;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * This activity only manage Fragments and a Toolbar.
 */
public class ImageViewerActivity extends BaseActivity {
    ActivityImageViewerBinding binding;
    PXLPhoto pxlPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expandContentAreaOverStatusBar();
        binding.bodyView.setPadding(0, getStatusBarHeight(), 0, 0);

        Intent i = getIntent();
        if (i != null) {
            String json = AssetUtil.getLottieLoadingJson(this);
            binding.lottieView.setAnimationFromJson(json, json);
            binding.lottieView.playAnimation();
            pxlPhoto = i.getParcelableExtra("pxlPhoto");
            if (pxlPhoto == null) {
                finish();
                return;
            }

            String imageUrl = pxlPhoto.getUrlForSize(PXLPhotoSize.BIG).toString();
            GlideApp.with(this)
                    .load(imageUrl)
                    .fitCenter()
                    .error(R.drawable.baseline_cloud_off_black_48)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.imageView.setScaleType(ImageView.ScaleType.CENTER);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.lottieView.setVisibility(View.GONE);
                            binding.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            return false;
                        }
                    }).into(binding.imageView);

            Glide.with(this)
                    .load(pxlPhoto.getUrlForSize(PXLPhotoSize.THUMBNAIL).toString())
                    .centerCrop()
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(70, 3)))
                    .into(binding.imageViewBg);

            if (pxlPhoto.products != null) {
                adapter = new ProductAdapter(pxlPhoto.products, new ProductAdapter.ProductListener() {
                    @Override
                    public void onClicked(PXLProduct product) {
                        if (product.link != null) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.link.toString()));
                            startActivity(browserIntent);
                        }

                    }
                });
                binding.list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                binding.list.setAdapter(adapter);
            }


        } else {
            finish();
        }

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    ProductAdapter adapter;

    public static void launch(Context context, PXLPhoto pxlPhoto) {
        Intent i = new Intent(context, ImageViewerActivity.class);
        i.putExtra("pxlPhoto", pxlPhoto);
        context.startActivity(i);
    }
}
