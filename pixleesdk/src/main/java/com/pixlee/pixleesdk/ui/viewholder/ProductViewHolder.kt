package com.pixlee.pixleesdk.ui.viewholder

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.icu.math.BigDecimal
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
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
import com.pixlee.pixleesdk.ui.widgets.CurrencyTextStyle
import com.pixlee.pixleesdk.ui.widgets.TextStyle
import com.pixlee.pixleesdk.ui.widgets.setTextStyle
import com.pixlee.pixleesdk.util.getCurrencySymbol
import com.pixlee.pixleesdk.util.getFractionalPart
import com.pixlee.pixleesdk.util.px
import com.pixlee.pixleesdk.util.setCompatColorFilter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.*
import java.text.DecimalFormat


/**
 * This is to display PXLProduct as a RecyclerView.ViewHolder
 */
class ProductViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    class Configuration(
            var mainTextStyle: TextStyle? = null,
            var subTextStyle: TextStyle? = null,
            var priceTextStyle: CurrencyTextStyle? = null,
            var bookmarkDrawable: Bookmark = Bookmark(),
            var circleIcon: CircleIcon = CircleIcon(),
            var discountPrice: DiscountPrice? = null
    )

    /**
     * Discount Badges
     */
    class DiscountPrice(var discountLayout: DiscountLayout)

    enum class DiscountLayout {
        CROSS_THROUGH, WAS_OLD_PRICE, WITH_DISCOUNT_LABEL
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
        product.price ?: 0.toBigDecimal().let { price ->
            val integerPrice = formatter.format(price.setScale(0, BigDecimal.ROUND_FLOOR))
            val symbol = product.getCurrencySymbol(configuration.priceTextStyle?.defaultCurrency)
            val decimalPrice = if (symbol != null) {
                price.getFractionalPart() + " " + symbol
            } else {
                price.getFractionalPart()
            }

            val total = "$integerPrice$decimalPrice"
            tvPrice.text = SpannableString(total).apply {
                val metrics = tvSub.context.resources.displayMetrics
                configuration.priceTextStyle?.leftText?.also { textStyle ->
                    applyTextStyle(total, integerPrice, textStyle, metrics)
                }
                configuration.priceTextStyle?.rightText?.also { textStyle ->
                    applyTextStyle(total, decimalPrice, textStyle, metrics)
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
        fun create(parent: ViewGroup): ProductViewHolder {
            return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))
        }
    }
}

fun SpannableString.applyTextStyle(total:String, targetText: String, targetTextStyle: TextStyle, metrics: DisplayMetrics) {
    total.indexOf(targetText).let { index ->
        val textLength = targetText.length
        targetTextStyle?.also {
            val fontSize = TypedValue.applyDimension(it.sizeUnit, it.size, metrics).toInt()
            setSpan(AbsoluteSizeSpan(fontSize), index, index + textLength, 0); // set size
            setSpan(ForegroundColorSpan(it.color), index, index + textLength, 0);// set color
        }
    }
}