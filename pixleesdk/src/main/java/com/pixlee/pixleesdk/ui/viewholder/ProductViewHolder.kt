package com.pixlee.pixleesdk.ui.viewholder

import android.graphics.Color
import android.graphics.PorterDuff
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixlee.pixleesdk.PXLProduct
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.util.setCompatColorFilter
import kotlinx.android.extensions.LayoutContainer
import java.text.DecimalFormat

import kotlinx.android.synthetic.main.item_product.*

/**
 * Created by sungjun on 9/11/20.
 */
class ProductViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    var formatter = DecimalFormat("#,##0.##")
    fun bind(product: PXLProduct, isBookmarked: Boolean?) {
        Glide.with(imageView.context)
                .load(product.imageThumb)
                .centerCrop()
                .into(imageView)

        tvMain.text = product.title

        //tvSub.visibility = if (product.description != null && product.description.isNotEmpty()) View.VISIBLE else View.GONE
        tvSub.text = "product.description"

        tvPrice.text = if (product.price == null) {
            ""
        } else if (product.currency != null && product.currency.isNotEmpty()) {
            product.currency + " " + formatter.format(product.price)
        } else {
            formatter.format(product.price)
        }

        bookmark.visibility = if (isBookmarked != null) View.VISIBLE else View.GONE

        if (isBookmarked != null) {
            changeBookmarkUI(isBookmarked)
        }
    }

    fun changeBookmarkUI(isBookmarked: Boolean) {
        bookmark.setBackgroundResource(when (isBookmarked) {
            false -> R.drawable.baseline_bookmark_border_black_36
            true -> R.drawable.baseline_bookmark_black_36
        })

        bookmark.setCompatColorFilter(ContextCompat.getColor(containerView.context, when (isBookmarked) {
            false -> R.color.bookmarkUnselected
            true -> R.color.bookmarkSelected
        }))
    }

    companion object {
        fun create(parent: ViewGroup): ProductViewHolder {
            return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))
        }
    }
}
