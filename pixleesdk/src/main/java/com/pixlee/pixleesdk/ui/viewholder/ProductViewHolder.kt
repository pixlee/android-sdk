package com.pixlee.pixleesdk.ui.viewholder

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.ui.widgets.TextStyle
import com.pixlee.pixleesdk.ui.widgets.setTextStyle
import com.pixlee.pixleesdk.util.px
import com.pixlee.pixleesdk.util.setCompatColorFilter
import kotlinx.android.extensions.LayoutContainer
import java.text.DecimalFormat

import kotlinx.android.synthetic.main.item_product.*

/**
 * Created by sungjun on 9/11/20.
 */
class ProductViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    class Configuration(
            var mainTextStyle: TextStyle? = null,
            var subTextStyle: TextStyle? = null,
            var priceTextStyle: TextStyle? = null,
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
            var isVisible:Boolean = true,
            @DrawableRes var icon: Int = R.drawable.baseline_bookmark_black_36,
            @ColorInt var iconColor: Int = Color.WHITE,
            @ColorInt var backgroundColor: Int = Color.YELLOW,
            var padding: Int = 5.px.toInt()
    )

    var formatter = DecimalFormat("#,##0.##")
    fun bind(product: PXLProduct, isBookmarked: Boolean?, configuration: Configuration) {
        Glide.with(imageView.context)
                .load(product.imageThumb)
                .centerCrop()
                .into(imageView)

        tvMain.text = product.title
        configuration.mainTextStyle?.also { tvMain.setTextStyle(it) }

        tvSub.visibility = if (product.description != null && product.description.isNotEmpty()) View.VISIBLE else View.GONE
        tvSub.text = product.description

        configuration.subTextStyle?.also { tvMain.setTextStyle(it) }

        configuration.priceTextStyle?.also { tvPrice.setTextStyle(it) }
        tvPrice.text = if (product.price == null) {
            ""
        } else if (product.currency != null && product.currency.isNotEmpty()) {
            product.currency + " " + formatter.format(product.price)
        } else {
            formatter.format(product.price)
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
