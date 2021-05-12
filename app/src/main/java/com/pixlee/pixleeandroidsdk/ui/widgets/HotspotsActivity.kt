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
import com.pixlee.pixleeandroidsdk.ui.uioptions.MockAlbumUtil
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.ui.widgets.*
import com.pixlee.pixleesdk.util.PXLViewUtil
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.activity_viewer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


/**
 * Created by sungjun on 9/11/20.
 */
/**
 * This shows how to play the video and its product list
 */
class HotspotsActivity : AppCompatActivity() {
    val mockAlbumUtil by lazy {
        MockAlbumUtil()
    }

    override fun onDestroy() {
        super.onDestroy()
        mockAlbumUtil.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewer)

        // set a full screen mode
        PXLViewUtil.expandContentAreaOverStatusBar(this)

        // give a padding to the top as much as the status bar's height
        pxlPhotoProductView.addPaddingToHeader(0, PXLViewUtil.getStatusBarHeight(this), 0, 0)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                mockAlbumUtil.setupMockedWebServer()
                mockAlbumUtil.intMockServer(this@HotspotsActivity, "pxl_product_with_hotspots_and_video.json")
            }

            val result = mockAlbumUtil.album.getFirstPage()
            val item = PhotoWithVideoInfo(pxlPhoto = result.photos.filter { !it.isVideo }[4],
                    configuration = PXLPhotoView.Configuration().apply {
                        // Customize image size, not a video
                        pxlPhotoSize = PXLPhotoSize.ORIGINAL
                        // Cystomize scale type
                        imageScaleType = ImageScaleType.FIT_CENTER
                    },
                    isLoopingVideo = true,
                    soundMuted = true)
            init(item)
        }
    }

    fun init(item: PhotoWithVideoInfo) {
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
                                    Toast.makeText(this@HotspotsActivity, "Replace this with your codes, currently 'onBackPressed()'", Toast.LENGTH_LONG).show()
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
                                    Toast.makeText(this@HotspotsActivity, "is muted: $it", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        configuration = ProductViewHolder.Configuration().apply {
                            circleIcon = ProductViewHolder.CircleIcon().apply {
                                icon = R.drawable.outline_shopping_bag_black_24
                                iconColor = Color.DKGRAY
                                backgroundColor = ContextCompat.getColor(this@HotspotsActivity, R.color.yellow_800)
                                padding = 5.px.toInt()
                            }
                            mainTextStyle = TextStyle().apply {
                                color = Color.BLACK
                                size = 16.px
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


    companion object {
        // start video view with a photo data
        fun launch(context: Context) {
            val i = Intent(context, HotspotsActivity::class.java)
            context.startActivity(i)
        }
    }
}
