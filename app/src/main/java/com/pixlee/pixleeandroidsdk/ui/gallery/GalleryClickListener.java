package com.pixlee.pixleeandroidsdk.ui.gallery;

import android.view.View;

import com.pixlee.pixleesdk.data.PXLPhoto;

/**
 * Click Listener for Gallery
 */
public interface GalleryClickListener {
    void onItemClicked(View view, PXLPhoto photo);
}
