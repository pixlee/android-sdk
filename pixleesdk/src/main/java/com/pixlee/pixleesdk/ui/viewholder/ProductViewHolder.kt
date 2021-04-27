package com.pixlee.pixleesdk.ui.viewholder

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder.Companion.SALES_TEXT_RATIO
import com.pixlee.pixleesdk.ui.widgets.CurrencyTextStyle
import com.pixlee.pixleesdk.ui.widgets.TextStyle
import com.pixlee.pixleesdk.ui.widgets.setTextStyle
import com.pixlee.pixleesdk.util.ExtendedCurrency
import com.pixlee.pixleesdk.util.getFractionalPart
import com.pixlee.pixleesdk.util.px
import com.pixlee.pixleesdk.util.setCompatColorFilter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.*
import java.math.BigDecimal
import java.text.DecimalFormat


/**
 * This is to display PXLProduct as a RecyclerView.ViewHolder
 */
class ProductViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    class Configuration(
            var mainTextStyle: TextStyle? = null,
            var subTextStyle: TextStyle? = null,
            var priceTextStyle: CurrencyTextStyle? = null,
            var discountPriceTextStyle: DiscountPrice? = null, // Optional, if you'd like to display sales price information, you can set DiscountPrice(...) to discountPriceTextStyle
            var bookmarkDrawable: Bookmark = Bookmark(),
            var circleIcon: CircleIcon = CircleIcon()
    )

    /**
     * Discount Badges
     */
    class DiscountPrice(var discountLayout: DiscountLayout, var priceTextStyle: CurrencyTextStyle? = null)

    enum class DiscountLayout {
        CROSS_THROUGH, // screenshot: https://xd.adobe.com/view/af65a724-66c0-4d78-bf8c-7e860a2b7595-fa36/screen/c5fad7cd-a861-415f-8916-cabf8b50f32b/
        WAS_OLD_PRICE, // screenshot: https://xd.adobe.com/view/af65a724-66c0-4d78-bf8c-7e860a2b7595-fa36/screen/21486793-b111-47ab-8029-038ee1544818/
        WITH_DISCOUNT_LABEL // screenshot: https://xd.adobe.com/view/af65a724-66c0-4d78-bf8c-7e860a2b7595-fa36/screen/ec1004e6-d7ad-4d7a-92e2-4fcbb26877eb/
    }

    class Bookmark(var isVisible: Boolean = true,
                   @DrawableRes var selectedIcon: Int = R.drawable.baseline_bookmark_black_36,
                   @DrawableRes var unselectedIcon: Int = R.drawable.baseline_bookmark_border_black_36,
                   var filterColor: FilterColor? = null
    ) {
        class FilterColor(@ColorInt var selectedColor: Int,
                          @ColorInt var unselectedColor: Int)
    }

    class CircleIcon(
            var isVisible: Boolean = true,
            @DrawableRes var icon: Int = R.drawable.baseline_bookmark_black_36,
            @ColorInt var iconColor: Int = Color.WHITE,
            @ColorInt var backgroundColor: Int = Color.YELLOW,
            var padding: Int = 5.px.toInt()
    )

    var formatter = DecimalFormat("#,##0.##")
    fun bind(product: PXLProduct, isBookmarked: Boolean?, configuration: Configuration) {
        // product image UI
        Glide.with(imageView.context)
                .load(product.imageThumb)
                .fitCenter()
                .into(imageView)

        // main text UI
        configuration.mainTextStyle?.also { tvMain.setTextStyle(it) }
        tvMain.text = product.title

        // sub text UI
        configuration.subTextStyle?.also { tvMain.setTextStyle(it) }
        tvSub.visibility = if (product.description != null && product.description.isNotEmpty()) View.VISIBLE else View.GONE
        tvSub.text = product.description

        // price UI
        (product.price ?: 0.toBigDecimal()).let { price ->
            class Price(val integerPrice: String, val decimalPrice: String)

            fun getPrice(productPrice: BigDecimal, textStyle: CurrencyTextStyle?, productCurrency: String?): Price {
                val currencySymbol = ExtendedCurrency.getCurrencyByISO(productCurrency
                        ?: textStyle?.defaultCurrency ?: "").let {
                    it?.symbol ?: productCurrency ?: ""
                }
                val isCurrencyLeading = textStyle?.isCurrencyLeading ?: false
                val space = if (product.hasAvailableSalesPrice()) "" else " "
                val integerPrice = (if (isCurrencyLeading) "$currencySymbol$space" else "") + formatter.format(productPrice.setScale(0, BigDecimal.ROUND_FLOOR))
                val decimalPrice = productPrice.getFractionalPart() + (if (!isCurrencyLeading) "$space$currencySymbol" else "")
                return Price(integerPrice = integerPrice, decimalPrice = decimalPrice)
            }

            val defaultPrice: Price = getPrice(price, configuration.priceTextStyle, product.currency)
            val salesPrice: Price? = product.salesPrice?.let { salesPrice ->
                if (configuration.discountPriceTextStyle != null && product.hasAvailableSalesPrice()) {
                    getPrice(salesPrice, configuration.discountPriceTextStyle?.priceTextStyle, product.currency)
                } else {
                    null
                }
            }

            val salesIntegerPrice = salesPrice?.integerPrice ?: ""
            val salesDecimalPrice = salesPrice?.decimalPrice ?: ""
            val defaultPrinceString = "${defaultPrice.integerPrice}${defaultPrice.decimalPrice}"
            val noSalesPrice = salesIntegerPrice.isEmpty() && salesDecimalPrice.isEmpty()
            val discountLayout = configuration.discountPriceTextStyle?.discountLayout
            var offLable = ""
            val total = if (!noSalesPrice && discountLayout!=null) {
                val discountPercentage = (1.toBigDecimal() - (price / product.salesPrice)) * 100.toBigDecimal()
                offLable = if(discountLayout==DiscountLayout.WITH_DISCOUNT_LABEL) " ${tvSub.context.getString(R.string.percent_off, "$discountPercentage")}" else ""
                "$salesIntegerPrice$salesDecimalPrice $defaultPrinceString$offLable"
            } else {
                defaultPrinceString
            }

            tvPrice.text = SpannableString(total).apply {
                val metrics = tvSub.context.resources.displayMetrics
                var newIndex = 0

                // Draw integer sales price
                if (salesIntegerPrice.isNotEmpty()) {
                    configuration.discountPriceTextStyle?.priceTextStyle?.leftText?.also { textStyle ->
                        val oldIndex = 0
                        newIndex += salesIntegerPrice.length
                        applyTextStyle(oldIndex, newIndex, textStyle, metrics)
                    }
                }

                // Draw decimal sales price
                if (salesDecimalPrice.isNotEmpty()) {
                    configuration.discountPriceTextStyle?.priceTextStyle?.rightText?.also { textStyle ->
                        val oldIndex = newIndex
                        newIndex += salesDecimalPrice.length
                        applyTextStyle(oldIndex, newIndex, textStyle, metrics)
                    }
                }

                // Draw integer default price
                configuration.priceTextStyle?.leftText?.also { textStyle ->
                    val spaceIndex = if (noSalesPrice) 0 else 1
                    val oldIndex = spaceIndex + newIndex
                    newIndex += spaceIndex + defaultPrice.integerPrice.length
                    if (noSalesPrice) applyTextStyle(oldIndex, newIndex, textStyle, metrics)
                    else applyDisabledTextStyle(oldIndex, newIndex, textStyle, metrics)
                }

                // Draw decimal default price
                (if (noSalesPrice) configuration.priceTextStyle?.rightText else configuration.priceTextStyle?.leftText)?.also { textStyle ->
                    val oldIndex = newIndex
                    newIndex += defaultPrice.decimalPrice.length
                    if (noSalesPrice) applyTextStyle(oldIndex, newIndex, textStyle, metrics)
                    else applyDisabledTextStyle(oldIndex, newIndex, textStyle, metrics)
                }

                // Draw off lable
                if (offLable.isNotEmpty()) {
                    configuration.priceTextStyle?.leftText?.also{ mainTextStyle ->
                        configuration.discountPriceTextStyle?.priceTextStyle?.rightText?.also { discountTextStyle ->
                            val oldIndex = newIndex
                            newIndex += offLable.length
                            val fontSize = (TypedValue.applyDimension(mainTextStyle.sizeUnit, mainTextStyle.size, metrics) * SALES_TEXT_RATIO).toInt()
                            setSpan(AbsoluteSizeSpan(fontSize), oldIndex, newIndex, 0) // font size
                            setSpan(ForegroundColorSpan(lighten(discountTextStyle.color, .25)), oldIndex, newIndex, 0) // font color
                        }
                    }

                }

            }
        }

        // bookmark UI
        bookmark.visibility = if (configuration.bookmarkDrawable.isVisible) View.VISIBLE else View.GONE
        if (isBookmarked != null) {
            changeBookmarkUI(isBookmarked, configuration)
        }

        // shop image UI
        iconBox.visibility = if (configuration.circleIcon.isVisible) View.VISIBLE else View.GONE
        iconBox.background = getDrawable(configuration)
        iconBox.setPadding(configuration.circleIcon.padding, configuration.circleIcon.padding, configuration.circleIcon.padding, configuration.circleIcon.padding)
        ivIcon.setBackgroundResource(configuration.circleIcon.icon)
        ivIcon.setCompatColorFilter(configuration.circleIcon.iconColor)
    }

    var drawable: GradientDrawable = GradientDrawable()
    fun getDrawable(configuration: Configuration): GradientDrawable {
        drawable?.apply {
            shape = android.graphics.drawable.GradientDrawable.OVAL
            setColor(configuration.circleIcon.backgroundColor)
        }
        return drawable
    }

    fun changeBookmarkUI(isBookmarked: Boolean, configuration: Configuration) {
        bookmark.setBackgroundResource(when (isBookmarked) {
            false -> configuration.bookmarkDrawable.unselectedIcon
            true -> configuration.bookmarkDrawable.selectedIcon
        })

        val filter = configuration.bookmarkDrawable.filterColor
        if (filter == null) {
            bookmark.setCompatColorFilter(null)
        } else {
            bookmark.setCompatColorFilter(when (isBookmarked) {
                false -> filter.unselectedColor
                true -> filter.selectedColor
            })
        }
    }

    companion object {
        const val SALES_TEXT_RATIO = 0.75
        fun create(parent: ViewGroup): ProductViewHolder {
            return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))
        }
    }
}

