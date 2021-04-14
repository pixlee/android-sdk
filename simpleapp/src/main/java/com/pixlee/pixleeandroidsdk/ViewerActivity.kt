package com.pixlee.pixleeandroidsdk

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.pixlee.pixleesdk.network.observer.AnalyticsObserver
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.ui.widgets.CurrencyTextStyle
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
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

        // this is an example of changing ImageScaleType
        item.configuration.imageScaleType = ImageScaleType.FIT_CENTER

        // give a padding to the top as much as the status bar's height
        pxlPhotoProductView.addPaddingToHeader(0, PXLViewUtil.getStatusBarHeight(this), 0, 0)

        // by passing lifecycle to pxlPhotoProductView, the SDK will automatically start and stop the video
        pxlPhotoProductView.useLifecycleObserver(lifecycle)

        // set your ui settings
        pxlPhotoProductView
                .setContent(photoInfo = item,
                        headerConfiguration = PXLPhotoProductView.Configuration().apply {
                            backButton = PXLPhotoProductView.CircleButton().apply {
                                onClickListener = {
                                    // back button's click effect
                                    onBackPressed()
                                }
                            }
                            muteCheckBox = PXLPhotoProductView.MuteCheckBox().apply {
                                onCheckedListener = {

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
                            }
                            subTextStyle = TextStyle().apply {
                                color = Color.BLACK
                                size = 12.px
                            }
                            priceTextStyle = CurrencyTextStyle().apply {
                                defaultCurrency = "EUR" // or null
                                leftText = TextStyle().apply {
                                    color = Color.BLACK
                                    size = 24.px
                                }
                                rightText = TextStyle().apply {
                                    color = Color.BLACK
                                    size = 14.px
                                }
                            }
                        },
                        onProductClicked = {
                            Toast.makeText(this, "product clicked, product id: ${it.id}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.link.toString())))
                        })
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
