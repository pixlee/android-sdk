package com.pixlee.pixleeandroidsdk.ui.uioptions

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.ViewerActivity
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.adapter.ProductAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.ui.widgets.*
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.fragment_product_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * To show you snapshot response data, this uses a mocked server and the content of the photos and the products in this demo is from a json file stored in this project.
 */
class ProductViewFragment : BaseFragment() {
    val mockAlbumUtil by lazy {
        MockAlbumUtil()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mockAlbumUtil.release()
    }

    override fun getTitleResource(): Int {
        return R.string.title_product_view
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnProductView.setOnClickListener {
            openViwerActivity()
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                mockAlbumUtil.setupMockedWebServer()
                mockAlbumUtil.intMockServer(context, "pxl_product.json")
            }

            val result = mockAlbumUtil.album.getFirstPage()
            val products = result.photos.firstOrNull()?.products ?: emptyList()
            rvOption1.adapter = makeProducts(ProductViewHolder.DiscountLayout.CROSS_THROUGH, products)
            rvOption2.adapter = makeProducts(ProductViewHolder.DiscountLayout.WAS_OLD_PRICE, products)
            rvOption3.adapter = makeProducts(ProductViewHolder.DiscountLayout.WITH_DISCOUNT_LABEL, products)
            rvOption4.adapter = makeProducts(null, products)
            rvOption1Icon.adapter = makeProducts(ProductViewHolder.DiscountLayout.CROSS_THROUGH, products, showShopIcon = true)
            rvOption2Icon.adapter = makeProducts(ProductViewHolder.DiscountLayout.WAS_OLD_PRICE, products, showShopIcon = true)
            rvOption3Icon.adapter = makeProducts(ProductViewHolder.DiscountLayout.WITH_DISCOUNT_LABEL, products, showShopIcon = true)
            rvOption4Icon.adapter = makeProducts(null, products, showShopIcon = true)
            rvOption1All.adapter = makeProducts(ProductViewHolder.DiscountLayout.CROSS_THROUGH, products, showBookmarkIcon = true, showShopIcon = true)
            rvOption2All.adapter = makeProducts(ProductViewHolder.DiscountLayout.WAS_OLD_PRICE, products, showBookmarkIcon = true, showShopIcon = true)
            rvOption3All.adapter = makeProducts(ProductViewHolder.DiscountLayout.WITH_DISCOUNT_LABEL, products, showBookmarkIcon = true, showShopIcon = true)
            rvOption4All.adapter = makeProducts(null, products, showBookmarkIcon = true, showShopIcon = true)
        }
    }

    val configuration by lazy {
        ProductViewHolder.Configuration().apply {
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
        }
    }

    fun makeProducts(discountLayout: ProductViewHolder.DiscountLayout?, products: List<PXLProduct>, showBookmarkIcon: Boolean = false, showShopIcon: Boolean = false): ProductAdapter {
        return ProductAdapter(
                configuration = configuration.copy().apply {
                    if (discountLayout != null)
                        discountPriceTextStyle = ProductViewHolder.DiscountPrice(
                                discountLayout = discountLayout,
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
                    else {
                        discountPriceTextStyle = null
                    }

                    if (showBookmarkIcon) {
                        bookmarkDrawable = ProductViewHolder.Bookmark().apply {
                            selectedIcon = com.pixlee.pixleesdk.R.drawable.baseline_bookmark_black_36
                            unselectedIcon = com.pixlee.pixleesdk.R.drawable.baseline_bookmark_border_black_36
                        }
                    }
                    if (showShopIcon) {
                        circleIcon = ProductViewHolder.CircleIcon().apply {
                            icon = R.drawable.outline_shopping_bag_black_24
                            iconColor = Color.DKGRAY
                            backgroundColor = ContextCompat.getColor(context!!, R.color.yellow_800)
                            padding = 5.px.toInt()
                        }
                    }
                },
                list = products,
                bookmarkMap = HashMap(),
                onBookmarkChanged = { productId, isBookmarkChecked ->
                    // write your own codes
                },
                onItemClicked = { product ->
                    // write your own codes
                }
        )
    }

    fun openViwerActivity() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                mockAlbumUtil.intMockServer(context, "pxl_product.json")
            }

            val result = mockAlbumUtil.album.getFirstPage()
            val info = PhotoWithVideoInfo(pxlPhoto = result.photos.first()!!,
                    configuration = PXLPhotoView.Configuration().apply {
                        // Customize image size, not a video
                        pxlPhotoSize = PXLPhotoSize.ORIGINAL
                        // Cystomize scale type
                        imageScaleType = ImageScaleType.CENTER_CROP
                        // Customize Main TextView
                        mainTextViewStyle = TextViewStyle().apply {
                            text = "Main Text"
                            size = 30.px
                            sizeUnit = TypedValue.COMPLEX_UNIT_PX
                            typeface = null
                        }
                        // Customize Sub TextView
                        subTextViewStyle = TextViewStyle().apply {
                            text = "Sub Text"
                            size = 18.px
                            sizeUnit = TypedValue.COMPLEX_UNIT_PX
                            typeface = null
                        }
                        // Customize Button
                        buttonStyle = PXLPhotoView.ButtonStyle().apply {
                            text = "Action Button"
                            size = 20.px
                            sizeUnit = TypedValue.COMPLEX_UNIT_PX
                            typeface = null
                            buttonIcon = com.pixlee.pixleesdk.R.drawable.baseline_play_arrow_white_24
                            stroke = PXLPhotoView.Stroke().apply {
                                width = 2.px.toInt()
                                color = Color.WHITE
                                radiusInPixel = 25.px
                                padding = PXLPhotoView.Padding().apply {
                                    left = 20.px.toInt()
                                    centerRight = 40.px.toInt()
                                    topBottom = 10.px.toInt()
                                }
                            }
                        }
                    },
                    isLoopingVideo = true,
                    soundMuted = true)
            ViewerActivity.launch(context!!, info)
        }
    }

    companion object {
        fun getInstance(openWidgets: Boolean): Fragment {
            val f: Fragment = ProductViewFragment()
            val bundle = Bundle()
            bundle.putBoolean("openWidgets", openWidgets)
            f.arguments = bundle
            return f
        }
    }
}
