package com.pixlee.pixleesdk.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder.Companion.create
import java.util.*

/**
 * This is to be used in PXLPhotoRecyclerView which extends RecyclerView
 */
class ProductAdapter(
        val configuration: ProductViewHolder.Configuration,
        val list: List<PXLProduct>,
                     /**
                      * String: product id
                      * Boolean: is bookmarked
                      */
                     val bookmarkMap: HashMap<String, Boolean>?,
                     val onBookmarkChanged: (productId: String, isBookmarkChecked: Boolean) -> Unit,
                     val onItemClicked: (product: PXLProduct) -> Unit) : RecyclerView.Adapter<ProductViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return create(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = list[position]
        if (bookmarkMap!=null) {
            holder.bind(product, bookmarkMap[product.id] ?: false, configuration)
        }else{
            holder.bind(product, null, configuration)
        }
        holder.binding.bookmark.setOnClickListener {
            val productId = list[holder.adapterPosition].id
            bookmarkMap?.also {
                val bookmarked = it[productId] ?: false
                it[productId] = !bookmarked
                onBookmarkChanged(list[holder.adapterPosition].id, !bookmarked)
                holder.changeBookmarkUI(!bookmarked, configuration.bookmarkDrawable)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClicked(list[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}