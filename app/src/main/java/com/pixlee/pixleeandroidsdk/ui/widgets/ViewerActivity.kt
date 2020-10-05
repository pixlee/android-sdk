package com.pixlee.pixleeandroidsdk.ui.widgets

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.ui.widgets.TextStyle
import com.pixlee.pixleesdk.util.PXLViewUtil
import com.pixlee.pixleesdk.util.px
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
        val item: PhotoWithVideoInfo? = i.getParcelableExtra("photoWithVideoInfo")
        // if the photo is null, close this image view
        if (item == null) {
            finish()
            return
        }

        init(item)
    }

    fun init(item: PhotoWithVideoInfo){
        pxlPhotoProductView.useLifecycleObserver(lifecycle)
        pxlPhotoProductView.setPhoto(photoInfo = item,
                configuration = ProductViewHolder.Configuration().apply {
                    circleIcon = ProductViewHolder.CircleIcon().apply {
                        icon = R.drawable.outline_shopping_bag_black_24
                        iconColor = Color.DKGRAY
                        backgroundColor = ContextCompat.getColor(this@ViewerActivity, R.color.yellow_800)
                        padding = 5.px.toInt()
                    }
                    mainTextStyle = TextStyle().apply {
                        size = 14.px
                        sizeUnit = TypedValue.COMPLEX_UNIT_PX
                        typeface = null
                    }
                    subTextStyle = TextStyle().apply {
                        size = 12.px
                        sizeUnit = TypedValue.COMPLEX_UNIT_PX
                        typeface = null
                    }
                    bookmarkDrawable = ProductViewHolder.Bookmark().apply {
                        isVisible = true
                        selectedIcon = com.pixlee.pixleesdk.R.drawable.baseline_bookmark_black_36
                        unselectedIcon = com.pixlee.pixleesdk.R.drawable.baseline_bookmark_border_black_36
                    }
                    priceTextStyle = TextStyle().apply {
                        size = 24.px
                        sizeUnit = TypedValue.COMPLEX_UNIT_PX
                        typeface = null
                    }
                },
                bookmarkMap = readBookmarks(item.pxlPhoto),
                onBookmarkClicked = { productId, isBookmarkChecked ->
                    Toast.makeText(this, "productId: $productId\nisBookmarkChecked: $isBookmarkChecked", Toast.LENGTH_SHORT).show()
                },
                onProductClicked = {
                    Toast.makeText(this, "product clicked, product id: ${it.id}", Toast.LENGTH_SHORT).show()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.link.toString()))
                    startActivity(browserIntent)
                })
        pxlPhotoProductView.playVideo()
    }

    /**
     * You should custom this method to pass your products' bookmark information
     */
    fun readBookmarks(pxlPhoto: PXLPhoto): HashMap<String, Boolean> {
        // this code should be replaced by your own bookmarks
        val bookmarkMap = HashMap<String, Boolean>()
        if (pxlPhoto.products != null) {
            for (product in pxlPhoto.products) {
                bookmarkMap[product.id] = Random().nextBoolean()
            }
        }
        return bookmarkMap
    }

    companion object {
        // start video view with a photo data
        fun launch(context: Context, pxlPhoto: PhotoWithVideoInfo?) {
            val i = Intent(context, ViewerActivity::class.java)
            i.putExtra("photoWithVideoInfo", pxlPhoto)
            context.startActivity(i)
        }
    }
}
