package com.pixlee.pixleesdk.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pixlee.pixleesdk.PXLPhoto
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.util.PXLViewUtil
import kotlinx.android.synthetic.main.activity_video_viewer.*
import java.util.*

/**
 * This shows how to play the video and its product list
 */
class PXLPhotoViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_viewer)

        // set a full screen mode
        PXLViewUtil.expandContentAreaOverStatusBar(this)

        // give a padding to the top as much as the status bar's height
        bodyView.setPadding(0, PXLViewUtil.getStatusBarHeight(this), 0, 0)

        // back button's click effect
        backButton.setOnClickListener { onBackPressed() }
        val i = intent
        if (i == null) {
            finish()
            return
        }
        val title = i.getStringExtra("title")
        if (!TextUtils.isEmpty(title)) {
            tvTitle.text = title
        }
        val pxlPhoto:PXLPhoto? = i.getParcelableExtra("pxlPhoto")
        // if the photo is null, close this image view
        if (pxlPhoto == null) {
            finish()
            return
        }
        pxlPhotoProductView.setPhoto(pxlPhoto = pxlPhoto)
    }

    companion object {
        // start video view with a photo data
        fun launch(context: Context, title: String?, pxlPhoto: PXLPhoto?, bookmarks: HashMap<String?, Boolean?>?) {
            val i = Intent(context, PXLPhotoViewerActivity::class.java)
            if (title != null) {
                i.putExtra("title", title)
            }
            if (bookmarks != null) {
                i.putExtra("bookmarks", bookmarks)
            }
            i.putExtra("pxlPhoto", pxlPhoto)
            context.startActivity(i)
        }

        fun launch(context: Context, pxlPhoto: PXLPhoto?) {
            launch(context, null, pxlPhoto, null)
        }
    }
}