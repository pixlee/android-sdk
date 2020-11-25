package com.pixlee.pixleeandroidsdk.ui.live

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.gallery.KtxGalleryViewModel
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.ui.widgets.*
import com.pixlee.pixleesdk.util.PXLViewUtil
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.activity_viewer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.net.URL
import java.util.*

/**
 * Created by sungjun on 9/11/20.
 */
/**
 * This shows how to play the video and its product list
 */
class LiveViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewer)

        // set a full screen mode
        PXLViewUtil.expandContentAreaOverStatusBar(this)

        // give a padding to the top as much as the status bar's height
        pxlPhotoProductView.addPaddingToHeader(0, PXLViewUtil.getStatusBarHeight(this), 0, 0)

        val i = intent
        if (i == null) {
            finish()
            return
        }
        val item: PXLPhoto? = i.getParcelableExtra("pxlPhoto")
        // if the photo is null, close this image view
        if (item == null) {
            finish()
            return
        }

        init(item)
    }

    fun init(item: PXLPhoto) {
        // by passing lifecycle to pxlPhotoProductView, the SDK will automatically start and stop the video
        pxlPhotoProductView.useLifecycleObserver(lifecycle)
        item.sourceUrl = URL("http://175.195.207.155/hls/${item.albumPhotoId}.m3u8")
        Log.e("livewView"," item.videoUrl: ${item.videoUrl}")
        // set your ui settings
        pxlPhotoProductView.setContent(photoInfo = PhotoWithVideoInfo(
                pxlPhoto = item,
                configuration = PXLPhotoView.Configuration().apply {
                    // Customize image size, not a video
                    pxlPhotoSize = PXLPhotoSize.ORIGINAL
                    // Customize image scale type
                    imageScaleType = ImageScaleType.CENTER_CROP
                    // Customize Main TextView
                    mainTextViewStyle = TextViewStyle().apply {
                        text = "Spring\nColors"
                        size = 30.px
                        sizeUnit = TypedValue.COMPLEX_UNIT_PX
                        typeface = null
                        textPadding = TextPadding(bottom = 30.px.toInt())
                    }
                    // Customize Sub TextView
                    subTextViewStyle = null // you can hide this view by giving it null
                    // Customize Button
                    buttonStyle = PXLPhotoView.ButtonStyle().apply {
                        text = "VER AHORA"
                        size = 12.px
                        sizeUnit = TypedValue.COMPLEX_UNIT_PX
                        typeface = null
                        buttonIcon = com.pixlee.pixleesdk.R.drawable.baseline_play_arrow_white_24
                        stroke = PXLPhotoView.Stroke().apply {
                            width = 1.px.toInt()
                            color = Color.WHITE
                            radiusInPixel = 25.px
                            padding = PXLPhotoView.Padding().apply {
                                left = 10.px.toInt()
                                centerRight = 20.px.toInt()
                                topBottom = 10.px.toInt()
                            }
                        }
                    }
                },
                isLoopingVideo = true,
                soundMuted = false
        ),
                headerConfiguration = PXLPhotoProductView.Configuration().apply {
                    backButton = PXLPhotoProductView.CircleButton().apply {
                        icon = com.pixlee.pixleesdk.R.drawable.round_close_black_18
                        iconColor = Color.BLACK
                        backgroundColor = Color.WHITE
                        padding = 10.px.toInt()
                        onClickListener = {
                            // back button's click effect
                            Toast.makeText(this@LiveViewerActivity, "Replace this with your codes, currently 'onBackPressed()'", Toast.LENGTH_LONG).show()
                            onBackPressed()
                        }
                    }
                    muteCheckBox = PXLPhotoProductView.MuteCheckBox().apply {
                        mutedIcon = com.pixlee.pixleesdk.R.drawable.outline_volume_up_black_18
                        unmutedIcon = com.pixlee.pixleesdk.R.drawable.outline_volume_off_black_18
                        iconColor = Color.BLACK
                        backgroundColor = Color.WHITE
                        padding = 10.px.toInt()
                        onCheckedListener = {
                            Toast.makeText(this@LiveViewerActivity, "is muted: $it", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                configuration = ProductViewHolder.Configuration().apply {
                    circleIcon = ProductViewHolder.CircleIcon().apply {
                        icon = R.drawable.outline_shopping_bag_black_24
                        iconColor = Color.DKGRAY
                        backgroundColor = ContextCompat.getColor(this@LiveViewerActivity, R.color.yellow_800)
                        padding = 5.px.toInt()
                    }
                    mainTextStyle = TextStyle().apply {
                        color = Color.BLACK
                        size = 14.px
                        sizeUnit = TypedValue.COMPLEX_UNIT_PX
                        typeface = null
                    }
                    subTextStyle = TextStyle().apply {
                        color = Color.BLACK
                        size = 12.px
                        sizeUnit = TypedValue.COMPLEX_UNIT_PX
                        typeface = null
                    }
                    bookmarkDrawable = ProductViewHolder.Bookmark().apply {
                        isVisible = true
                        selectedIcon = com.pixlee.pixleesdk.R.drawable.baseline_bookmark_black_36
                        unselectedIcon = com.pixlee.pixleesdk.R.drawable.baseline_bookmark_border_black_36
                    }
                    priceTextStyle = CurrencyTextStyle().apply {
                        defaultCurrency = "EUR" // or null
                        leftText = TextStyle().apply {
                            color = Color.BLACK
                            size = 24.px
                            sizeUnit = TypedValue.COMPLEX_UNIT_PX
                            typeface = null
                        }

                        rightText = TextStyle().apply {
                            color = Color.BLACK
                            size = 14.px
                            sizeUnit = TypedValue.COMPLEX_UNIT_PX
                            typeface = null
                        }
                    }
                },
                onProductClicked = {
                    Toast.makeText(this, "product clicked, product id: ${it.id}", Toast.LENGTH_SHORT).show()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.link.toString()))
                    startActivity(browserIntent)
                })
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
        fun launch(context: Context, pxlPhoto: PXLPhoto) {
            val i = Intent(context, LiveViewerActivity::class.java)
            i.putExtra("pxlPhoto", pxlPhoto)
            context.startActivity(i)
        }
    }
}
