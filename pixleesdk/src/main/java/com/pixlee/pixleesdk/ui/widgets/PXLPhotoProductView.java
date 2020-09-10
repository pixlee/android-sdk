package com.pixlee.pixleesdk.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.pixlee.pixleesdk.databinding.WidgetViewerBinding;
import com.pixlee.pixleesdk.ui.adapter.ProductAdapter;
import com.pixlee.pixleesdk.util.PXLViewUtil;

import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by sungjun on 9/10/20.
 */
public class PXLPhotoProductView extends RelativeLayout {
    public PXLPhotoProductView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PXLPhotoProductView(Context context) {
        super(context, null);
        initView(context);
    }

    WidgetViewerBinding binding;

    public void initView(Context context) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = WidgetViewerBinding.inflate(li, this, false);
        addView(binding.getRoot());
    }

    PXLPhoto pxlPhoto;
    HashMap<String, Boolean> bookmarkMap;

    /**
     * Start the UI
     * @param pxlPhoto
     * @param bookmarkMap: user's current bookmarks <Product id: String, is bookmarked: Boolean>
     *                      if null, hide bookmark toggle
     *                      if not null, show bookmark toggle
     */
    public void setPhoto(PXLPhoto pxlPhoto, @Nullable HashMap<String, Boolean> bookmarkMap) {
        this.pxlPhoto = pxlPhoto;
        this.bookmarkMap = bookmarkMap;

        startBlurBG();
        loadProducts();

        if (pxlPhoto.isVideo()) {
            startVideo();
        } else {
            startPhoto();
        }
    }

    public void setPhoto(PXLPhoto pxlPhoto) {
        setPhoto(pxlPhoto, null);
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
            adapter = new ProductAdapter(pxlPhoto.products, bookmarkMap, new ProductAdapter.ProductListener() {
                @Override
                public void onClicked(PXLProduct product) {
                    if (product.link != null) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.link.toString()));
                        getContext().startActivity(browserIntent);
                    }

                }
            });
            binding.list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
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
        String json = PXLViewUtil.getLottieLoadingJson(getContext());
        binding.lottieView.setAnimationFromJson(json, json);
        binding.lottieView.playAnimation();

        // play the video
        playVideo();
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
                if (getContext() instanceof ComponentActivity) {
                    ((ComponentActivity) getContext()).getLifecycle().addObserver(new LifecycleEventObserver() {
                        @Override
                        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                            Log.d("PPV", "Lifecycle.Event : " + event.name());
                            if (event == Lifecycle.Event.ON_DESTROY) {
                                handler.removeCallbacks(runnableTimer);
                            } else {
                                handler.postDelayed(runnableTimer, 0);
                            }

                            if (event == Lifecycle.Event.ON_RESUME) {
                                onResume();
                            } else if (event == Lifecycle.Event.ON_PAUSE) {
                                onPause();
                            }
                        }
                    });
                }

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

    int stopPosition = 0;

    public void onResume() {
        if (pxlPhoto.isVideo() && !binding.videoView.isPlaying()) {
            binding.videoView.seekTo(stopPosition);
            binding.videoView.start();
        }
    }

    public void onPause() {
        if (pxlPhoto.isVideo() && binding.videoView.isPlaying()) {
            stopPosition = binding.videoView.getCurrentPosition();
            binding.videoView.pause();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Stop media player
    }
}
