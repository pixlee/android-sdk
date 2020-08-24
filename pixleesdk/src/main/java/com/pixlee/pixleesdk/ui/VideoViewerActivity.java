package com.pixlee.pixleesdk.ui;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLPhotoSize;
import com.pixlee.pixleesdk.PXLProduct;
import com.pixlee.pixleesdk.databinding.ActivityVideoViewerBinding;
import com.pixlee.pixleesdk.util.PXLViewUtil;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * This shows how to play the video and its product list
 */
public class VideoViewerActivity extends AppCompatActivity {
    ActivityVideoViewerBinding binding;
    PXLPhoto pxlPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set a full screen mode
        PXLViewUtil.expandContentAreaOverStatusBar(this);

        // give a padding to the top as much as the status bar's height
        binding.bodyView.setPadding(0, PXLViewUtil.getStatusBarHeight(this), 0, 0);

        // back button's click effect
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent i = getIntent();
        if (i == null) {
            finish();
            return;
        }

        pxlPhoto = i.getParcelableExtra("pxlPhoto");

        // if the photo is null, close this image view
        if (pxlPhoto == null) {
            finish();
            return;
        }

        // start a pixlee loading view
        String json = PXLViewUtil.getLottieLoadingJson(this);
        binding.lottieView.setAnimationFromJson(json, json);
        binding.lottieView.playAnimation();


        // play the video
        playVideo();

        // load a main image into an ImageView
        Glide.with(this)
                .load(pxlPhoto.getUrlForSize(PXLPhotoSize.THUMBNAIL).toString())
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(70, 3)))
                .into(binding.imageViewBg);

        // initiate the product list view
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
    }

    int stopPosition = 0;

    @Override
    public void onResume() {
        super.onResume();
        if (!binding.videoView.isPlaying()) {
            binding.videoView.seekTo(stopPosition);
            binding.videoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.videoView.isPlaying()) {
            stopPosition = binding.videoView.getCurrentPosition();
            binding.videoView.pause();
        }
    }

    void playVideo() {
        setVideoViewer(pxlPhoto.getUrlForSize(PXLPhotoSize.ORIGINAL).toString());
    }

    ProductAdapter adapter;

    void setVideoViewer(String videoUrl) {
        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                binding.lottieView.setVisibility(View.GONE);

                getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            handler.removeCallbacks(runnableTimer);
                        } else {
                            handler.postDelayed(runnableTimer, 0);
                        }
                    }
                });
            }
        });

        binding.videoView.setVideoURI(Uri.parse(videoUrl));

        binding.videoView.setVideoPath(videoUrl);
        binding.videoView.setZOrderOnTop(true);
        binding.videoView.start();
    }

    Handler handler = new Handler();
    Runnable runnableTimer = new Runnable() {

        @Override
        public void run() {
            boolean started = binding.videoView.isPlaying();
            if (!started || binding.videoView.isPlaying()) {
                if (!started) {
                    started = binding.videoView.isPlaying();
                }

                binding.tvTime.setText(showMMSS(binding.videoView.getDuration(), binding.videoView.getCurrentPosition()));
                handler.postDelayed(runnableTimer, 1000);
            } else {
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

    // start video view with a photo data
    public static void launch(Context context, PXLPhoto pxlPhoto) {
        Intent i = new Intent(context, VideoViewerActivity.class);
        i.putExtra("pxlPhoto", pxlPhoto);
        context.startActivity(i);
    }
}
