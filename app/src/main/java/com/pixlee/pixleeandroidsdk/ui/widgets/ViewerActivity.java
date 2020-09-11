package com.pixlee.pixleeandroidsdk.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pixlee.pixleeandroidsdk.databinding.ActivityViewerBinding;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.databinding.ActivityVideoViewerBinding;
import com.pixlee.pixleesdk.util.PXLViewUtil;

import java.util.HashMap;

/**
 * This shows how to play the video and its product list
 */
public class ViewerActivity extends AppCompatActivity {
    ActivityViewerBinding binding;
    PXLPhoto pxlPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewerBinding.inflate(getLayoutInflater());
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
        HashMap<String, Boolean> bookmarks = (HashMap<String, Boolean>) i.getSerializableExtra("bookmarks");
        // if the photo is null, close this image view
        if (pxlPhoto == null) {
            finish();
            return;
        }

        binding.pxlPhotoProductView.setPhoto(pxlPhoto, bookmarks);
    }

    // start video view with a photo data
    public static void launch(Context context, PXLPhoto pxlPhoto) {
        Intent i = new Intent(context, ViewerActivity.class);
        i.putExtra("pxlPhoto", pxlPhoto);
        context.startActivity(i);
    }
}
