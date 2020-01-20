package com.pixlee.pixleeandroidsdk.viewer;

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
import com.pixlee.pixleeandroidsdk.BaseFragment;
import com.pixlee.pixleeandroidsdk.R;
import com.pixlee.pixleeandroidsdk.config.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageViewerFragment extends BaseFragment {
    @BindView(R.id.iv)
    ImageView iv;

    @BindView(R.id.v_loading)
    View v_loading;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GlideApp.with(getContext()).asDrawable().clone()
                .load(getImageUrl())
                .thumbnail(0.2f)
                .fitCenter()
                .dontAnimate()
                .error(R.drawable.baseline_cloud_off_black_48)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        iv.setScaleType(ImageView.ScaleType.CENTER);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        v_loading.setVisibility(View.GONE);
                        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        return false;
                    }
                }).into(iv);
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
