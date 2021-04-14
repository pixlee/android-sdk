package com.pixlee.pixleesdk.ui.widgets.list

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.client.PXLAlbum
import com.pixlee.pixleesdk.client.PXLAnalytics
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by sungjun on 9/17/20.
 */

open class BaseRecyclerView : RecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val pxlPhotoAdapter: PXLPhotoAdapter by lazy {
        PXLPhotoAdapter()
    }

    /**
     * Add a list: List<PhotoWithImageScaleType> to an existing list
     */
    open fun addList(list: List<PhotoWithImageScaleType>) {
        setList(ListAddType.ADD, list)
    }

    /**
     * Replace a list: List<PhotoWithImageScaleType> with an existing list
     */
    fun replaceList(list: List<PhotoWithImageScaleType>) {
        setList(ListAddType.REPLACE, list)
    }

    internal open fun setList(type: ListAddType, list: List<PhotoWithImageScaleType>) {
        clearOldList(type)

        var lastItem = pxlPhotoAdapter.list.lastOrNull()
        if(lastItem!=null && lastItem is PXLPhotoAdapter.Item.LoadMore){
            val position = pxlPhotoAdapter.list.count() - 1
            pxlPhotoAdapter.list.removeAt(position)
            pxlPhotoAdapter.notifyItemRemoved(position)
        } else {
            lastItem = null
        }
        if (list.isNotEmpty()) {
            val position = pxlPhotoAdapter.list.count()
            list.forEach {
                pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Content(it))
            }
            lastItem?.also {
                pxlPhotoAdapter.list.add(it)
            }
            pxlPhotoAdapter.notifyItemRangeInserted(position, pxlPhotoAdapter.list.count() - position)
        }
        fireOpenAndVisible()
    }

    internal fun clearOldList(type: ListAddType) {
        when (type) {
            ListAddType.REPLACE -> clearOldList()
            ListAddType.ADD -> { /* do nothing */
            }
        }
    }

    internal fun clearOldList() {
        if (pxlPhotoAdapter.list.isNotEmpty()) {
            pxlPhotoAdapter.list.clear()
            pxlPhotoAdapter.notifyDataSetChanged()
        }
    }

    internal enum class ListAddType {
        ADD, REPLACE
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("BaseRV", "onAttachedToWindow: $visibility")
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        Log.d("BaseRV", "onVisibilityChanged.visibility: $visibility")
//        printPosition()
    }


    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        Log.d("BaseRV", "onWindowVisibilityChanged.visibility: $visibility")
        fireOpenAndVisible()
    }
    
    class AlbumForAutoAnalytics(val album: PXLKtxAlbum, val widgetType: String)

    /**
     * If you pass PXLKtxAlbum to this method , 'VisibleWidget' and 'OpenedWidget' analytics events will get fired automatically when needed.
     *
     * Note that if you need to pass region_id to analytics events, you can set yours to pxlKtxAlbum.params.regionId.
     * Then pass your pxlKtxAlbum to enableAutoAnalytics(...). That will add region_id to analytics events when they need to be fired.
     *
     * @param pxlKtxAlbum: PXLKtxAlbum? Please pass the same reference that you make photoInfo: PhotoWithVideoInfo with
     * @param pxlWidgetType: String
     */
    var albumForAutoAnalytics: AlbumForAutoAnalytics? = null
        set(value) {
            field = value
            fireOpenAndVisible()
        }
    
    protected fun fireOpenAndVisible() {
        fireAnalyticsOpenedWidget()
        fireAnalyticsWidgetVisible()
    }

    private fun isAutoAnalyticsNeeded(): Boolean {
        return PXLClient.autoAnalyticsEnabled
    }

    private var isAnalyticsOpenedWidgetFired: Boolean = false
    private fun fireAnalyticsOpenedWidget() {
        if (isAutoAnalyticsNeeded() && !isAnalyticsOpenedWidgetFired) {
            if (albumForAutoAnalytics == null) {
                Log.e(PXLAnalytics.TAG, "can't fire OpenedWidget analytics event because albumForAutoAnalytics is null. Please pass your own AlbumForAutoAnalytics to albumForAutoAnalytics.")
                return
            }
            if (pxlPhotoAdapter.list.isNotEmpty() && visibility == View.VISIBLE) {
                isAnalyticsOpenedWidgetFired = true
                GlobalScope.launch {
                    albumForAutoAnalytics?.also {
                        try {
                            it.album.openedWidget(it.widgetType)
                        } catch (e: Exception) {
                            isAnalyticsOpenedWidgetFired = false
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private var isAnalyticsWidgetVisibleFired: Boolean = false
    private fun fireAnalyticsWidgetVisible() {
        if (isAutoAnalyticsNeeded() && !isAnalyticsWidgetVisibleFired) {
            if (albumForAutoAnalytics == null) {
                Log.e(PXLAnalytics.TAG, "can't fire WidgetVisible analytics event because albumForAutoAnalytics is null. Please pass your own AlbumForAutoAnalytics to albumForAutoAnalytics.")
                return
            }

            if (pxlPhotoAdapter.list.isNotEmpty() && isVisibleInScreen()) {
                isAnalyticsWidgetVisibleFired = true
                GlobalScope.launch {
                    albumForAutoAnalytics?.also {
                        try {
                            it.album.widgetVisible(it.widgetType)
                        } catch (e: Exception) {
                            isAnalyticsWidgetVisibleFired = false
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    fun isVisibleInScreen(): Boolean {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay

        val size = Point()
        display.getSize(size)

        val width: Int = size.x
        val height: Int = size.y

        // loc [0] is x position of this view on the physical screen
        // loc [1] is y position of this view on the physical screen
        val loc = IntArray(2)
        getLocationOnScreen(loc)

        Log.d("BaseRV", "onScrollChanged: $visibility, loc[0]: ${loc[0]}, loc[1]: ${loc[1]},   width: $width, height: $height")
        return visibility == View.VISIBLE && loc[0] in 0..width && loc[1] in 0..height
    }
}