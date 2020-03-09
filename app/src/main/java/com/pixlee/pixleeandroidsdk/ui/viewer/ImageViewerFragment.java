package com.pixlee.pixleeandroidsdk.ui.viewer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.config.GlideApp;
import com.pixlee.pixleeandroidsdk.databinding.FragmentImageViewerBinding;
import com.pixlee.pixleeandroidsdk.ui.BaseFragment;

/**
 * This is an image viewer
 * Created by sungjun.
 */
public class ImageViewerFragment extends BaseFragment {
    @Override
    public int getTitleResource() {
        return R.string.title_image_viewer;
    }

    FragmentImageViewerBinding binding;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentImageViewerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GlideApp.with(getContext()).asDrawable().clone()
                .load(getImageUrl())
                .thumbnail(0.2f)
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
                        binding.vLoading.setVisibility(View.GONE);
                        binding.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        return false;
                    }
                }).into(binding.imageView);
    }

    private String getImageUrl() {
        return getArguments().getString("imageUrl");
    }

    public static Fragment getInstance(String imageUrl){
        Fragment f = new ImageViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", imageUrl);
        f.setArguments(bundle);
        return f;
    }
}
