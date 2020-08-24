package com.pixlee.pixleesdk.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLPhotoSize;
import com.pixlee.pixleesdk.PXLProduct;
import com.pixlee.pixleesdk.databinding.ActivityVideoViewerBinding;
import com.pixlee.pixleesdk.ui.adapter.ProductAdapter;
import com.pixlee.pixleesdk.util.PXLViewUtil;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * This shows how to play the video and its product list
 */
public class PXLPhotoViewerActivity extends AppCompatActivity {
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

        String title = i.getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            binding.tvTitle.setText(title);
        }

        pxlPhoto = i.getParcelableExtra("pxlPhoto");

        // if the photo is null, close this image view
        if (pxlPhoto == null) {
            finish();
            return;
        }

        startBlurBG();
        loadProducts();

        if (pxlPhoto.isVideo()) {
            startVideo();
        } else {
            startPhoto();
        }
    }

    void startBlurBG() {
        // load a main image into an ImageView
        Glide.with(this)
                .load(pxlPhoto.getUrlForSize(PXLPhotoSize.THUMBNAIL).toString())
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(70, 3)))
                .into(binding.imageViewBg);
    }

    void loadProducts() {
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

    void startPhoto() {
        binding.imageView.setVisibility(View.VISIBLE);
        String imageUrl = pxlPhoto.getUrlForSize(PXLPhotoSize.BIG).toString();
        // load a main image into an ImageView
        Glide.with(this)
                .load(imageUrl)
                .fitCenter()
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
    }

    void startVideo() {
        // start a pixlee loading view
        String json = PXLViewUtil.getLottieLoadingJson(this);
        binding.lottieView.setAnimationFromJson(json, json);
        binding.lottieView.playAnimation();

        // play the video
        playVideo();
    }

    int stopPosition = 0;

    @Override
    public void onResume() {
        super.onResume();
        if (pxlPhoto.isVideo() && !binding.videoView.isPlaying()) {
            binding.videoView.seekTo(stopPosition);
            binding.videoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (pxlPhoto.isVideo() && binding.videoView.isPlaying()) {
            stopPosition = binding.videoView.getCurrentPosition();
            binding.videoView.pause();
        }
    }

    void playVideo() {
        setVideoViewer(pxlPhoto.getUrlForSize(PXLPhotoSize.ORIGINAL).toString());
    }

    ProductAdapter adapter;

    void setVideoViewer(String videoUrl) {
        binding.videoView.setVisibility(View.VISIBLE);
        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            binding.videoView.setAlpha(1);
                        }
                        return true;
                    }
                });
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
    public static void launch(Context context, PXLPhoto pxlPhoto, String title) {
        Intent i = new Intent(context, PXLPhotoViewerActivity.class);
        if (title != null) {
            i.putExtra("title", title);
        }

        i.putExtra("pxlPhoto", pxlPhoto);
        context.startActivity(i);
    }

    public static void launch(Context context, PXLPhoto pxlPhoto) {
        launch(context, pxlPhoto, null);
    }
}
