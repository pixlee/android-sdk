package com.pixlee.pixleesdk.ui.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixlee.pixleesdk.PXLProduct
import com.pixlee.pixleesdk.R
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

        tvMessage.text = product.title

        if (product.price != null) {
            tvPrice.text = if (product.currency != null && product.currency.isNotEmpty()) {
                product.currency + " " + formatter.format(product.price)
            } else {
                formatter.format(product.price)
            }
        } else {
            tvPrice.text = ""
        }

        bookmark.visibility = if (isBookmarked != null) View.VISIBLE else View.GONE
        if (isBookmarked != null) {
            bookmark.isChecked = isBookmarked
        }
    }

    companion object {
        fun create(parent: ViewGroup): ProductViewHolder {
            return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))
        }
    }
}
