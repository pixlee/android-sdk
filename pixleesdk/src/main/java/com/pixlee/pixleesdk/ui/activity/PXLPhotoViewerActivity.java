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

import java.util.HashMap;
import java.util.Random;

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
        HashMap<String, Boolean> bookmarks = (HashMap<String, Boolean>) i.getSerializableExtra("bookmarks");
        // if the photo is null, close this image view
        if (pxlPhoto == null) {
            finish();
            return;
        }

        binding.pxlPhotoProductView.setPhoto(pxlPhoto, bookmarks);
    }

    // start video view with a photo data
    public static void launch(Context context, String title, PXLPhoto pxlPhoto, HashMap<String, Boolean> bookmarks) {
        Intent i = new Intent(context, PXLPhotoViewerActivity.class);
        if (title != null) {
            i.putExtra("title", title);
        }

        if (bookmarks != null) {
            i.putExtra("bookmarks", bookmarks);
        }

        i.putExtra("pxlPhoto", pxlPhoto);
        context.startActivity(i);
    }

    public static void launch(Context context, PXLPhoto pxlPhoto) {
        launch(context, null, pxlPhoto, null);
    }
}
