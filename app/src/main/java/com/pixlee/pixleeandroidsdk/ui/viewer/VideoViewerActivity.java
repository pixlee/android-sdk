package com.pixlee.pixleeandroidsdk.ui.viewer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pixlee.pixleeandroidsdk.BaseActivity;
import com.pixlee.pixleeandroidsdk.databinding.ActivityVideoViewerBinding;
import com.pixlee.pixleeandroidsdk.ui.util.AssetUtil;
import com.pixlee.pixleeandroidsdk.ui.viewer.adapter.ProductAdapter;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLPhotoSize;
import com.pixlee.pixleesdk.PXLProduct;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * This activity only manage Fragments and a Toolbar.
 */
public class VideoViewerActivity extends BaseActivity {
    ActivityVideoViewerBinding binding;
    PXLPhoto pxlPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expandContentAreaOverStatusBar();
        binding.bodyView.setPadding(0, getStatusBarHeight(), 0, 0);
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

            setVideoViewer(pxlPhoto.getUrlForSize(PXLPhotoSize.ORIGINAL).toString());

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

    void setVideoViewer(String videoUrl){
        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                binding.lottieView.setVisibility(View.GONE);

                getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                        if(event==Lifecycle.Event.ON_DESTROY){
                            handler.removeCallbacks(runnableTimer);
                        }else{
                            handler.postDelayed(runnableTimer, 0);
                        }
                    }
                });
            }
        });

        binding.videoView.setVideoPath(videoUrl);
        binding.videoView.setZOrderOnTop(true);
        binding.videoView.start();
    }

    Handler handler = new Handler();
    Runnable runnableTimer = new Runnable(){

        @Override
        public void run() {
            boolean started = binding.videoView.isPlaying();
            if (!started || binding.videoView.isPlaying()) {
                if (!started) {
                    started = binding.videoView.isPlaying();
                }

                binding.tvTime.setText(showMMSS(binding.videoView.getDuration(), binding.videoView.getCurrentPosition()));
                handler.postDelayed(runnableTimer, 1000);
            }else{
                binding.tvTime.setText(showMMSS(binding.videoView.getDuration(), binding.videoView.getDuration()));
            }
        }
    };

    String showMMSS(int duration, int timeInMilli) {
        int gap = duration - timeInMilli;
        int sec = gap / 1000;
        int min = sec / 60;
        int secOfMin = sec % 60;
        return String.format(min + ":%02d", secOfMin);
    }

    public static void launch(Context context, PXLPhoto pxlPhoto) {
        Intent i = new Intent(context, VideoViewerActivity.class);
        i.putExtra("pxlPhoto", pxlPhoto);
        context.startActivity(i);
    }
}
