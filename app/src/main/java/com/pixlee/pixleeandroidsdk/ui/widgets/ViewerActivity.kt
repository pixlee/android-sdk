package com.pixlee.pixleeandroidsdk.ui.widgets

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.network.observer.AnalyticsObserver
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.ui.widgets.CurrencyTextStyle
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoProductView
import com.pixlee.pixleesdk.ui.widgets.TextStyle
import com.pixlee.pixleesdk.util.PXLViewUtil
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.activity_viewer.*
import kotlinx.coroutines.launch
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
        pxlPhotoProductView.addPaddingToHeader(0, PXLViewUtil.getStatusBarHeight(this), 0, 0)

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

        listenAnalyticsForInstrumentTesting()
        init(item)
    }

    fun init(item: PhotoWithVideoInfo) {
        //item.configuration.imageScaleType = ImageScaleType.FIT_CENTER
        // set your ui settings
        pxlPhotoProductView
                .setContent(photoInfo = item,
                        showHotspots = true,
                        headerConfiguration = PXLPhotoProductView.Configuration().apply {
                            backButton = PXLPhotoProductView.CircleButton().apply {
                                icon = com.pixlee.pixleesdk.R.drawable.round_close_black_18
                                iconColor = Color.BLACK
                                backgroundColor = Color.WHITE
                                padding = 10.px.toInt()
                                onClickListener = {
                                    // back button's click effect
                                    Toast.makeText(this@ViewerActivity, "Replace this with your codes, currently 'onBackPressed()'", Toast.LENGTH_LONG).show()
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
                                    Toast.makeText(this@ViewerActivity, "is muted: $it", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        configuration = ProductViewHolder.Configuration().apply {
                            circleIcon = ProductViewHolder.CircleIcon().apply {
                                icon = R.drawable.outline_shopping_bag_black_24
                                iconColor = Color.DKGRAY
                                backgroundColor = ContextCompat.getColor(this@ViewerActivity, R.color.yellow_800)
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
                                selectedIcon = com.pixlee.pixleesdk.R.drawable.baseline_bookmark_black_36
                                unselectedIcon = com.pixlee.pixleesdk.R.drawable.baseline_bookmark_border_black_36
                            }
                            priceTextStyle = CurrencyTextStyle().apply {
                                isCurrencyLeading = true
                                defaultCurrency = "EUR" // or null
                                leftText = TextStyle().apply {
                                    color = Color.DKGRAY
                                    size = 24.px
                                    sizeUnit = TypedValue.COMPLEX_UNIT_PX
                                    typeface = null
                                }

                                rightText = TextStyle().apply {
                                    color = Color.DKGRAY
                                    size = 14.px
                                    sizeUnit = TypedValue.COMPLEX_UNIT_PX
                                    typeface = null
                                }
                            }
                            discountPriceTextStyle = ProductViewHolder.DiscountPrice(
                                    discountLayout = ProductViewHolder.DiscountLayout.WITH_DISCOUNT_LABEL,
                                    priceTextStyle = CurrencyTextStyle().apply {
                                        isCurrencyLeading = true
                                        defaultCurrency = "EUR" // or null
                                        leftText = TextStyle().apply {
                                            color = Color.RED
                                            size = 24.px
                                            sizeUnit = TypedValue.COMPLEX_UNIT_PX
                                            typeface = null
                                        }

                                        rightText = TextStyle().apply {
                                            color = Color.RED
                                            size = 14.px
                                            sizeUnit = TypedValue.COMPLEX_UNIT_PX
                                            typeface = null
                                        }
                                    }
                            )
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

    fun listenAnalyticsForInstrumentTesting() {
        lifecycleScope.launch {
            AnalyticsObserver.observe("Obsev.ViewerActivity", tvDebugTextViewer)
        }
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
