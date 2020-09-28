package com.pixlee.pixleesdk.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder.Companion.create
import kotlinx.android.synthetic.main.item_product.*
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
        holder.bind(product, bookmarkMap?.get(product.id), configuration)
        holder.bookmark.setOnClickListener {
            val productId = list[holder.adapterPosition].id
            bookmarkMap?.also {
                val bookmarked = it[productId] ?: false
                it[productId] = !bookmarked
                onBookmarkChanged(list[holder.adapterPosition].id, !bookmarked)
                holder.changeBookmarkUI(!bookmarked, configuration)
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