fun SpannableString.applyTextStyle(startIndex: Int, endIndex: Int, targetTextStyle: TextStyle, metrics: DisplayMetrics) {
    targetTextStyle.also {
        val fontSize = TypedValue.applyDimension(it.sizeUnit, it.size, metrics).toInt()
        setSpan(AbsoluteSizeSpan(fontSize), startIndex, endIndex, 0); // font size
        setSpan(ForegroundColorSpan(it.color), startIndex, endIndex, 0);// font color
    }
}

fun SpannableString.applyDisabledTextStyle(startIndex: Int, endIndex: Int, targetTextStyle: TextStyle, metrics: DisplayMetrics) {
    targetTextStyle.also {
        val fontSize = (TypedValue.applyDimension(it.sizeUnit, it.size, metrics) * SALES_TEXT_RATIO).toInt()
        setSpan(AbsoluteSizeSpan(fontSize), startIndex, endIndex, 0) // font size
        setSpan(ForegroundColorSpan(lighten(it.color, .25)), startIndex, endIndex, 0) // font color
        setSpan(StrikethroughSpan(), startIndex, endIndex, 0) // font size

    }
}

fun lighten(@ColorInt color: Int, fraction: Double): Int {
    var red = Color.red(color).let { if (it == 0) 1 else it }
    var green = Color.green(color).let { if (it == 0) 1 else it }
    var blue = Color.blue(color).let { if (it == 0) 1 else it }
    fun lightenColor(color: Int, fraction: Double): Int {
        return Math.min(color + ((255.0 - color) * fraction), 255.0).toInt()
    }
    red = lightenColor(red, fraction)
    green = lightenColor(green, fraction)
    blue = lightenColor(blue, fraction)
    val alpha = Color.alpha(color)
    return Color.argb(alpha, red, green, blue)
}