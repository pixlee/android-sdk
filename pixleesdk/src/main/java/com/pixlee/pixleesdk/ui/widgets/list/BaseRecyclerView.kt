package com.pixlee.pixleesdk.ui.widgets.list

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.client.PXLAnalytics
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.enums.PXLWidgetType
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
     * this is for automatic Analytics event
     */
    var pxlKtxAlbum: PXLKtxAlbum? = null

    var pxlWidgetType: String? = null

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

        if (list.isNotEmpty()) {
            list.forEach {
                pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Content(it))
            }
            pxlPhotoAdapter.notifyDataSetChanged()
        }
        fireAnalytics()
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
        fireAnalytics()
    }

    /**
     * this let this view to fire 'VisibleWidget' and 'OpenedWidget' analytics events automatically for you.
     * @param pxlKtxAlbum: PXLKtxAlbum? Please pass the same reference that you make photoInfo: PhotoWithVideoInfo with
     * @param pxlWidgetType: PXLWidgetType
     * If you pass pxlKtxAlbum to this method, openLightbox analytics event will get fired automatically.
     */
    fun enableAutoAnalytics(pxlKtxAlbum: PXLKtxAlbum, pxlWidgetType: PXLWidgetType) {
        this.pxlKtxAlbum = pxlKtxAlbum
        this.pxlWidgetType = pxlWidgetType.type
        fireAnalytics()
    }

    /**
     * this let this view to fire 'VisibleWidget' and 'OpenedWidget' analytics events automatically for you.
     * If you pass pxlKtxAlbum to this method, openLightbox analytics event will get fired automatically.
     *
     * Note that if you need to pass region_id to analytics events, you can set yours to pxlKtxAlbum.params.regionId.
     * Then pass your pxlKtxAlbum to enableAutoAnalytics(...). That will add region_id to analytics events when they need to be fired.
     *
     * @param pxlKtxAlbum: PXLKtxAlbum? Please pass the same reference that you make photoInfo: PhotoWithVideoInfo with
     * @param pxlWidgetType: String
     */
    fun enableAutoAnalytics(pxlKtxAlbum: PXLKtxAlbum, pxlWidgetType: String) {
        this.pxlKtxAlbum = pxlKtxAlbum
        this.pxlWidgetType = pxlWidgetType
        fireAnalytics()
    }

    protected fun fireAnalytics() {
        fireAnalyticsOpenedWidget()
        fireAnalyticsVisibleWidget()
    }

    private var isAnalyticsOpenedWidgetFired: Boolean = false
    private fun fireAnalyticsOpenedWidget() {
        if (pxlKtxAlbum != null && !isAnalyticsOpenedWidgetFired) {
            if (pxlWidgetType == null) Log.e(PXLAnalytics.TAG, "can't fire OpenedWidget analytics event because pxlWidgetType is null")
            if (pxlPhotoAdapter.list.isNotEmpty() && visibility == View.VISIBLE) {
                isAnalyticsOpenedWidgetFired = true
                GlobalScope.launch {
                    pxlKtxAlbum?.also { album ->
                        pxlWidgetType?.also { pxlWidgetType ->
                            try {
                                album.openedWidget(pxlWidgetType)
                            } catch (e: Exception) {
                                isAnalyticsOpenedWidgetFired = false
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

        }
    }

    private var isAnalyticsVisibleWidgetFired: Boolean = false
    private fun fireAnalyticsVisibleWidget() {
        if (pxlKtxAlbum != null && !isAnalyticsVisibleWidgetFired) {
            if (pxlWidgetType == null) Log.e(PXLAnalytics.TAG, "can't fire WidgetVisible analytics event because pxlWidgetType is null")
            if (pxlPhotoAdapter.list.isNotEmpty() && isVisibleInScreen()) {
                isAnalyticsVisibleWidgetFired = true
                GlobalScope.launch {
                    pxlKtxAlbum?.also { album ->
                        pxlWidgetType?.also { pxlWidgetType ->
                            try {
                                album.widgetVisible(pxlWidgetType)
                            } catch (e: Exception) {
                                isAnalyticsVisibleWidgetFired = false
                                e.printStackTrace()
                            }
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