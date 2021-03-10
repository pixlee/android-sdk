package com.pixlee.pixleesdk.ui.viewholder

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.icu.math.BigDecimal
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.data.PXLVideoTimestamp
import com.pixlee.pixleesdk.ui.widgets.*
import com.pixlee.pixleesdk.util.getCurrencySymbol
import com.pixlee.pixleesdk.util.getFractionalPart
import com.pixlee.pixleesdk.util.px
import com.pixlee.pixleesdk.util.setCompatColorFilter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.*
import java.text.DecimalFormat
import java.util.*


/**
 * This is to display PXLProduct as a RecyclerView.ViewHolder
 */
class ProductViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    class Configuration(
            var mainTextStyle: TextStyle? = null,
            var subTextStyle: TextStyle? = null,
            var priceTextStyle: CurrencyTextStyle? = null,
            var videoTimestampTextViewStyle: TextViewStyle? = TextViewStyle(text = "", textPadding = TextPadding()),
            var bookmarkDrawable: Bookmark = Bookmark(),
            var circleIcon: CircleIcon = CircleIcon()
    )

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
    fun bind(product: PXLProduct, isBookmarked: Boolean?, configuration: Configuration, videoTimestampMap: HashMap<String, PXLVideoTimestamp>) {
        Glide.with(imageView.context)
                .load(product.imageThumb)
                .fitCenter()
                .into(imageView)

        configuration.mainTextStyle?.also { tvMain.setTextStyle(it) }
        tvMain.text = product.title

        configuration.subTextStyle?.also { tvMain.setTextStyle(it) }
        tvSub.visibility = if (product.description != null && product.description.isNotEmpty()) View.VISIBLE else View.GONE
        tvSub.text = product.description


        configuration.priceTextStyle?.leftText?.also { tvPriceLeft.setTextStyle(it) }
        configuration.priceTextStyle?.rightText?.also { tvPriceRight.setTextStyle(it) }
        val price = product.price ?: 0.toBigDecimal()
        tvPriceLeft.text = formatter.format(price.setScale(0, BigDecimal.ROUND_FLOOR))
        tvPriceRight.text = if (price == null) {
            ""
        } else {
            val symbol = product.getCurrencySymbol(configuration.priceTextStyle?.defaultCurrency)
            if (symbol != null) {
                price.getFractionalPart() + " " + symbol
            } else {
                price.getFractionalPart()
            }
        }

        configuration.videoTimestampTextViewStyle?.also { tvVideoTimestamp.setTextStyle(it) }
        val videoTimestamp = videoTimestampMap[product.id]
        val videoTimestampText = if (videoTimestamp != null) {
            String.format(Locale.US, "${configuration.videoTimestampTextViewStyle?.text ?: ""}%02d:%02d", videoTimestamp.timestamp / 60, videoTimestamp.timestamp % 60)
        } else {
            ""
        }

        tvVideoTimestamp.text = SpannableString(videoTimestampText).apply {
            setSpan(UnderlineSpan(), 0, length, 0)
        }

        bookmark.visibility = if (configuration.bookmarkDrawable.isVisible) View.VISIBLE else View.GONE

        if (isBookmarked != null) {
            changeBookmarkUI(isBookmarked, configuration)
        }

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
