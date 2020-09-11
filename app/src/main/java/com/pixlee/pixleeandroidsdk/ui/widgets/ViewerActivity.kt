package com.pixlee.pixleeandroidsdk.ui.widgets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleesdk.PXLPhoto
import com.pixlee.pixleesdk.util.PXLViewUtil
import kotlinx.android.synthetic.main.activity_viewer.*
import java.util.*

/**
 * Created by sungjun on 9/11/20.
 */
/**
 * This shows how to play the video and its product list
 */
class ViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewer)

        // set a full screen mode
        PXLViewUtil.expandContentAreaOverStatusBar(this)

        // give a padding to the top as much as the status bar's height
        bodyView.setPadding(0, PXLViewUtil.getStatusBarHeight(this), 0, 0)

        // back button's click effect
        backButton.setOnClickListener(View.OnClickListener { onBackPressed() })
        val i = intent
        if (i == null) {
            finish()
            return
        }
        val pxlPhoto: PXLPhoto? = i.getParcelableExtra("pxlPhoto")
        // if the photo is null, close this image view
        if (pxlPhoto == null) {
            finish()
            return
        }
        pxlPhotoProductView.setPhoto(pxlPhoto = pxlPhoto, bookmarkMap = readBookmarks(pxlPhoto), onBookmarkClicked = { productId, isBookmarkChecked ->
            Toast.makeText(this, "productId: $productId\nisBookmarkChecked: $isBookmarkChecked", Toast.LENGTH_SHORT).show()
        })
    }

    fun readBookmarks(pxlPhoto: PXLPhoto): HashMap<String, Boolean> {
        // this code should be replaced by your own bookmarks
        val bookmarkMap = HashMap<String, Boolean>()
        if (pxlPhoto.products != null) {
            for (product in pxlPhoto.products) {
                bookmarkMap.put(product.id, Random().nextBoolean())
            }
        }
        return bookmarkMap
    }

    companion object {
        // start video view with a photo data
        fun launch(context: Context, pxlPhoto: PXLPhoto?) {
            val i = Intent(context, ViewerActivity::class.java)
            i.putExtra("pxlPhoto", pxlPhoto)
            context.startActivity(i)
        }
    }
}
